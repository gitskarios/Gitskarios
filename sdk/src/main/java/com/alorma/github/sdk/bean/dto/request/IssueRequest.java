package com.alorma.github.sdk.bean.dto.request;

import com.alorma.github.sdk.bean.dto.response.IssueState;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueRequest {
    public String title;
    public String body;
    public String assignee;
    public Integer milestone;
    public String[] labels;
	public IssueState state;
}
