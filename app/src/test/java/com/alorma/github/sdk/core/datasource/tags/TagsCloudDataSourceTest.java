package com.alorma.github.sdk.core.datasource.tags;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.core.BeanUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.ApiClient;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.repositories.releases.Release;
import core.repositories.releases.tags.RepositoryTagsService;
import core.repositories.releases.tags.Tag;
import core.repositories.releases.tags.TagsCloudDataSource;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;
import rx.observers.TestSubscriber;

import static com.alorma.github.sdk.core.BeanUtils.SORT_ORDER_ASK;
import static com.alorma.github.sdk.core.BeanUtils.TEST_PAGE;
import static com.alorma.github.sdk.core.BeanUtils.TEST_PAGE2;
import static com.alorma.github.sdk.core.BeanUtils.createRelease1;
import static com.alorma.github.sdk.core.BeanUtils.createRepoInfo1;
import static com.alorma.github.sdk.core.BeanUtils.createTag1;
import static com.alorma.github.sdk.core.BeanUtils.createTag2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TagsCloudDataSourceTest {

    private TestRestWrapper testRestWrapper;
    private TagsCloudDataSource tagsCloudDataSource;

    @Mock
    private RepositoryTagsService mockRepositoryTagsService;
    SdkItem<RepoInfo> sdkItem;
    SdkItem<RepoInfo> sdkItemWithPage;
    RepoInfo repoInfo;
    Call<List<Tag>> tagsCall1;
    Response<List<Tag>> tagsResponse1;
    Call<List<Tag>> tagsCall2;
    Response<List<Tag>> tagsResponse2;
    Call<Release> releaseResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        repoInfo = createRepoInfo1();

        List<Tag> listTags = new ArrayList<>();
        listTags.add(createTag1());
        listTags.add(createTag2());
        tagsResponse1 = Response.success(listTags);
        tagsCall1 = Calls.response(tagsResponse1);

        List<Tag> listTagsReverse = new ArrayList<>();
        listTagsReverse.add(createTag2());
        listTagsReverse.add(createTag1());
        tagsResponse2 = Response.success(listTagsReverse);
        tagsCall2 = Calls.response(tagsResponse2);

        Release release = createRelease1();
        releaseResponse = Calls.response(release);

        sdkItem = new SdkItem<>(repoInfo);
        sdkItemWithPage = new SdkItem<>(TEST_PAGE, repoInfo);
        testRestWrapper = new TestRestWrapper();
        tagsCloudDataSource = new TagsCloudDataSource(testRestWrapper, SORT_ORDER_ASK);
    }

    @Test
    public void execute_twoTagsAndFirstWithRelease_returnsProperRepositoryTags() {
        // Given
        when(mockRepositoryTagsService.tags(anyString(), anyString(), anyString()))
                .thenReturn(tagsCall1);
        when(mockRepositoryTagsService.release(anyString(), anyString(), eq(BeanUtils.TAG_1.TEST_TAG1)))
                .thenReturn(releaseResponse);
        when(mockRepositoryTagsService.release(anyString(), anyString(), eq(BeanUtils.TAG_2.TEST_TAG2)))
                .thenReturn(Calls.response((Release) null));

        // When
        TestSubscriber<SdkItem<List<Tag>>> subscriber = new TestSubscriber<>();
        tagsCloudDataSource.execute(sdkItem).subscribe(subscriber);

        // Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(mockRepositoryTagsService)
                .tags(BeanUtils.REPO_INFO_1.OWNER, BeanUtils.REPO_INFO_1.REPO, SORT_ORDER_ASK);
        verify(mockRepositoryTagsService)
                .release(eq(BeanUtils.REPO_INFO_1.OWNER), eq(BeanUtils.REPO_INFO_1.REPO), eq(BeanUtils.TAG_1.TEST_TAG1));
        verify(mockRepositoryTagsService)
                .release(eq(BeanUtils.REPO_INFO_1.OWNER), eq(BeanUtils.REPO_INFO_1.REPO), eq(BeanUtils.TAG_2.TEST_TAG2));

        // Result
        List<SdkItem<List<Tag>>> onNextEvents = subscriber.getOnNextEvents();
        // was only one call to API
        assertEquals(1, onNextEvents.size());
        SdkItem<List<Tag>> sdkItem = onNextEvents.get(0);
        // we have 2 tags on list
        List<Tag> tags = sdkItem.getK();
        assertEquals(2, tags.size());

        // check tag 1
        Tag tag1 = tags.get(0);
        Release release1 = tag1.release;
        checkRelease1(release1);

        // check tag 1
        Tag tag2 = tags.get(1);
        checkTag2(tag2);
        assertNull(tag2.release);
    }

    @Test
    public void execute_twoTagsAndSecondWithRelease_returnsNextPageOfRepositoryTags() {
        // Given
        when(mockRepositoryTagsService.tags(anyString(), anyString(), eq(TEST_PAGE), anyString()))
                .thenReturn(tagsCall2);
        when(mockRepositoryTagsService.release(anyString(), anyString(), eq(BeanUtils.TAG_2.TEST_TAG2)))
                .thenReturn(releaseResponse);
        when(mockRepositoryTagsService.release(anyString(), anyString(), eq(BeanUtils.TAG_1.TEST_TAG1)))
                .thenReturn(Calls.response((Release) null));

        // When
        TestSubscriber<SdkItem<List<Tag>>> subscriber = new TestSubscriber<>();
        tagsCloudDataSource.execute(sdkItemWithPage).subscribe(subscriber);

        // Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(mockRepositoryTagsService)
                .tags(BeanUtils.REPO_INFO_1.OWNER, BeanUtils.REPO_INFO_1.REPO, TEST_PAGE, SORT_ORDER_ASK);
        verify(mockRepositoryTagsService)
                .release(eq(BeanUtils.REPO_INFO_1.OWNER), eq(BeanUtils.REPO_INFO_1.REPO), eq(BeanUtils.TAG_2.TEST_TAG2));
        verify(mockRepositoryTagsService)
                .release(eq(BeanUtils.REPO_INFO_1.OWNER), eq(BeanUtils.REPO_INFO_1.REPO), eq(BeanUtils.TAG_1.TEST_TAG1));

        // Result
        List<SdkItem<List<Tag>>> onNextEvents = subscriber.getOnNextEvents();
        // was only one call to API
        assertEquals(1, onNextEvents.size());
        SdkItem<List<Tag>> sdkItem = onNextEvents.get(0);
        // we have 2 tags on list
        List<Tag> tags = sdkItem.getK();
        assertEquals(2, tags.size());
        assertEquals(TEST_PAGE2, (int) sdkItem.getPage());

        // check tag 1
        Tag tag2 = tags.get(0);
        checkTag2(tag2);
        Release release1 = tag2.release;
        checkRelease1(release1);

        // check tag 2
        Tag tag1 = tags.get(1);
        assertNull(tag1.release);
        checkTag1(tag1);
    }

    @Test
    public void execute_onlyRepositoryInfo_403ForbiddenError() {
        // Given
        when(mockRepositoryTagsService.tags(anyString(), anyString(), anyString()))
                .thenThrow(get403ForbiddenError());

        // When
        TestSubscriber<SdkItem<List<Tag>>> subscriber = new TestSubscriber<>();
        tagsCloudDataSource.execute(sdkItem).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertError(HttpException.class);

        verify(mockRepositoryTagsService)
                .tags(BeanUtils.REPO_INFO_1.OWNER, BeanUtils.REPO_INFO_1.REPO, SORT_ORDER_ASK);

        verify(mockRepositoryTagsService, never())
                .release(anyString(), anyString(), anyString());
    }

    @Test
    public void execute_twoTagsAndFirstWithRelease_IOExceptionOnFetchingReleases() throws IOException {
        // Given
        when(mockRepositoryTagsService.tags(anyString(), anyString(), anyString()))
                .thenReturn(tagsCall1);
        when(mockRepositoryTagsService.release(anyString(), anyString(), eq(BeanUtils.TAG_1.TEST_TAG1)))
                .thenReturn(Calls.failure(new IOException()));
        when(mockRepositoryTagsService.release(anyString(), anyString(), eq(BeanUtils.TAG_2.TEST_TAG2)))
                .thenReturn(releaseResponse);

        // When
        TestSubscriber<SdkItem<List<Tag>>> subscriber = new TestSubscriber<>();
        tagsCloudDataSource.execute(sdkItem).subscribe(subscriber);

        // Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(mockRepositoryTagsService)
                .tags(BeanUtils.REPO_INFO_1.OWNER, BeanUtils.REPO_INFO_1.REPO, SORT_ORDER_ASK);
        verify(mockRepositoryTagsService)
                .release(eq(BeanUtils.REPO_INFO_1.OWNER), eq(BeanUtils.REPO_INFO_1.REPO), eq(BeanUtils.TAG_1.TEST_TAG1));
        verify(mockRepositoryTagsService)
                .release(eq(BeanUtils.REPO_INFO_1.OWNER), eq(BeanUtils.REPO_INFO_1.REPO), eq(BeanUtils.TAG_2.TEST_TAG2));

        // Result
        List<SdkItem<List<Tag>>> onNextEvents = subscriber.getOnNextEvents();
        // was only one call to API
        assertEquals(1, onNextEvents.size());
        SdkItem<List<Tag>> sdkItem = onNextEvents.get(0);
        // we have 2 tags on list
        List<Tag> tags = sdkItem.getK();
        assertEquals(2, tags.size());

        // check tag 1
        Tag tag1 = tags.get(0);
        assertNull(tag1.release);
        checkTag1(tag1);

        // check tag 2
        Tag tag2 = tags.get(1);
        checkRelease1(tag2.release);
        checkTag2(tag2);
    }

    private HttpException get403ForbiddenError() {
        return new HttpException(Response.error(403, ResponseBody.create(MediaType.parse("application/json"), "Forbidden")));

    }

    private void checkTag2(Tag tag2) {
        assertEquals(BeanUtils.TAG_2.TEST_TAG2, tag2.getName());
        assertEquals(BeanUtils.TAG_2.TEST_TAG2_ZIPBALL, tag2.getZipballUrl());
        assertEquals(BeanUtils.TAG_2.TEST_TAG2_TARBALL, tag2.getTarballUrl());
        assertEquals(BeanUtils.TAG_2.TEST_TAG2_SHA, tag2.getSha());
        assertEquals(BeanUtils.TAG_2.TEST_TAG2_SHA_VALUE, tag2.getSha().getSha());
        assertEquals(BeanUtils.TAG_2.TEST_TAG2_SHA_URL, tag2.getSha().getUrl());
    }

    private void checkTag1(Tag tag1) {
        assertEquals(BeanUtils.TAG_1.TEST_TAG1, tag1.getName());
        assertEquals(BeanUtils.TAG_1.TEST_TAG1_ZIPBALL, tag1.getZipballUrl());
        assertEquals(BeanUtils.TAG_1.TEST_TAG1_TARBALL, tag1.getTarballUrl());
        assertEquals(BeanUtils.TAG_1.TEST_TAG1_SHA, tag1.getSha());
        assertEquals(BeanUtils.TAG_1.TEST_TAG1_SHA_VALUE, tag1.getSha().getSha());
        assertEquals(BeanUtils.TAG_1.TEST_TAG1_SHA_URL, tag1.getSha().getUrl());
    }

    private void checkRelease1(Release release1) {
        assertEquals(BeanUtils.RELEASE_1.ASSET_URL, release1.getAssetsUrl());
        assertEquals(BeanUtils.RELEASE_1.UPLOAD_URL, release1.getUploadUrl());
        assertEquals(BeanUtils.RELEASE_1.ZIPBALL, release1.getZipballUrl());
        assertEquals(BeanUtils.RELEASE_1.TARBALL, release1.getTarballUrl());
    }

    private class TestRestWrapper extends RestWrapper {
        public TestRestWrapper() {
            super(null);
        }

        @Override
        protected RepositoryTagsService get(ApiClient apiClient) {
            return mockRepositoryTagsService;
        }

        @Override
        public boolean isPaginated(Response response) {
            if (isResponse2(response)) return true;
            return false;
        }

        @Override
        public Integer getPage(Response response) {
            if (isResponse2(response)) return TEST_PAGE2;
            return 0;
        }

        private boolean isResponse2(Response response) {
            if (tagsResponse2.equals(response)) {
                return true;
            }
            return false;
        }
    }

    private class HttpException extends RuntimeException {
        public HttpException(Response<Object> forbidden) {
        }
    }
}