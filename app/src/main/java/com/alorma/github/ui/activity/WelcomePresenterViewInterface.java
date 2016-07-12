package com.alorma.github.ui.activity;

import com.alorma.github.sdk.bean.dto.response.User;

public interface WelcomePresenterViewInterface {

  void willLogin();

  void onErrorUnauthorized();

  void onErrorTwoFactorException();

  void onGenericError();

  void finishAccess(User user);

  void didLogin();

  void onErrorTwoFactorAppException();

  class NullView implements WelcomePresenterViewInterface {

    @Override
    public void willLogin() {

    }

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

    @Override
    public void didLogin() {

    }

    @Override
    public void onErrorTwoFactorAppException() {

    }
  }

}