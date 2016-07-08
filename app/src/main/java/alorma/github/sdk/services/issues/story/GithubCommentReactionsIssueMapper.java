package com.alorma.github.sdk.services.issues.story;

import com.alorma.github.sdk.bean.dto.response.GithubComment;

import rx.functions.Func1;

public class GithubCommentReactionsIssueMapper implements Func1<GithubComment, GithubComment> {
    @Override
    public GithubComment call(GithubComment githubComment) {
        ReactionMapper.parseGithubReaction(githubComment.reactions);
        return githubComment;
    }
}
