package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable {
    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    private String name;
    private ShaUrl sha;
    private String zipballUrl;
    private String tarballUrl;

    public Release release;

    public Tag(){}

    public Tag(com.alorma.github.sdk.core.repositories.releases.tags.Tag tag) {
        this.name = tag.getName();
        this.sha = new ShaUrl();
        this.sha.url = tag.getSha().getUrl();
        this.sha.sha = tag.getSha().getSha();
        this.zipballUrl = tag.getZipballUrl();
        this.tarballUrl = tag.getTarballUrl();
        this.release = new Release(tag.release);
    }

    protected Tag(Parcel in) {
        this.name = in.readString();
        this.sha = in.readParcelable(ShaUrl.class.getClassLoader());
        this.zipballUrl = in.readString();
        this.tarballUrl = in.readString();
        this.release = in.readParcelable(Release.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeParcelable(this.sha, flags);
        dest.writeString(this.zipballUrl);
        dest.writeString(this.tarballUrl);
        dest.writeParcelable(this.release, flags);
    }
}
