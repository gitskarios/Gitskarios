package com.alorma.github.injector.module;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.CommitInfoPresenter;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class CommitDetailModule {

    @Provides
    @PerActivity
    CommitInfoPresenter provideCommitInfoPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler) {
        return new CommitInfoPresenter(mainScheduler, ioScheduler);
    }
}
