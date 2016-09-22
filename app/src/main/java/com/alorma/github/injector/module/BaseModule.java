package com.alorma.github.injector.module;

import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class BaseModule {
    @Provides @PerActivity @MainScheduler
    Scheduler provideMainScheduler(){
        return AndroidSchedulers.mainThread();
    }

    @Provides @PerActivity @IOScheduler
    Scheduler provideIOScheduler(){
        return Schedulers.io();
    }
}
