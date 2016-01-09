/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mobile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.widget.TextView;

import com.alorma.github.emoji.EmojiBitmapLoader;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.gh4a.utils.FileUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Original source https://github.com/github/android/blob/master/app/src/main/java/com/github/mobile/util/HttpImageGetter.java
 * Getter for an image
 */
public class HttpImageGetter implements ImageGetter {

    private final Context context;
    private final Map<Object, CharSequence> rawHtmlCache = new HashMap<>();
    private final Map<Object, CharSequence> fullHtmlCache = new HashMap<>();
    private RepoInfo repoInfo;
    private LoadingImageGetter loading;
    private File dir;

    private int width;
    private int height;
    private ArrayList<WeakReference<Bitmap>> loadedBitmaps;
    private boolean destroyed;

    /**
     * Create image getter for context
     */
    public HttpImageGetter(Context context) {
        this.context = context;
        if (context != null) {
            dir = context.getCacheDir();

            width = context.getResources().getDisplayMetrics().widthPixels / 2;
            height = context.getResources().getDisplayMetrics().heightPixels / 2;

            loadedBitmaps = new ArrayList<>();
            loading = new LoadingImageGetter(context, 24);
        }
    }

    private static boolean containsImages(final String html) {
        return html.contains("<img");
    }

    public void destroy() {
        for (WeakReference<Bitmap> ref : loadedBitmaps) {
            Bitmap bitmap = ref.get();
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        destroyed = true;
    }

    private HttpImageGetter show(final TextView view, final CharSequence html) {
        if (TextUtils.isEmpty(html)) return hide(view);

        view.setText(html);

        new EmojiBitmapLoader().parseTextView(view);

        view.setVisibility(VISIBLE);
        view.setTag(null);
        return this;
    }

    private HttpImageGetter hide(final TextView view) {
        view.setText(null);
        view.setVisibility(GONE);
        view.setTag(null);
        return this;
    }

    /**
     * Encode given HTML string and map it to the given id
     *
     * @return this image getter
     */
    public void encode(final Object id, final String html) {
        if (TextUtils.isEmpty(html)) return;

        CharSequence encoded = HtmlUtils.encode(html, loading);
        // Use default encoding if no img tags
        if (containsImages(html)) {
            rawHtmlCache.put(id, encoded);
        } else {
            rawHtmlCache.remove(id);
            fullHtmlCache.put(id, encoded);
        }
    }

    /**
     * Bind text view to HTML string
     *
     * @return this image getter
     */
    public HttpImageGetter bind(final TextView view, final String html, final Object id) {
        if (TextUtils.isEmpty(html)) return hide(view);

        CharSequence encoded = fullHtmlCache.get(id);
        if (encoded != null) return show(view, encoded);

        encoded = rawHtmlCache.get(id);
        if (encoded == null) {
            encoded = HtmlUtils.encode(html, loading);
            if (containsImages(html)) {
                rawHtmlCache.put(id, encoded);
            } else {
                rawHtmlCache.remove(id);
                fullHtmlCache.put(id, encoded);
                return show(view, encoded);
            }
        }

        if (TextUtils.isEmpty(encoded)) return hide(view);

        show(view, encoded);
        view.setTag(id);
        ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask();
        AsyncTaskCompat.executeParallel(asyncTask, html, id, view);
        return this;
    }

    public void repoInfo(RepoInfo repoInfo) {
        this.repoInfo = repoInfo;
    }

    private InputStream fetch(String urlString) throws IOException {
        if (!urlString.contains("http")) {
            Uri.Builder builder = Uri.parse("https://github.com/").buildUpon();

            builder.appendPath(repoInfo.owner);
            builder.appendPath(repoInfo.name);
            builder.appendPath("raw");
            builder.appendPath(repoInfo.branch);

            if (urlString.startsWith("./")) {
                urlString = urlString.replace("./", "");
            }

            builder.appendPath(urlString);
            urlString = builder.build().toString();
        }
        URL url = new URL(urlString);
        return url.openStream();
    }

    @Override
    public Drawable getDrawable(String source) {
        /*
        try {
            Bitmap bitmap = ImageUtils.getBitmap(context, dir.getAbsolutePath(), width, height);

            loadedBitmaps.add(new WeakReference<>(bitmap));

            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            return drawable;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
        */

        if (loading != null) {
            File output = null;
            if (destroyed) {
                return loading.getDrawable(source);
            }
            try {


                output = File.createTempFile("image", ".jpg", dir);
                InputStream is = fetch(source);
                if (is != null) {
                    boolean success = FileUtils.save(output, is);
                    if (success) {
                        Bitmap bitmap = ImageUtils.getBitmap(output, width, Integer.MAX_VALUE);
                        if (bitmap == null) {
                            return loading.getDrawable(source);
                        }
                        loadedBitmaps.add(new WeakReference<Bitmap>(bitmap));
                        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        return drawable;
                    } else {
                        return loading.getDrawable(source);
                    }
                } else {
                    return loading.getDrawable(source);
                }
            } catch (IOException e) {
                return loading.getDrawable(source);
            } finally {
                if (output != null) output.delete();
            }
        }
        return null;
    }

    private static class LoadingImageGetter implements ImageGetter {

        private final Drawable image;

        private LoadingImageGetter(final Context context, final int size) {
            int imageSize = Math.round(context.getResources().getDisplayMetrics().density * size + 0.5F);
            image = new IconicsDrawable(context, Octicons.Icon.oct_file_media).sizePx(imageSize);
        }

        public Drawable getDrawable(String source) {
            return image;
        }
    }

    public class ImageGetterAsyncTask extends AsyncTask<Object, Void, CharSequence> {

        String html;
        Object id;
        TextView view;

        @Override
        protected CharSequence doInBackground(Object... params) {
            html = (String) params[0];
            id = params[1];
            view = (TextView) params[2];
            return HtmlUtils.encode(html, HttpImageGetter.this);
        }

        protected void onPostExecute(CharSequence result) {
            if (result != null) {
                rawHtmlCache.remove(id);
                fullHtmlCache.put(id, result);

                if (id.equals(view.getTag())) {
                    show(view, result);
                }
            }
        }
    }
}