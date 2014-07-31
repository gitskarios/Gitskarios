package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabTitle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.ui.adapter.detail.repo.RepoDetailPagerAdapter;
import com.alorma.github.ui.listeners.RefreshListener;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailFragment extends Fragment implements RefreshListener, View.OnClickListener, ViewPager.OnPageChangeListener, BaseClient.OnResultCallback<Repo> {
    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";
    public static final String FROM_INTENT_FILTER = "FROM_INTENT_FILTER";

    private String owner;
    private String repo;
    private SmoothProgressBar smoothBar;
    private ViewPager pager;
    private TabTitle tabReadme;
    private TabTitle tabSource;
    private TabTitle tabInfo;
    private List<TabTitle> tabs;
    private boolean fromIntentFilter;

    public static RepoDetailFragment newInstance(String owner, String repo) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);

        RepoDetailFragment f = new RepoDetailFragment();
        f.setArguments(bundle);
        return f;
    }

    public static RepoDetailFragment newInstance(String owner, String repo, boolean fromIntentFilter) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);
        bundle.putBoolean(FROM_INTENT_FILTER, fromIntentFilter);

        RepoDetailFragment f = new RepoDetailFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.repo_detail_fragment, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            owner = getArguments().getString(OWNER);
            repo = getArguments().getString(REPO);
            fromIntentFilter = getArguments().getBoolean(FROM_INTENT_FILTER, false);

            if (getActivity() != null && getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle(owner + "/" + repo);
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(!fromIntentFilter);
            }

            GetRepoClient repoClient = new GetRepoClient(getActivity(), owner, repo);
            repoClient.setOnResultCallback(this);
            repoClient.execute();

            smoothBar = (SmoothProgressBar) view.findViewById(R.id.smoothBar);

            tabReadme = (TabTitle) view.findViewById(R.id.tabReadme);
            tabSource = (TabTitle) view.findViewById(R.id.tabSource);
            tabInfo = (TabTitle) view.findViewById(R.id.tabInfo);

            tabReadme.setOnClickListener(this);
            tabSource.setOnClickListener(this);
            tabInfo.setOnClickListener(this);

            tabs = new ArrayList<TabTitle>();
            tabs.add(tabReadme);
            tabs.add(tabSource);
            tabs.add(tabInfo);

            pager = (ViewPager) view.findViewById(R.id.pager);
            pager.setOffscreenPageLimit(3);
            pager.setOnPageChangeListener(this);
            pager.setAdapter(new RepoDetailPagerAdapter(getFragmentManager(), owner, repo, this));

            selectButton(tabReadme);
        } else {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void showRefresh() {
        smoothBar.progressiveStart();
    }

    @Override
    public void cancelRefresh() {
        smoothBar.progressiveStop();
    }


    private void selectButton(TabTitle tabSelected) {
        for (TabTitle tab : tabs) {
            if (tab != null) {
                tab.setSelected(tab == tabSelected);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tabReadme:
                selectButton(tabReadme);
                pager.setCurrentItem(0);
                break;
            case R.id.tabSource:
                pager.setCurrentItem(1);
                selectButton(tabSource);
                break;
            case R.id.tabInfo:
                pager.setCurrentItem(2);
                selectButton(tabInfo);
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                selectButton(tabReadme);
                break;
            case 1:
                selectButton(tabSource);
                break;
            case 2:
                selectButton(tabInfo);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onResponseOk(Repo repo, Response r) {

    }

    @Override
    public void onFail(RetrofitError error) {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
