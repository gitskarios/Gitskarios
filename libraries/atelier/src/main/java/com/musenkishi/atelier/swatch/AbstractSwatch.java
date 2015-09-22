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

package com.musenkishi.atelier.swatch;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

import com.musenkishi.atelier.ColorType;

/**
 * An abstract class containing functions that should be available for all child classes.
 * <p>Created by Freddie (Musenkishi) Lust-Hed on 04/06/15.</p>
 */
abstract class AbstractSwatch implements Swatch {

    private ColorType colorType;

    public AbstractSwatch(@NonNull ColorType colorType) {
        this.colorType = colorType;
    }

    protected ColorType getColorType() {
        return this.colorType;
    }

    protected int getSwatchColor(@NonNull Palette palette, @Nullable Palette.Swatch swatch) {

        swatch = ensureSwatchIsNotNull(palette, swatch);

        switch (getColorType()) {
            case BACKGROUND:
                return swatch.getRgb();

            case TEXT_BODY:
                return swatch.getBodyTextColor();

            case TEXT_TITLE:
                return swatch.getTitleTextColor();
        }

        return Color.GRAY;
    }

    /**
     * A function that ensures a non-null {@link android.support.v7.graphics.Palette.Swatch} is
     * returned, whether from the {@link Palette} or a new based on Color.GRAY.
     * @param palette The desired {@link Palette}
     * @param nullableSwatch The {@link Palette.Swatch} that could be null.
     * @return A non-null {@link Palette.Swatch} from the
     * {@link Palette}, or a new {@link Palette.Swatch}
     * based on Color.GRAY.
     */
    @NonNull
    public Palette.Swatch ensureSwatchIsNotNull(@NonNull Palette palette, @Nullable Palette.Swatch nullableSwatch) {

        if (nullableSwatch != null) {
            return nullableSwatch;
        }
        //nullableSwatch was null, fall back to a swatch that isn't null.
        for (Palette.Swatch swatch : palette.getSwatches()) {
            if (swatch != null) {
                return swatch;
            }
        }

        return new Palette.Swatch(Color.GRAY, 1);
    }

}
