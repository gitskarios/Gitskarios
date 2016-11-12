package core;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GithubCommentReactions extends HashMap<String, Object> implements Parcelable {

    private int totalCount;

    private List<GithubReaction> reactions = new ArrayList<>();

    private String url;

    public List<GithubReaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<GithubReaction> reactions) {
        this.reactions = reactions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalCount);
        dest.writeList(this.reactions);
        dest.writeString(this.url);
    }

    public GithubCommentReactions() {
    }

    protected GithubCommentReactions(Parcel in) {
        this.totalCount = in.readInt();
        this.reactions = new ArrayList<>();
        in.readList(this.reactions, GithubReaction.class.getClassLoader());
        this.url = in.readString();
    }

    public static final Parcelable.Creator<GithubCommentReactions> CREATOR = new Parcelable.Creator<GithubCommentReactions>() {
        @Override
        public GithubCommentReactions createFromParcel(Parcel source) {
            return new GithubCommentReactions(source);
        }

        @Override
        public GithubCommentReactions[] newArray(int size) {
            return new GithubCommentReactions[size];
        }
    };
}
