package com.alorma.github.injector.component;

import com.alorma.github.injector.PerActivity;
import com.alorma.github.injector.module.ActivityModule;
import com.alorma.github.injector.module.NotificationsModule;
import com.alorma.github.ui.fragment.NotificationsFragment;
import dagger.Component;

@PerActivity @Component(dependencies = ApplicationComponent.class, modules = {
    ActivityModule.class, NotificationsModule.class
}) public interface NotificationsComponent {
  void inject(NotificationsFragment notificationsFragment);
}
