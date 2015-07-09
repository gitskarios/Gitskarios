/*
 * Copyright (C) 2015 Freddie (Musenkishi) Lust-Hed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.musenkishi.atelier;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.musenkishi.atelier.swatch.Swatch;
import com.musenkishi.atelier.swatch.VibrantSwatch;

import org.apache.commons.collections4.map.LRUMap;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class that can apply colors to Views lazily.
 * Can be used in ListViews.
 * <p/>
 * Created by Freddie (Musenkishi) Lust-Hed on 2014-10-21.
 */
public class Atelier {

    private static final int MSG_RENDER_PALETTE = 4194;
    private static final int MSG_DISPLAY_PALETTE = 4195;

    private static final int MAX_ITEMS_IN_CACHE = 100;
    private static final int MAX_CONCURRENT_THREADS = 5;
    private static final int viewTagKey = R.string.atelier_view_tag;

    private static int TRUE = 1;
    private static int FALSE = 0;

    private static Handler uiHandler;
    private static Handler backgroundHandler;

    private static Map<String, Palette> paletteCache;
    private static ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS);
    private static Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            switch (message.what) {

                case MSG_RENDER_PALETTE:
                    Pair<Bitmap, PaletteTarget> pair = (Pair<Bitmap, PaletteTarget>) message.obj;
                    if (pair != null && !pair.first.isRecycled()) {
                        Palette palette = Palette.from(pair.first).generate();

                        paletteCache.put(pair.second.getId(), palette);

                        Message uiMessage = uiHandler.obtainMessage();
                        uiMessage.what = MSG_DISPLAY_PALETTE;
                        uiMessage.obj = new Pair<>(palette, pair.second);
                        uiMessage.arg1 = FALSE;

                        uiHandler.sendMessage(uiMessage);
                    }
                    break;

                case MSG_DISPLAY_PALETTE:
                    Pair<Palette, PaletteTarget> pairDisplay = (Pair<Palette, PaletteTarget>) message.obj;
                    boolean fromCache = message.arg1 == TRUE;
                    applyColorToView(pairDisplay.second, pairDisplay.first, fromCache);
                    callListener(pairDisplay.first, pairDisplay.second.getListener());

                    break;

            }

            return false;
        }
    };

    private Atelier() {
        //Don't use
    }

    public static AtelierBuilder with(Context context, String id) {
        if (paletteCache == null) {
            paletteCache = Collections.synchronizedMap(
                    new LRUMap<String, Palette>(MAX_ITEMS_IN_CACHE)
            );
        }
        if (uiHandler == null || backgroundHandler == null) {
            setupHandlers(context.getApplicationContext());
        }
        return new AtelierBuilder(id);
    }

    private static void setupHandlers(Context context) {
        HandlerThread handlerThread = new HandlerThread("palette-loader-background");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper(), callback);
        uiHandler = new Handler(context.getMainLooper(), callback);
    }

    private static void applyColorToView(PaletteTarget target, Palette palette, boolean fromCache) {
        if (!isViewRecycled(target)) {
            applyColorToView(target, target.getSwatch().getColor(palette), fromCache);
        }
    }

    private static void applyColorToView(final PaletteTarget target, int color, boolean fromCache) {
        if (target.getView() instanceof TextView) {
            applyColorToView((TextView) target.getView(), color, fromCache);
        } else if (target.getView() instanceof CardView) {
            applyColorToView((CardView) target.getView(), color, fromCache);
        } else if (target.getView() instanceof FloatingActionButton) {
            applyColorToView((FloatingActionButton) target.getView(), color, fromCache, target.shouldMaskDrawable());
        } else if (target.getView() instanceof ImageView) {
            applyColorToView((ImageView) target.getView(), color, fromCache, target.shouldMaskDrawable());
        } else {
            if (fromCache) {
                target.getView().setBackgroundColor(color);
            } else {
                Drawable preDrawable;

                if (target.getView().getBackground() == null) {
                    preDrawable = new ColorDrawable(Color.TRANSPARENT);
                } else {
                    preDrawable = target.getView().getBackground();
                }

                TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{
                        preDrawable,
                        new ColorDrawable(color)
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    target.getView().setBackground(transitionDrawable);
                } else {
                    target.getView().setBackgroundDrawable(transitionDrawable);
                }
                transitionDrawable.startTransition(300);
            }
        }
    }

    private static void applyColorToView(final TextView textView, int color, boolean fromCache) {
        if (fromCache) {
            textView.setTextColor(color);
        } else {
            Integer colorFrom = textView.getCurrentTextColor();
            Integer colorTo = color;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    textView.setTextColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }
    }

    private static void applyColorToView(final CardView cardView, int color, boolean fromCache) {
        if (fromCache) {
            cardView.setCardBackgroundColor(color);
        } else {
            Integer colorFrom = Color.parseColor("#FFFAFAFA"); //Default light CardView color.
            Integer colorTo = color;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    cardView.setCardBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.setDuration(300);
            colorAnimation.start();
        }
    }

    private static void applyColorToView(final FloatingActionButton floatingActionButton, int color, boolean fromCache, boolean shouldMask) {
        if (fromCache) {
            if (shouldMask) {
                if (floatingActionButton.getDrawable() != null) {
                    floatingActionButton.getDrawable().mutate()
                            .setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                } else if (floatingActionButton.getBackground() != null) {
                    floatingActionButton.getBackground().mutate()
                            .setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                }
            } else {
                ColorStateList colorStateList = ColorStateList.valueOf(color);
                floatingActionButton.setBackgroundTintList(colorStateList);
            }
        } else {
            if (shouldMask) {

                Integer colorFrom;
                ValueAnimator.AnimatorUpdateListener imageAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        if (floatingActionButton.getDrawable() != null) {
                            floatingActionButton.getDrawable().mutate()
                                    .setColorFilter((Integer) valueAnimator
                                            .getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
                        } else if (floatingActionButton.getBackground() != null) {
                            floatingActionButton.getBackground().mutate()
                                    .setColorFilter((Integer) valueAnimator
                                            .getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
                        }
                    }
                };
                ValueAnimator.AnimatorUpdateListener animatorUpdateListener;

                PaletteTag paletteTag = (PaletteTag) floatingActionButton.getTag(viewTagKey);
                animatorUpdateListener = imageAnimatorUpdateListener;
                colorFrom = paletteTag.getColor();
                floatingActionButton.setTag(viewTagKey, new PaletteTag(paletteTag.getId(), color));

                Integer colorTo = color;
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.addUpdateListener(animatorUpdateListener);
                colorAnimation.setDuration(300);
                colorAnimation.start();

            } else {

                Integer colorFrom = Color.parseColor("#FFFAFAFA");

                ColorStateList colorStateList = floatingActionButton.getBackgroundTintList();
                if (colorStateList != null) {
                    colorFrom = colorStateList.getDefaultColor();
                }

                Integer colorTo = color;
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        int color = (Integer) animator.getAnimatedValue();
                        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(color));
                    }
                });
                colorAnimation.setDuration(300);
                colorAnimation.start();
            }
        }
    }

    private static void applyColorToView(final ImageView imageView, int color, boolean fromCache, boolean shouldMask) {
        if (fromCache) {
            if (shouldMask) {
                if (imageView.getDrawable() != null) {
                    imageView.getDrawable().mutate()
                            .setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                } else if (imageView.getBackground() != null) {
                    imageView.getBackground().mutate()
                            .setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                }
            } else {
                imageView.setBackgroundColor(color);
            }
        } else {
            if (shouldMask) {
                Integer colorFrom;
                ValueAnimator.AnimatorUpdateListener imageAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        if (imageView.getDrawable() != null) {
                            imageView.getDrawable().mutate()
                                    .setColorFilter((Integer) valueAnimator
                                            .getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
                        } else if (imageView.getBackground() != null) {
                            imageView.getBackground().mutate()
                                    .setColorFilter((Integer) valueAnimator
                                            .getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
                        }
                    }
                };
                ValueAnimator.AnimatorUpdateListener animatorUpdateListener;

                PaletteTag paletteTag = (PaletteTag) imageView.getTag(viewTagKey);
                animatorUpdateListener = imageAnimatorUpdateListener;
                colorFrom = paletteTag.getColor();
                imageView.setTag(viewTagKey, new PaletteTag(paletteTag.getId(), color));

                Integer colorTo = color;
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.addUpdateListener(animatorUpdateListener);
                colorAnimation.setDuration(300);
                colorAnimation.start();
            } else {
                Drawable preDrawable;

                if (imageView.getBackground() == null) {
                    preDrawable = new ColorDrawable(Color.TRANSPARENT);
                } else {
                    preDrawable = imageView.getBackground();
                }

                TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{
                        preDrawable,
                        new ColorDrawable(color)
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.setBackground(transitionDrawable);
                } else {
                    imageView.setBackgroundDrawable(transitionDrawable);
                }
                transitionDrawable.startTransition(300);
            }
        }
    }

    /**
     * Is it null? Is that null? What about that one? Is that null too? What about this one?
     * And this one? Is this null? Is null even null? How can null be real if our eyes aren't real?
     * <p/>
     * <p>Checks whether the view has been recycled or not.</p>
     *
     * @param target A {@link Atelier.PaletteTarget} to check
     * @return true is view has been recycled, otherwise false.
     */
    private static boolean isViewRecycled(PaletteTarget target) {

        if (target != null && target.getId() != null && target.getView() != null
                && target.getView().getTag(viewTagKey) != null) {
            if (target.getView().getTag(viewTagKey) instanceof PaletteTag) {
                return !target.getId().equals(((PaletteTag) target.getView().getTag(viewTagKey)).getId());
            } else {
                throw new NoPaletteTagFoundException("Atelier couldn't determine whether" +
                        " a View has been reused or not. Atelier uses View.setTag(key, object) and " +
                        "View.getTag(key) for keeping check if a View has been reused and it's " +
                        "recommended to refrain from setting tags to Views Atelier is using."
                );
            }
        } else {
            return false;
        }
    }

    private static void callListener(Palette palette, OnPaletteRenderedListener onPaletteRenderedListener) {
        if (onPaletteRenderedListener != null) {
            onPaletteRenderedListener.onRendered(palette);
        }
    }

    private static void callColorListener(int color, OnPaletteColorListener onPaletteColorListener) {
        if (onPaletteColorListener != null) {
            onPaletteColorListener.onColor(color);
        }
    }

    public interface OnPaletteRenderedListener {
        void onRendered(Palette palette);
    }

    public interface OnPaletteColorListener {
        void onColor(int color);
    }

    public static class AtelierBuilder {

        private String id;
        private Bitmap bitmap;
        private boolean maskDrawable;
        private int fallbackColor = Color.TRANSPARENT;
        private Swatch swatch = new VibrantSwatch(ColorType.BACKGROUND);
        private Palette palette;
        private OnPaletteRenderedListener onPaletteRenderedListener;
        private OnPaletteColorListener onPaletteColorListener;

        public AtelierBuilder(String id) {
            this.id = id;
        }

        public AtelierBuilder load(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public AtelierBuilder load(Palette palette) {
            this.palette = palette;
            return this;
        }

        public AtelierBuilder mask() {
            maskDrawable = true;
            return this;
        }

        public AtelierBuilder fallbackColor(int fallbackColor) {
            this.fallbackColor = fallbackColor;
            return this;
        }

        public AtelierBuilder swatch(Swatch swatch) {
            this.swatch = swatch;
            return this;
        }

        public AtelierBuilder listener(OnPaletteRenderedListener onPaletteRenderedListener) {
            this.onPaletteRenderedListener = onPaletteRenderedListener;
            return this;
        }

        public AtelierBuilder colorsListener(OnPaletteColorListener onPaletteColorListener) {
            this.onPaletteColorListener = onPaletteColorListener;
            return this;
        }

        public void into(View... views) {
            for (View view : views) {
                start(view);
            }
        }

        private void start(View view) {
            final PaletteTarget paletteTarget = new PaletteTarget(id, swatch, view, maskDrawable, fallbackColor, onPaletteRenderedListener, onPaletteColorListener);
            if (palette != null) {
                paletteCache.put(paletteTarget.getId(), palette);
                applyColorToView(paletteTarget, palette, false);
                callListener(palette, onPaletteRenderedListener);
                callColorListener(swatch.getColor(palette), onPaletteColorListener);
            } else {
                if (paletteCache.get(id) != null) {
                    Palette palette = paletteCache.get(id);
                    applyColorToView(paletteTarget, palette, true);
                    callListener(palette, onPaletteRenderedListener);
                    callColorListener(swatch.getColor(palette), onPaletteColorListener);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        executorService.submit(new PaletteRenderer(bitmap, paletteTarget));
                    } else {
                        Message bgMessage = backgroundHandler.obtainMessage();
                        bgMessage.what = MSG_RENDER_PALETTE;
                        bgMessage.obj = new Pair<>(bitmap, paletteTarget);
                        backgroundHandler.sendMessage(bgMessage);
                    }
                }
            }
        }

    }

    private static class PaletteRenderer implements Runnable {

        private Bitmap bitmap;
        private PaletteTarget paletteTarget;

        private PaletteRenderer(Bitmap bitmap, PaletteTarget paletteTarget) {
            this.bitmap = bitmap;
            this.paletteTarget = paletteTarget;
        }

        @Override
        public void run() {
            if (bitmap != null && !bitmap.isRecycled()) {
                Palette palette = Palette.from(bitmap).generate();
                paletteCache.put(paletteTarget.getId(), palette);

                PalettePresenter palettePresenter = new PalettePresenter(
                        paletteTarget,
                        palette,
                        false
                );
                uiHandler.post(palettePresenter);
            }
        }
    }

    private static class PalettePresenter implements Runnable {

        private PaletteTarget paletteTarget;
        private Palette palette;
        private boolean fromCache;

        private PalettePresenter(PaletteTarget paletteTarget, Palette palette, boolean fromCache) {
            this.paletteTarget = paletteTarget;
            this.palette = palette;
            this.fromCache = fromCache;
        }

        @Override
        public void run() {
            applyColorToView(paletteTarget, palette, fromCache);
            callListener(palette, paletteTarget.getListener());
            callColorListener(paletteTarget.getSwatch().getColor(palette), paletteTarget.getColorListener());
        }
    }

    private static class PaletteTarget {
        private String id;
        private Swatch swatch;
        private View view;
        private OnPaletteColorListener onPaletteColorListener;
        private boolean maskDrawable;
        private OnPaletteRenderedListener onPaletteRenderedListener;

        private PaletteTarget(String id, Swatch swatch, View view, boolean maskDrawable, int fallbackColor, OnPaletteRenderedListener onPaletteRenderedListener, OnPaletteColorListener onPaletteColorListener) {
            this.id = id;
            this.swatch = swatch;
            this.view = view;
            this.onPaletteColorListener = onPaletteColorListener;
            this.onPaletteRenderedListener = onPaletteRenderedListener;
            this.view.setTag(viewTagKey, new PaletteTag(this.id, fallbackColor));
            this.maskDrawable = maskDrawable;
        }

        public String getId() {
            return id;
        }

        public Swatch getSwatch() {
            return swatch;
        }

        public View getView() {
            return view;
        }

        public boolean shouldMaskDrawable() {
            return maskDrawable;
        }

        public OnPaletteRenderedListener getListener() {
            return onPaletteRenderedListener;
        }

        public OnPaletteColorListener getColorListener() {
            return onPaletteColorListener;
        }
    }

    private static class PaletteTag {
        private String id;
        private Integer color;

        private PaletteTag(String id, Integer color) {
            this.id = id;
            this.color = color;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getColor() {
            return color;
        }

        public void setColor(Integer color) {
            this.color = color;
        }
    }

    public static class NoPaletteTagFoundException extends NullPointerException {
        public NoPaletteTagFoundException() {
            super();
        }

        public NoPaletteTagFoundException(String message) {
            super(message);
        }
    }
}
