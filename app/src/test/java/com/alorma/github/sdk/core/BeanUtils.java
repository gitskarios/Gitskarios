package com.alorma.github.sdk.core;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.Sha;
import core.repositories.Permissions;
import core.repositories.releases.Release;
import core.repositories.releases.tags.Tag;

import static java.lang.Boolean.FALSE;

public class BeanUtils {
    public static final String SORT_ORDER_ASK = "asc";
    public static final int TEST_PAGE = 5;
    public static final int TEST_PAGE2 = 8;

    public static RepoInfo createRepoInfo(String repo, String branch, String owner) {
        RepoInfo repoInfo = new RepoInfo();
        repoInfo.name = repo;
        repoInfo.branch = branch;
        repoInfo.owner = owner;
        repoInfo.permissions = createPermissions(FALSE, FALSE, FALSE);
        return repoInfo;
    }

    public static RepoInfo createRepoInfo1() {
        return createRepoInfo(REPO_INFO_1.REPO, REPO_INFO_1.BRANCH, REPO_INFO_1.OWNER);
    }

    public static Permissions createPermissions(boolean isAdmin, boolean push, boolean pull) {
        Permissions permissions = new Permissions();
        permissions.admin = isAdmin;
        permissions.pull = pull;
        permissions.push = push;
        return permissions;
    }

    public static Sha createSha(String shaValue, String url) {
        Sha sha = new Sha();
        sha.sha = shaValue;
        sha.url = url;
        return sha;
    }

    public static Tag createTag(String tagName, Sha sha, String zipBall, String tarBall) {
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setSha(sha);
        tag.setZipballUrl(zipBall);
        tag.setTarballUrl(tarBall);
        return tag;
    }

    public static Tag createTag1() {
        return createTag(TAG_1.TEST_TAG1, TAG_1.TEST_TAG1_SHA, TAG_1.TEST_TAG1_ZIPBALL, TAG_1.TEST_TAG1_TARBALL);
    }

    public static Tag createTag2() {
        return createTag(TAG_2.TEST_TAG2, TAG_2.TEST_TAG2_SHA, TAG_2.TEST_TAG2_ZIPBALL, TAG_2.TEST_TAG2_TARBALL);
    }

    public static Release createRelease1() {
        Release release = new Release();
        release.setAssetsUrl(RELEASE_1.ASSET_URL);
        release.setUploadUrl(RELEASE_1.UPLOAD_URL);
        release.setZipballUrl(RELEASE_1.ZIPBALL);
        release.setTarballUrl(RELEASE_1.TARBALL);
        return release;
    }

    public static class RELEASE_1 {
        public static final String ASSET_URL = "test_asset_url";
        public static final String UPLOAD_URL = "test_upload_url";
        public static final String ZIPBALL = "test_zipball";
        public static final String TARBALL = "test_tarball";
    }

    public static class REPO_INFO_1 {
        public static final String REPO = "test_repo_info_1_repo";
        public static final String BRANCH = "test_repo_info_1_branch";
        public static final String OWNER = "test_repo_info_1_owner";
    }

    public static class TAG_1 {
        public static final String TEST_TAG1 = "test_tag1";
        public static final String TEST_TAG1_SHA_VALUE = "test_tag1_sha_value";
        public static final String TEST_TAG1_SHA_URL = "test_tag1_sha_url";
        public static Sha TEST_TAG1_SHA = createSha(TEST_TAG1_SHA_VALUE, TEST_TAG1_SHA_URL);
        public static final String TEST_TAG1_ZIPBALL = "test_tag1_zipball";
        public static final String TEST_TAG1_TARBALL = "test_tag1_tarball";
    }

    public static class TAG_2 {
        public static final String TEST_TAG2 = "test_tag2";
        public static final String TEST_TAG2_SHA_VALUE = "test_tag2_sha_value";
        public static final String TEST_TAG2_SHA_URL = "test_tag2_sha_url";
        public static Sha TEST_TAG2_SHA  = createSha(TEST_TAG2_SHA_VALUE, TEST_TAG2_SHA_URL);
        public static final String TEST_TAG2_ZIPBALL = "test_tag2_zipball";
        public static final String TEST_TAG2_TARBALL = "test_tag2_tarball";
    }
}
