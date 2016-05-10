package com.alorma.github.injector.component;

import android.app.Activity;
import com.alorma.github.injector.PerActivity;
import com.alorma.github.injector.module.ActivityModule;
import com.alorma.github.sdk.core.ApiClient;
import dagger.Component;

/**
 * A base component upon which fragment's components may depend. Activity-level components should
 * extend this component.
 * <p>
 * Subtypes of ActivityComponent should be decorated with annotation: {@link PerActivity}
 */
@PerActivity @Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

  // Publicly available
  Activity getActivity();
}