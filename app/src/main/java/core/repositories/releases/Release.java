package core.repositories.releases;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import core.ShaUrl;
import core.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Release extends ShaUrl implements Parcelable {
    @SerializedName("assets_url") private String assetsUrl;
    @SerializedName("upload_url") private String uploadUrl;
    @SerializedName("tarball_url") private String tarballUrl;
    @SerializedName("zipball_url") private String zipballUrl;
    @SerializedName("id") private Integer id;
    @SerializedName("tag_name") private String tagName;
    @SerializedName("target_commitish") private String targetCommitish;
    @SerializedName("name") private String name;
    @SerializedName("body") private String body;
    @SerializedName("draft") private Boolean draft;
    @SerializedName("prerelease") private Boolean prerelease;
    @SerializedName("created_at") private Date createdAt;
    @SerializedName("published_at") private Date publishedAt;
    @SerializedName("author") private User author;
    @SerializedName("assets") private List<Asset> assets = new ArrayList<Asset>();

    public String getAssetsUrl() {
        return assetsUrl;
    }

    public void setAssetsUrl(String assetsUrl) {
        this.assetsUrl = assetsUrl;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getTarballUrl() {
        return tarballUrl;
    }

    public void setTarballUrl(String tarballUrl) {
        this.tarballUrl = tarballUrl;
    }

    public String getZipballUrl() {
        return zipballUrl;
    }

    public void setZipballUrl(String zipballUrl) {
        this.zipballUrl = zipballUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTargetCommitish() {
        return targetCommitish;
    }

    public void setTargetCommitish(String targetCommitish) {
        this.targetCommitish = targetCommitish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Boolean isPreRelease() {
        return prerelease;
    }

    public void setPrerelease(Boolean prerelease) {
        this.prerelease = prerelease;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.assetsUrl);
        dest.writeString(this.uploadUrl);
        dest.writeString(this.tarballUrl);
        dest.writeString(this.zipballUrl);
        dest.writeValue(this.id);
        dest.writeString(this.tagName);
        dest.writeString(this.targetCommitish);
        dest.writeString(this.name);
        dest.writeString(this.body);
        dest.writeValue(this.draft);
        dest.writeValue(this.prerelease);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.publishedAt != null ? this.publishedAt.getTime() : -1);
        dest.writeParcelable(this.author, flags);
        dest.writeList(this.assets);
    }

    public Release() {
    }

    protected Release(Parcel in) {
        this.assetsUrl = in.readString();
        this.uploadUrl = in.readString();
        this.tarballUrl = in.readString();
        this.zipballUrl = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tagName = in.readString();
        this.targetCommitish = in.readString();
        this.name = in.readString();
        this.body = in.readString();
        this.draft = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.prerelease = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpPublishedAt = in.readLong();
        this.publishedAt = tmpPublishedAt == -1 ? null : new Date(tmpPublishedAt);
        this.author = in.readParcelable(User.class.getClassLoader());
        this.assets = new ArrayList<Asset>();
        in.readList(this.assets, Asset.class.getClassLoader());
    }

    public static final Parcelable.Creator<Release> CREATOR = new Parcelable.Creator<Release>() {
        @Override
        public Release createFromParcel(Parcel source) {
            return new Release(source);
        }

        @Override
        public Release[] newArray(int size) {
            return new Release[size];
        }
    };
}
