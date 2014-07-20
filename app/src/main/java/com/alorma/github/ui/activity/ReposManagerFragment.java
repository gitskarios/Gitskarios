package com.alorma.github.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumericTitle;
import android.widget.TabTitle;

import com.alorma.github.R;
import com.alorma.github.ui.fragment.repos.RepoFragmentType;
import com.alorma.github.ui.fragment.repos.ReposFragmentFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 17/07/2014.
 */
public class ReposManagerFragment extends Fragment implements View.OnClickListener {

    private List<TabTitle> tabs;
    private TabTitle tab1;
    private TabTitle tab2;
    private TabTitle tab3;

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

        tab1.performClick();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab1:
                selectButton(tab1);
                setContainerFragment(ReposFragmentFactory.getFragment(RepoFragmentType.REPO));
                break;
            case R.id.tab2:
                setContainerFragment(ReposFragmentFactory.getFragment(RepoFragmentType.WATCHED));
                selectButton(tab2);
                break;
            case R.id.tab3:
                setContainerFragment(ReposFragmentFactory.getFragment(RepoFragmentType.STARRED));
                selectButton(tab3);
                break;
        }
    }

    protected void setContainerFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }

    private void selectButton(TabTitle tabSelected) {
        for (TabTitle tab : tabs) {
            if (tab != null) {
                tab.setSelected(tab == tabSelected);
            }
        }
    }
}
