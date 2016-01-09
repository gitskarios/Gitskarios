package com.alorma.github.emoji;

import android.graphics.Bitmap;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bernat on 08/07/2015.
 */
public class EmojiBitmapLoader {

    private final Handler handler;
    private Map<String, Bitmap> map;
    private Map<String, String> emojisMap;
    private SpannableString spannableString;
    private int sizeIcon;
    private String textFromTextView;
    private TextView textView;

    public EmojiBitmapLoader() {
        this.map = new HashMap<>();
        this.emojisMap = new HashMap<>();
        handler = new Handler();
    }

    public void parseTextView(final TextView textView) {
        /*this.textView = textView;

        if (textView.getEditableText() != null) {
            spannableString = new SpannableString(textView.getEditableText());
        } else {
            spannableString = new SpannableString(textView.getText());
        }

        sizeIcon = (int) (-textView.getPaint().ascent());
        textFromTextView = textView.getText().toString();


        if (emojisMap.size() == 0) {
            EmojisProvider provider = new EmojisProvider(textView.getContext());
            provider.getEmojis(new EmojisProvider.EmojisCallback() {
                @Override
                public void onEmojisLoaded(List<Emoji> emojis) {

                    for (Emoji emoji : emojis) {
                        emojisMap.put(":" + emoji.getKey() + ":", emoji.getValue());
                    }

                    loadFromTextView();
                }

                @Override
                public void onEmojisLoadFail() {

                }
            });
        } else {
            loadFromTextView();
        }*/
    }

    private void loadFromTextView() {

        // TODO '[^:]+'
        // TODO  '([^:']+)' i agafa el matx 1
        // TODO [[^']|:]:((\+|\-)?\w+|\d):[[^']|:]
        final String regex = ":((\\+|\\-)?\\w+|\\d):";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(textFromTextView);
        if (matcher.find()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Pattern pattern = Pattern.compile(regex);

                    Matcher matcher = pattern.matcher(textFromTextView);

                    while (matcher.find()) {
                        if (emojisMap.get(matcher.group()) != null) {
                            map.put(matcher.group(), null);
                        }
                    }

                    for (String s : map.keySet()) {
                        downloadBitmap(s, emojisMap.get(s), sizeIcon);
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    private void downloadBitmap(final String key, String url, final int size) {
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                if (loadedImage != null) {
                    Bitmap icon = Bitmap.createScaledBitmap(loadedImage, size, size, true);
                    map.put(key, icon);
                }
                Map<String, Bitmap> clearMap = new HashMap<>(map);

                for (String s : map.keySet()) {
                    if (map.get(s) != null) {
                        clearMap.remove(s);
                    }
                }

                if (clearMap.size() == 0) {
                    addIcons();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private void addIcons() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String regex = ":((\\+|\\-)?\\w+|\\d):";

                Pattern pattern = Pattern.compile(regex);

                final Matcher matcher = pattern.matcher(textFromTextView);
                while (matcher.find()) {
                    if (map.get(matcher.group()) != null) {
                        int start = matcher.start();
                        int end = matcher.end();
                        ImageSpan span = new ImageSpan(textView.getContext(), map.get(matcher.group()), ImageSpan.ALIGN_BASELINE);
                        spannableString.setSpan(span, start, end, 0);
                    }
                }

                textView.setText(spannableString);

                if (textView instanceof EditText) {
                    ((EditText) textView).setSelection(textView.getSelectionEnd());
                }
            }
        });
    }
}
