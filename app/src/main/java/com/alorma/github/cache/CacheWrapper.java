package com.alorma.github.cache;

import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.fewlaps.quitnowcache.QNCache;
import com.fewlaps.quitnowcache.QNCacheBuilder;

import java.util.concurrent.TimeUnit;

public class CacheWrapper {

    private static final int KEEPALIVE_IN_MILLIS = 10 * 60 * 1000;

    private static final String REPO_KEY_PREFIX = "repo";
    private static final String README_KEY_PREFIX = "readme";
    private static final String ISSUE_COMMENT_KEY_PREFIX = "issue";
    private static final String ISSUE_REQUEST_KEY_PREFIX = "issue_request";

    private static QNCache cache = new QNCacheBuilder().setDefaultKeepaliveInMillis(KEEPALIVE_IN_MILLIS).createQNCache();

    private static String convertToEffectiveRepoKey(String repoId) {
        return REPO_KEY_PREFIX + repoId;
    }

    //region readmes
    public static String getReadme(String repoId) {
        return cache.get(convertToEffectiveReadmeKey(repoId));
    }

    public static void setReadme(String repoId, String htmlContent) {
        cache.set(convertToEffectiveReadmeKey(repoId), htmlContent);
    }

    private static String convertToEffectiveReadmeKey(String repoId) {
        return README_KEY_PREFIX + repoId;
    }
    //endregion

    // region newissuecomment
    public static String getIssueComment(String newIssueId) {
        return cache.get(convertToEffectiveIssueCommentKey(newIssueId));
    }

    public static void setNewIssueComment(String newIssueId, String newIssueComment) {
        cache.set(convertToEffectiveIssueCommentKey(newIssueId), newIssueComment);
    }

    public static void clearIssueComment(String newIssueId) {
        cache.remove(convertToEffectiveIssueCommentKey(newIssueId));
    }

    private static String convertToEffectiveIssueCommentKey(String newIssueId) {
        return ISSUE_COMMENT_KEY_PREFIX + newIssueId;
    }

    // region newissuerequest
    public static IssueRequest getIssueRequest(String repoId) {
        return cache.get(convertToEffectiveIssueCommentKey(repoId));
    }

    public static void setNewIssueRequest(String repoId, IssueRequest issueRequest) {
        cache.set(convertToEffectiveIssueCommentKey(repoId), issueRequest);
    }

    public static void clearIssueRequest(String repoId) {
        cache.remove(convertToEffectiveIssueCommentKey(repoId));
    }

    private static String convertToEffectiveIssueRequestKey(String repoId) {
        return ISSUE_REQUEST_KEY_PREFIX + repoId;
    }
    // endregion

    public static void clear() {
        cache.removeAll();
    }

    // region Repository
    public static Repo getRepository(RepoInfo repoInfo) {
        return cache.get(convertToEffectiveRepoKey(repoInfo.owner + "/" + repoInfo.name));
    }

    public static void setRepository(Repo repo) {
        cache.set(convertToEffectiveRepoKey(repo.owner + "/" + repo.name), repo, TimeUnit.MINUTES.toMillis(10));
    }
}
