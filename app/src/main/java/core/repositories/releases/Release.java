package core.repositories.releases;

import com.google.gson.annotations.SerializedName;
import core.ShaUrl;
import core.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Release extends ShaUrl {
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
}
