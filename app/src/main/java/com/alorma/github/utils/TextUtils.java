package com.alorma.github.utils;

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

}
