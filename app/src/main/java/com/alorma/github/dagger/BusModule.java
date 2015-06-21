package com.alorma.github.dagger;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Bernat on 01/03/2015.
 */
@Module(
        library = true
)
public class BusModule {

    @Provides
    @Singleton
    public Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

}
