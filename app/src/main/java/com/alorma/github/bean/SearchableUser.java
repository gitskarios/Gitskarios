package com.alorma.github.bean;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.alorma.github.sdk.bean.dto.response.User;
import com.linkedin.android.spyglass.mentions.Mentionable;
import com.linkedin.android.spyglass.suggestions.interfaces.Suggestible;

/**
 * Created by bernat.borras on 9/1/16.
 */
public class SearchableUser extends User implements Suggestible, Mentionable {
    @Override
    public int getSuggestibleId() {
        return id;
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return login;
    }

    public SearchableUser() {

    }

    public SearchableUser(Parcel in) {
        login = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new SearchableUser(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(login);
        super.writeToParcel(dest, flags);
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        return "@" + login;
    }

    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }
}
