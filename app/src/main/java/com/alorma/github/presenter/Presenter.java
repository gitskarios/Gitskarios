package com.alorma.github.presenter;

public abstract class Presenter<Request, Response> {

  public abstract void load(Request request, Callback<Response> responseCallback);

  public interface Callback<Response> {

    void showLoading();

    void onResponse(Response response);

    void hideLoading();
  }
}
