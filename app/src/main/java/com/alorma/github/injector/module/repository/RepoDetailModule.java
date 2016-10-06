package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.RepositoryPresenter;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class RepoDetailModule {

    @Provides
    @PerActivity
    RepositoryPresenter provideRepositoryPresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler) {
        return new RepositoryPresenter(mainScheduler, ioScheduler);
    }
}
