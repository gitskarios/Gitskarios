package core;

import android.os.Parcel;
import android.os.Parcelable;

public class GithubReaction implements Parcelable {
    private final GithubReactionType type;
    private final int value;

    public GithubReaction(GithubReactionType type, int value) {
        this.type = type;
        this.value = value;
    }

    public GithubReactionType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getEmijoByUnicode() + " " + value;
    }

    public String getEmijoByUnicode() {
        return new String(Character.toChars(type.getEmoji()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeInt(this.value);
    }

    protected GithubReaction(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : GithubReactionType.values()[tmpType];
        this.value = in.readInt();
    }

    public static final Parcelable.Creator<GithubReaction> CREATOR = new Parcelable.Creator<GithubReaction>() {
        @Override
        public GithubReaction createFromParcel(Parcel source) {
            return new GithubReaction(source);
        }

        @Override
        public GithubReaction[] newArray(int size) {
            return new GithubReaction[size];
        }
    };
}
