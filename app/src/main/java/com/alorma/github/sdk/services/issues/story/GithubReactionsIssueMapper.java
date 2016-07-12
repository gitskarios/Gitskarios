package com.alorma.github.sdk.services.issues.story;

import com.alorma.github.sdk.bean.dto.response.Issue;

import rx.functions.Func1;

public class GithubReactionsIssueMapper implements Func1<Issue, Issue> {
    @Override
    public Issue call(Issue issue) {
        ReactionMapper.parseGithubReaction(issue.reactions);
        return issue;
    }
}
