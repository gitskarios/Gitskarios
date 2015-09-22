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

import android.support.v7.graphics.Palette;

import com.musenkishi.atelier.ColorType;

/**
 * A Swatch delegate representing the {@link android.support.v7.graphics.Palette.Swatch}
 * from {@link Palette}.getVibrantSwatch().
 * <p>Created by Freddie (Musenkishi) Lust-Hed on 04/06/15.</p>
 */
public class VibrantSwatch extends AbstractSwatch {

    public VibrantSwatch(ColorType colorType) {
        super(colorType);
    }

    @Override
    public int getColor(Palette palette) {
        return getSwatchColor(palette, palette.getVibrantSwatch());
    }
}
