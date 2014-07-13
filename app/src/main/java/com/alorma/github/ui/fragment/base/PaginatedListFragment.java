package com.alorma.github.ui.fragment.base;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.alorma.github.BuildConfig;
import com.alorma.github.sdk.bean.Link;
import com.alorma.github.sdk.bean.RelType;
import com.alorma.github.sdk.services.client.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public abstract class PaginatedListFragment<K> extends ListFragment implements BaseClient.OnResultCallback<K>, AbsListView.OnScrollListener {

    protected static final String USERNAME = "USERNAME";

    private Link bottomLink;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(this);

        executeRequest();
    }

    protected abstract void executeRequest();

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int first, int last, int total) {
        if (total > 0 && first + last == total) {
            if (bottomLink != null && bottomLink.rel == RelType.next) {
                executePaginatedRequest(bottomLink.page);
                bottomLink = null;
            }
        }
    }

    protected abstract void executePaginatedRequest(int page);

    @Override
    public void onResponseOk(K k, Response r) {
        if (getActivity() != null && isAdded()) {
            onResponse(k);

            getLinkData(r);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(getActivity(), "failed: " + error, Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract void onResponse(K k);

    private void getLinkData(Response r) {
        List<Header> headers = r.getHeaders();
        Map<String, String> headersMap = new HashMap<String, String>(headers.size());
        for (Header header : headers) {
            headersMap.put(header.getName(), header.getValue());
        }

        String link = headersMap.get("Link");

        if (link != null) {
            String[] parts = link.split(",");
            try {
                bottomLink = new Link(parts[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
