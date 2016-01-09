package com.alorma.github.cache;

import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.assertj.core.api.Assertions.assertThat;

public class CacheWrapperTest {

    @Test
    public void testQuitNowCacheReadme() {
        String readme = "the banana readme";

        CacheWrapper.setReadme("id", readme);
        String cachedReadme = CacheWrapper.getReadme("id");

        assertThat(cachedReadme).isEqualTo(readme);
    }

    @Test
    public void testQuitNowCacheNewIssueComment() {
        String newIssueComment = "the banana comment";

        CacheWrapper.setNewIssueComment("id", newIssueComment);
        String cacheNewComment = CacheWrapper.getIssueComment("id");

        assertThat(cacheNewComment).isEqualTo(newIssueComment);
    }

    @Test
    public void testQuitNowCacheRemoveNewIssue() {
        String newIssueComment = "the banana comment";

        CacheWrapper.setNewIssueComment("id", newIssueComment);
        CacheWrapper.clearIssueComment("id");
        String cacheNewComment = CacheWrapper.getIssueComment("id");

        assertNull(cacheNewComment, null);
    }
}
