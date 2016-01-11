package com.alorma.github.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.gists.UserGistsClient;
import com.alorma.github.ui.activity.gists.CreateGistActivity;
import com.alorma.github.ui.adapter.GistsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GistsFragment extends LoadingListFragment<GistsAdapter> implements GistsAdapter.GistsAdapterListener {

    private static final String USERNAME = "USERNAME";
    public GistsFragmentListener gistsFragmentListener;
    private String username;

    public static GistsFragment newInstance(String username) {
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME, username);

        GistsFragment f = new GistsFragment();
        f.setArguments(bundle);

        return f;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GistsAdapter adapter = new GistsAdapter(LayoutInflater.from(getActivity()));
        adapter.setGistsAdapterListener(this);
        setAdapter(adapter);
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_gist;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_gists;
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        setAction(new UserGistsClient(username));
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        setAction(new UserGistsClient(username, page));
    }

    private void setAction(GithubListClient<List<Gist>> userGistsClient) {
        userGistsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<Pair<List<Gist>, Integer>, List<Gist>>() {
            @Override
            public List<Gist> call(Pair<List<Gist>, Integer> listIntegerPair) {
                setPage(listIntegerPair.second);
                return listIntegerPair.first;
            }
        }).subscribe(new Subscriber<List<Gist>>() {
            @Override
            public void onCompleted() {
                stopRefresh();
            }

            @Override
            public void onError(Throwable e) {
                stopRefresh();
                if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                    setEmpty();
                }
            }

            @Override
            public void onNext(List<Gist> gists) {
                getAdapter().addAll(gists);
            }
        });
    }

    @Override
    protected Octicons.Icon getFABGithubIcon() {
        return Octicons.Icon.oct_plus;
    }

    @Override
    protected boolean useFAB() {
        return username == null;
    }

    public void setGistsFragmentListener(GistsFragmentListener gistsFragmentListener) {
        this.gistsFragmentListener = gistsFragmentListener;
    }

    @Override
    public void onGistSelected(Gist gist) {
        if (gistsFragmentListener != null) {
            gistsFragmentListener.onGistsRequest(gist);
        }
    }

    @Override
    protected void fabClick() {
        try {
            Intent intent = CreateGistActivity.createLauncherIntent(getActivity());
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface GistsFragmentListener {
        void onGistsRequest(Gist gist);
    }
}