package com.alorma.github.sdk.bean.dto.response;

public class UserPlan  {

	/** serialVersionUID */
    private static final long serialVersionUID = 4759542049129654659L;

	private long collaborators;

	private long privateRepos;

	private long space;

	private String name;

	/**
	 * @return contributors
	 */
	public long getCollaborators() {
		return collaborators;
	}

	/**
	 * @param collaborators
	 * @return this user plan
	 */
	public UserPlan setCollaborators(long collaborators) {
		this.collaborators = collaborators;
		return this;
	}

	/**
	 * @return privateRepos
	 */
	public long getPrivateRepos() {
		return privateRepos;
	}

	/**
	 * @param privateRepos
	 * @return this user plan
	 */
	public UserPlan setPrivateRepos(long privateRepos) {
		this.privateRepos = privateRepos;
		return this;
	}

	/**
	 * @return space
	 */
	public long getSpace() {
		return space;
	}

	/**
	 * @param space
	 * @return this user plan
	 */
	public UserPlan setSpace(long space) {
		this.space = space;
		return this;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @return this user plan
	 */
	public UserPlan setName(String name) {
		this.name = name;
		return this;
	}
}