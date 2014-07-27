package com.alorma.github.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabTitle;

import com.alorma.github.R;
import com.alorma.github.ui.adapter.repos.ReposPagerAdapter;
import com.alorma.github.ui.fragment.repos.RepoFragmentType;
import com.alorma.github.ui.fragment.repos.ReposFragmentFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 17/07/2014.
 */
public class ReposManagerFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private List<TabTitle> tabs;
    private TabTitle tab1;
    private TabTitle tab2;
    private TabTitle tab3;
    private ViewPager pager;

    public static Fragment newInstance() {
        return new ReposManagerFragment();
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
}
