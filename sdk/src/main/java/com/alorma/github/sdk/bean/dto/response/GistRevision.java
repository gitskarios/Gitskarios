package com.alorma.github.sdk.bean.dto.response;

import java.util.Date;

public class GistRevision {

	private Date committedAt;

	private GistChangeStatus changeStatus;

	private String url;

	private String version;

	private User user;

	/**
	 * @return committedAt
	 */
	public Date getCommittedAt() {
		return committedAt;
	}

	/**
	 * @param committedAt
	 * @return this gist revision
	 */
	public GistRevision setCommittedAt(Date committedAt) {
		this.committedAt = committedAt;
		return this;
	}

	/**
	 * @return changeStatus
	 */
	public GistChangeStatus getChangeStatus() {
		return changeStatus;
	}

	/**
	 * @param changeStatus
	 * @return this gist revision
	 */
	public GistRevision setChangeStatus(GistChangeStatus changeStatus) {
		this.changeStatus = changeStatus;
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
	 * @return this gist revision
	 */
	public GistRevision setUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * @return version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 * @return this gist revision
	 */
	public GistRevision setVersion(String version) {
		this.version = version;
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
	 * @return this gist revision
	 */
	public GistRevision setUser(User user) {
		this.user = user;
		return this;
	}
}