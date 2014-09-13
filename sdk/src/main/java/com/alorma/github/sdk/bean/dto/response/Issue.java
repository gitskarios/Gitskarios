package com.alorma.github.sdk.bean.dto.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernat on 20/07/2014.
 */

public class Issue extends IssueComment{
    public int number;
    public IssueState state;
    public String title;
    public ListLabels labels;
    public User asignee;
    public Milestone milestone;
    public int comments;
    @SerializedName("pull_request")
    public Issue pullRequest;
    @SerializedName("closed_at")
    public String closedAt;
	public Repo repository;
}
