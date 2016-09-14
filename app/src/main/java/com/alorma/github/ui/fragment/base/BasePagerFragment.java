package com.alorma.github.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;

public abstract class BasePagerFragment extends BaseFragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return getThemedLayoutInflater(inflater).inflate(R.layout.general_pager, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabStrip);
    ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

    PagerAdapter pagerAdapter = provideAdapter(getChildFragmentManager());
    viewPager.setAdapter(pagerAdapter);
    viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
    tabLayout.setupWithViewPager(viewPager);
  }

  @Override
  public void onResume() {
    super.onResume();

    getActivity().setTitle(getTitle());
  }

  protected abstract PagerAdapter provideAdapter(FragmentManager fm);
}
