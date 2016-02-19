package com.alorma.github.ui.activity;

import com.alorma.github.sdk.bean.dto.response.User;

public interface WelcomePresenterViewInterface {

  void onErrorUnauthorized();

  void onErrorTwoFactorException();

  void onGenericError();

  void finishAccess(User user);

  class NullView implements WelcomePresenterViewInterface {

    @Override
    public void onErrorUnauthorized() {

    }

    @Override
    public void onErrorTwoFactorException() {

    }

    @Override
    public void onGenericError() {

    }

    @Override
    public void finishAccess(User user) {

    }
  }

}