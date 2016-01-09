package com.clean.presenter;

/**
 * Created by bernat.borras on 12/11/15.
 */
public abstract class Presenter<Request, Response> {

    public abstract void load(Request request, Callback<Response> responseCallback);

    public interface Callback<Response> {

        void showLoading();

        void onResponse(Response response);

        void hideLoading();
    }
}
