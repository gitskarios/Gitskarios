package com.alorma.github.cache;

import com.alorma.github.sdk.bean.dto.response.Branch;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheWrapperTest {

    @Test
    public void testQuitNowCacheBranches() {
        List<Branch> branches = new ArrayList<>();
        branches.add(new Branch());

        CacheWrapper.setBranches("id", branches);
        List<Branch> cachedBranches = CacheWrapper.getBranches("id");

        assertThat(cachedBranches.get(0)).isEqualTo(branches.get(0));
    }

    @Test
    public void testQuitNowCacheReadme() {
        String readme = "the banana readme";

        CacheWrapper.setReadme("id", readme);
        String cachedReadme = CacheWrapper.getReadme("id");

        assertThat(cachedReadme).isEqualTo(readme);
    }
}
