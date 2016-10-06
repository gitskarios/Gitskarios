package com.alorma.github.ui.tags;

import com.alorma.github.di.DaggerTestComponent;
import com.alorma.github.di.TestComponent;
import com.alorma.github.di.TestModule;
import com.alorma.github.di.tags.TagsTestModule;
import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.presenter.View;
import com.alorma.github.presenter.repos.releases.tags.RepositoryTagsPresenter;
import com.alorma.github.presenter.repos.releases.tags.TagsCacheDataSource;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.utils.BeanUtils;
import com.alorma.gitskarios.core.client.TokenProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import core.datasource.SdkItem;
import core.repositories.releases.tags.Tag;
import core.repositories.releases.tags.TagsCloudDataSource;
import core.repository.GenericRepository;
import rx.Observable;
import rx.Scheduler;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepositoryTagsPresenterTest {

  @Mock View<List<Tag>> view;
  @Inject TagsCacheDataSource tagsCacheDataSource;
  @Inject TagsCloudDataSource tagsCloudDataSource;
  @Inject @MainScheduler Scheduler mainScheduler;
  @Inject @IOScheduler Scheduler ioScheduler;
  RepositoryTagsPresenter repositoryTagsPresenter;
  RepoInfo testRepoInfo;
  SdkItem<RepoInfo> sdkItem;
  SdkItem<List<Tag>> sdkItemObsWithPage;
  List<Tag> listOfTags;
  Observable<SdkItem<List<Tag>>> sdkItemObs;
  Observable<SdkItem<List<Tag>>> sdkItemWithPageObs;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    TokenProvider.setTokenProviderInstance(() -> "test_token");
    // dagger 2
    TestComponent testComponent = DaggerTestComponent.builder().testModule(new TestModule()).build();
    testComponent.plus(new TagsTestModule()).inject(this);

    repositoryTagsPresenter = new RepositoryTagsPresenter(
            mainScheduler, ioScheduler,
            new GenericRepository<>(tagsCacheDataSource, tagsCloudDataSource));
    repositoryTagsPresenter.attachView(view);

    testRepoInfo = BeanUtils.createRepoInfo1();
    sdkItem = new SdkItem<>(testRepoInfo);
    listOfTags = new ArrayList<>();
    listOfTags.add(BeanUtils.createTag1());
    listOfTags.add(BeanUtils.createTag2());
    sdkItemObs = Observable.just(new SdkItem<>(listOfTags));
    sdkItemObsWithPage = new SdkItem<>(BeanUtils.TEST_PAGE, listOfTags);
    sdkItemWithPageObs = Observable.just(sdkItemObsWithPage);
  }

  @Test
  public void load_haveOnlyRepositoryInfo_successfullyLoadsTagsFromCloud() {
    // Given
    when(tagsCacheDataSource.getData(anyObject())).thenReturn(Observable.empty());
    when(tagsCloudDataSource.execute(anyObject())).thenReturn(sdkItemObs);

    // When
    repositoryTagsPresenter.execute(testRepoInfo);

    // Then
    verify(view).showLoading();
    verify(view).onDataReceived(eq(listOfTags), eq(false));
    verify(view).hideLoading();

    // Result
    assertNull(repositoryTagsPresenter.getPage());
  }

  @Test
  public void load_haveOnlyRepositoryInfo_successfullyLoadsTagsFromCache() {
    // Given
    when(tagsCacheDataSource.getData(anyObject())).thenReturn(sdkItemObs);
    when(tagsCloudDataSource.execute(anyObject())).thenReturn(Observable.empty());

    // When
    repositoryTagsPresenter.execute(testRepoInfo);

    // Then
    verify(view).showLoading();
    verify(view).onDataReceived(eq(listOfTags), eq(false));
    verify(view).hideLoading();

    // Result
    assertNull(repositoryTagsPresenter.getPage());
  }

  @Test
  public void load_noRepositoryInfo_failedInLoadingTags() {
    // Given
    when(tagsCacheDataSource.getData(anyObject())).thenReturn(Observable.empty());
    when(tagsCloudDataSource.execute(anyObject())).thenReturn(Observable.empty());

    // When
    repositoryTagsPresenter.execute(null);

    // Then
    verify(view).showLoading();
    verify(view).showError(any());
    verify(view).hideLoading();
    verify(view, never()).onDataReceived(eq(listOfTags), eq(false));

    // Result
    assertNull(repositoryTagsPresenter.getPage());
  }

  @Test
  public void loadMore_noPageInfo_noRequestsAndResponses() {
    // Given - Should never be called !!!
    when(tagsCacheDataSource.getData(anyObject())).thenThrow(new RuntimeException());
    when(tagsCloudDataSource.execute(anyObject())).thenThrow(new RuntimeException());

    // When
    boolean thrown = false;
    try {
      repositoryTagsPresenter.executePaginated(testRepoInfo);
    } catch (RuntimeException e) {
      thrown = true;
    }

    // Then
    assertFalse(thrown);
    verify(view, never()).showLoading();
    verify(view, never()).onDataReceived(eq(listOfTags), eq(true));
    verify(view, never()).hideLoading();
    verify(view, never()).showError(any());

    // Result
    assertNull(repositoryTagsPresenter.getPage());
  }

  @Test
  public void loadMore_repositoryInfoAndHasPage_successfullyLoadsTagsFromCloud() {
    // Given
    when(tagsCacheDataSource.getData(anyObject())).thenReturn(Observable.empty());
    when(tagsCloudDataSource.execute(anyObject())).thenReturn(sdkItemWithPageObs);

    // When
    repositoryTagsPresenter.execute(testRepoInfo);

    // Then
    verify(view, times(1)).showLoading();
    verify(view, times(1)).onDataReceived(eq(listOfTags), eq(false));
    verify(view, times(1)).hideLoading();
    verify(view, never()).showError(any());

    // Result
    assertNotNull(repositoryTagsPresenter.getPage());
    assertEquals(BeanUtils.TEST_PAGE, (int) repositoryTagsPresenter.getPage());

    // When LOAD MORE ITEMS
    repositoryTagsPresenter.executePaginated(testRepoInfo);
    verify(view, times(2)).showLoading();
    verify(view, times(1)).onDataReceived(eq(listOfTags), eq(true));
    verify(view, times(2)).hideLoading();
    verify(view, never()).showError(any());

    // Result
    assertNotNull(repositoryTagsPresenter.getPage());
    assertEquals(BeanUtils.TEST_PAGE, (int) repositoryTagsPresenter.getPage());
  }

  @Test
  public void loadMore_repositoryInfoAndHasPage_successfullyLoadsTagsFromCache() {
    // Given
    when(tagsCacheDataSource.getData(anyObject())).thenReturn(sdkItemWithPageObs);
    when(tagsCloudDataSource.execute(anyObject())).thenReturn(Observable.empty());

    // When
    repositoryTagsPresenter.execute(testRepoInfo);

    // Then
    verify(view, times(1)).showLoading();
    verify(view, times(1)).onDataReceived(eq(listOfTags), eq(false));
    verify(view, times(1)).hideLoading();
    verify(view, never()).showError(any());

    // Result
    assertNotNull(repositoryTagsPresenter.getPage());
    assertEquals(BeanUtils.TEST_PAGE, (int) repositoryTagsPresenter.getPage());

    // When LOAD MORE ITEMS
    repositoryTagsPresenter.executePaginated(testRepoInfo);
    verify(view, times(2)).showLoading();
    verify(view, times(1)).onDataReceived(eq(listOfTags), eq(true));
    verify(view, times(2)).hideLoading();
    verify(view, never()).showError(any());

    // Result
    assertNotNull(repositoryTagsPresenter.getPage());
    assertEquals(BeanUtils.TEST_PAGE, (int) repositoryTagsPresenter.getPage());
  }
}
