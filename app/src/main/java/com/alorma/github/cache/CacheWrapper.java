package com.alorma.github.cache;

import com.alorma.github.sdk.bean.dto.response.Branch;
import com.fewlaps.quitnowcache.QNCache;
import com.fewlaps.quitnowcache.QNCacheBuilder;

import java.util.List;

public class CacheWrapper {

    private static final int KEEPALIVE_IN_MILLIS = 10 * 60 * 1000;

    private static final String REPO_KEY_PREFIX = "repo";
    private static final String README_KEY_PREFIX = "readme";

    private static QNCache cache = new QNCacheBuilder().setDefaultKeepaliveInMillis(KEEPALIVE_IN_MILLIS).createQNCache();

    //region branches
    public static List<Branch> getBranches(String repoId) {
        return cache.get(convertToEffectiveRepoKey(repoId));
    }

    public static void setBranches(String repoId, List<Branch> branches) {
        cache.set(convertToEffectiveRepoKey(repoId), branches);
    }

    private static String convertToEffectiveRepoKey(String repoId) {
        return REPO_KEY_PREFIX + repoId;
    }
    //endregion

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
}
