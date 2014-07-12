package com.alorma.github.sdk.bean.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Gist {

    @SerializedName("public")
	private boolean isPublic;

	private Date createdAt;

	private Date updatedAt;

	private int comments;

	private List<GistRevision> history;

	private Map<String, GistFile> files;

	private String description;

	private String gitPullUrl;

	private String gitPushUrl;

    @SerializedName("forks_url")
	private String forksUrl;

	private String htmlUrl;

	private String id;

	private String url;

	private User user;

	/**
	 * @return isPublic
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * @param isPublic
	 * @return this gist
	 */
	public Gist setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt
	 * @return this gist
	 */
	public Gist setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	/**
	 * @return updatedAt
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt
	 * @return this gist
	 */
	public Gist setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	/**
	 * @return comments
	 */
	public int getComments() {
		return comments;
	}

	/**
	 * @param comments
	 * @return this gist
	 */
	public Gist setComments(int comments) {
		this.comments = comments;
		return this;
	}

	/**
	 * @return history
	 */
	public List<GistRevision> getHistory() {
		return history;
	}

	/**
	 * @param history
	 * @return this gist
	 */
	public Gist setHistory(List<GistRevision> history) {
		this.history = history;
		return this;
	}

	/**
	 * @return files
	 */
	public Map<String, GistFile> getFiles() {
		return files;
	}

	/**
	 * @param files
	 * @return this gist
	 */
	public Gist setFiles(Map<String, GistFile> files) {
		this.files = files;
		return this;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 * @return this gist
	 */
	public Gist setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * @return gitPullUrl
	 */
	public String getGitPullUrl() {
		return gitPullUrl;
	}

	/**
	 * @param gitPullUrl
	 * @return this gist
	 */
	public Gist setGitPullUrl(String gitPullUrl) {
		this.gitPullUrl = gitPullUrl;
		return this;
	}

	/**
	 * @return gitPushUrl
	 */
	public String getGitPushUrl() {
		return gitPushUrl;
	}

	/**
	 * @param gitPushUrl
	 * @return this gist
	 */
	public Gist setGitPushUrl(String gitPushUrl) {
		this.gitPushUrl = gitPushUrl;
		return this;
	}

	/**
	 * @return htmlUrl
	 */
	public String getHtmlUrl() {
		return htmlUrl;
	}

	/**
	 * @param htmlUrl
	 * @return this gist
	 */
	public Gist setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
		return this;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 * @return this gist
	 */
	public Gist setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 * @return this gist
	 */
	public Gist setUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 * @return this gist
	 */
	public Gist setUser(User user) {
		this.user = user;
		return this;
	}
}