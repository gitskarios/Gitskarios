package com.alorma.github.injector.module.repository;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.ui.activity.issue.IssueMilestonePresenter;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class RepositoryMilestonesModule {

    private MilestoneState milestoneState;

    public RepositoryMilestonesModule(MilestoneState state) {
        milestoneState = state;
    }

    @Provides
    @PerActivity
    IssueMilestonePresenter provideIssueMilestonePresenter(
            @MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler) {
        return new IssueMilestonePresenter(mainScheduler, ioScheduler, milestoneState);
    }
}
