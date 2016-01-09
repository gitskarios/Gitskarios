package com.alorma.github.utils;

import android.text.TextUtils;

import static java.util.Locale.US;

/**
 * Created by Bernat on 10/09/2014.
 */

public class ImageUtils {

    private static final String[] IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".gif"};

    /**
     * Is the the given file name a image file?
     *
     * @return true if the name has a markdown extension, false otherwise
     */
    public static boolean isImage(String name) {
        if (TextUtils.isEmpty(name)) return false;

        name = name.toLowerCase(US);
        for (String extension : IMAGE_EXTENSIONS)
            if (name.endsWith(extension)) return true;

        return false;
    }
}
