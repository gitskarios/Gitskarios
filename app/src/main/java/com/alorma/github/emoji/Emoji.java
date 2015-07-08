package com.alorma.github.emoji;

/**
 * Created by Bernat on 08/07/2015.
 */
public class Emoji {

    private String key;
    private String value;

    public Emoji(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
