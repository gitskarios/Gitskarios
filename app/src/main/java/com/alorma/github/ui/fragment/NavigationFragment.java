package com.alorma.github.ui.fragment;

import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.NavigationModule;
import com.alorma.github.presenter.NavigationProfilesPresenter;
import com.alorma.github.presenter.View;
import com.alorma.github.ui.fragment.base.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import core.User;

public class NavigationFragment extends BaseFragment implements View<List<User>> {
  @Inject
  NavigationProfilesPresenter navigationProfilesPresenter;

  private NavigationCallback navigationCallbackNull = organizations -> {
  };
  private NavigationCallback navigationCallback;

  @Override
  public void onResume() {
    super.onResume();
    navigationProfilesPresenter.attachView(this);
    navigationProfilesPresenter.execute(nameProvider.getName());
  }

  @Override
  public void onPause() {
    super.onPause();
    navigationProfilesPresenter.detachView();
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);

    applicationComponent.inject(this);

    ApiComponent apiComponent =
            DaggerApiComponent.builder()
                    .applicationComponent(applicationComponent)
                    .apiModule(new ApiModule())
                    .build();

    initInjectors(apiComponent);
  }

  protected void initInjectors(ApiComponent apiComponent) {
    apiComponent
            .plus(new NavigationModule())
            .inject(this);
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void onDataReceived(List<User> users, boolean isFromPaginated) {
    if (getActivity() instanceof NavigationCallback) {
      navigationCallback = (NavigationCallback) getActivity();
    }
    navigationCallback.onOrganizationsLoaded(users);
  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void showError(Throwable throwable) {

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
