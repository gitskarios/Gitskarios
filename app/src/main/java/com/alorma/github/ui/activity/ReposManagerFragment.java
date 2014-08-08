package com.alorma.github.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TabTitle;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.search.ReposSearch;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.search.RepoSearchClient;
import com.alorma.github.ui.activity.search.SearchReposActivity;
import com.alorma.github.ui.adapter.repos.ReposPagerAdapter;
import com.alorma.github.ui.fragment.repos.RepoFragmentType;
import com.alorma.github.ui.fragment.repos.ReposFragmentFactory;
import com.alorma.github.ui.fragment.search.SearchReposFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 17/07/2014.
 */
public class ReposManagerFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener, SearchView.OnQueryTextListener {

    private List<TabTitle> tabs;
    private TabTitle tab1;
    private TabTitle tab2;
    private TabTitle tab3;
    private ViewPager pager;

    public static Fragment newInstance() {
        return new ReposManagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.repos_fragment_manager, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tab1 = (TabTitle) view.findViewById(R.id.tab1);
        tab2 = (TabTitle) view.findViewById(R.id.tab2);
        tab3 = (TabTitle) view.findViewById(R.id.tab3);

        tabs = new ArrayList<TabTitle>();
        tabs.add(tab1);
        tabs.add(tab2);
        tabs.add(tab3);

        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);

        selectButton(tab1);

        pager = (ViewPager) view.findViewById(R.id.content);
        pager.setOnPageChangeListener(this);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(new ReposPagerAdapter(getFragmentManager()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_repos, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        IconDrawable iconSearch = new IconDrawable(getActivity(), Iconify.IconValue.fa_search);
        iconSearch.actionBarSize();
        iconSearch.color(Color.WHITE);
        searchItem.setIcon(iconSearch);


        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setIconified(true);

        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_search) {
            Log.i("ALORMA", "HELLO search");
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab1:
                selectButton(tab1);
                pager.setCurrentItem(0);
                break;
            case R.id.tab2:
                pager.setCurrentItem(1);
                selectButton(tab2);
                break;
            case R.id.tab3:
                pager.setCurrentItem(2);
                selectButton(tab3);
                break;
        }
    }

    private void selectButton(TabTitle tabSelected) {
        for (TabTitle tab : tabs) {
            if (tab != null) {
                tab.setSelected(tab == tabSelected);
            }
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                selectButton(tab1);
                break;
            case 1:
                selectButton(tab2);
                break;
            case 2:
                selectButton(tab3);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent reposActivity = new Intent(getActivity(), SearchReposActivity.class);
        reposActivity.putExtra(SearchManager.QUERY, query);
        startActivity(reposActivity);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
