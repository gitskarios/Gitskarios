package com.alorma.github.injector.module;

import com.alorma.github.injector.named.ComputationScheduler;
import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.RepositorySourcePresenter;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module public class RepositorySourceModule {

  @Provides
  @PerActivity
  RepositorySourcePresenter provideRepository(@MainScheduler Scheduler mainScheduler, @ComputationScheduler Scheduler ioScheduler) {
    return new RepositorySourcePresenter(mainScheduler, ioScheduler);
  }
}
