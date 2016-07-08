package com.alorma.github.sdk.services.issues.story;

import com.alorma.github.sdk.bean.dto.response.PullRequest;

import rx.functions.Func1;

public class GithubPRReactionsIssueMapper implements Func1<PullRequest, PullRequest> {
    @Override
    public PullRequest call(PullRequest pullRequest) {
        ReactionMapper.parseGithubReaction(pullRequest.reactions);
        return pullRequest;
    }

}
