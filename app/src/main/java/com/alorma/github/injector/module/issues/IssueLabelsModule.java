package com.alorma.github.injector.module.issues;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.issue.IssueLabelsPresenter;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class IssueLabelsModule {

    @Provides
    @PerActivity
    IssueLabelsPresenter provideIssueLabelsPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler) {
        return new IssueLabelsPresenter(mainScheduler, ioScheduler);
    }
}
