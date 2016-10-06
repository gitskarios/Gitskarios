package com.alorma.github.injector.module.issues;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.named.Token;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.issue.EditIssueCommentDataSource;
import com.alorma.github.presenter.issue.IssueCommentBaseRxPresenter;

import core.ApiClient;
import core.GithubComment;
import core.datasource.CloudDataSource;
import core.issue.EditIssueCommentBodyRequest;
import core.issue.IssuesCommentsRetrofitWrapper;
import core.repository.GenericRepository;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class IssueDetailModule {

    @Provides
    @PerActivity
    IssuesCommentsRetrofitWrapper provideRest(ApiClient apiClient, @Token String token) {
        return new IssuesCommentsRetrofitWrapper(apiClient, token);
    }

    @Provides
    @PerActivity
    IssueCommentBaseRxPresenter provideIssueCommentBaseRxPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
            IssuesCommentsRetrofitWrapper retrofitWrapper) {

        CloudDataSource<EditIssueCommentBodyRequest, GithubComment> api =
                new EditIssueCommentDataSource(retrofitWrapper);

        return new IssueCommentBaseRxPresenter(
                mainScheduler, ioScheduler, new GenericRepository<>(null, api));
    }


}
