package com.alorma.github.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.sdk.core.User;
import com.alorma.github.ui.fragment.base.BaseFragment;
import java.util.List;
import javax.inject.Inject;

public class NavigationFragment extends BaseFragment implements Presenter.Callback<List<User>> {
  @Inject NavigationProfilesPresenter navigationProfilesPresenter;

  private NavigationCallback navigationCallbackNull = organizations -> {
  };
  private NavigationCallback navigationCallback;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
    navigationProfilesPresenter.load(null, this);
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);

    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();

    initInjectors(apiComponent);
  }

  protected void initInjectors(ApiComponent apiComponent) {
    apiComponent.inject(this);
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void onResponse(List<User> users, boolean firstTime) {
    if (getActivity() instanceof NavigationCallback) {
      navigationCallback = (NavigationCallback) getActivity();
    }
    navigationCallback.onOrganizationsLoaded(users);
  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onResponseEmpty() {

  }

  public void setNavigationCallback(NavigationCallback navigationCallback) {
    if (navigationCallback == null) {
      navigationCallback = navigationCallbackNull;
    }
    this.navigationCallback = navigationCallback;
  }

  public interface NavigationCallback {
    void onOrganizationsLoaded(List<User> organizations);
  }
}
