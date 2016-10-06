package com.alorma.github.injector.component;

import com.alorma.github.injector.module.UserNotificationsModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = UserNotificationsModule.class)
public interface UserNotificationsComponent {
    void inject(NotificationsFragment fragment);
}
