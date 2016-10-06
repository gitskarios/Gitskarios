package com.alorma.github.injector.component;

import com.alorma.github.injector.module.NavigationModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.NavigationFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = NavigationModule.class)
public interface NavigationComponent {
    void inject(NavigationFragment navigationFragment);
}
