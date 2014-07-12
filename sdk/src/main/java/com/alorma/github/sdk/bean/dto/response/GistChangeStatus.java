package com.alorma.github.sdk.bean.dto.response;

public class GistChangeStatus {

	private int additions;

	private int deletions;

	private int total;

	/**
	 * @return additions
	 */
	public int getAdditions() {
		return additions;
	}

	/**
	 * @param additions
	 * @return this gist change status
	 */
	public GistChangeStatus setAdditions(int additions) {
		this.additions = additions;
		return this;
	}

	/**
	 * @return deletions
	 */
	public int getDeletions() {
		return deletions;
	}

	/**
	 * @param deletions
	 * @return this gist change status
	 */
	public GistChangeStatus setDeletions(int deletions) {
		this.deletions = deletions;
		return this;
	}

	/**
	 * @return total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total
	 * @return this gist change status
	 */
	public GistChangeStatus setTotal(int total) {
		this.total = total;
		return this;
	}
}