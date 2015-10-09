package com.alorma.github.utils;

import android.widget.TextView;

import com.alorma.github.R;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 28/05/2015.
 */
public class TextUtils {

    public static String splitLines(String content, int maxLines) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(content));
        String line = reader.readLine();
        while (line != null) {
            result.add(line);
            line = reader.readLine();
        }
        if (result.size() > maxLines) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < maxLines; i++) {
                if (result.get(i).isEmpty()) {
                    builder.append("\n");
                } else {
                    builder.append(result.get(i));
                    builder.append("\n");
                }
            }
            content = builder.toString();
        }
        return content;
    }

    public static void applyNumToTextView(TextView textView, Octicons.Icon value, int num) {
        IconicsDrawable drawable = new IconicsDrawable(textView.getContext(), value);
        drawable.sizeRes(R.dimen.textSizeSmall);
        int colorRes;
        if (num > 0) {
            colorRes = R.color.icons;
        } else {
            colorRes = R.color.gray_github_light_selected;
        }
        drawable.colorRes(colorRes);
        textView.setCompoundDrawables(null, null, drawable, null);
        int offset = textView.getResources().getDimensionPixelOffset(R.dimen.gapSmall);
        textView.setCompoundDrawablePadding(offset);
        textView.setText(String.valueOf(num));
        textView.setTextColor(textView.getContext().getResources().getColor(colorRes));
    }
}
