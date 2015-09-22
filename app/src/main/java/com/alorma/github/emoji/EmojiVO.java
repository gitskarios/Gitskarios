package com.alorma.github.emoji;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bernat on 08/07/2015.
 */
public class EmojiVO extends RealmObject {

    @PrimaryKey
    private String key;
    private String value;

    public EmojiVO() {

    }

    public EmojiVO(String key, String value) {
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
