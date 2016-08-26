package com.alorma.github.ui.fragment.donate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.CardView;
import java.util.ArrayList;
import java.util.List;

public class SkuItemsAdapter extends FragmentPagerAdapter implements CardAdapter {

  private List<SkuCardFragment> mFragments;
  private float mBaseElevation;

  public SkuItemsAdapter(FragmentManager fm, float mBaseElevation, ArrayList<DonateItem> skuList) {
    super(fm);
    this.mBaseElevation = mBaseElevation;

    mFragments = new ArrayList<>(skuList.size());
    for (DonateItem donateItem : skuList) {
      SkuCardFragment fragment = SkuCardFragment.newInstance(donateItem);
      mFragments.add(fragment);
    }
  }

  @Override
  public int getCount() {
    return mFragments.size();
  }

  @Override
  public float getBaseElevation() {
    return mBaseElevation;
  }

  @Override
  public CardView getCardViewAt(int position) {
    return mFragments.get(position).getCardView();
  }

  @Override
  public Fragment getItem(int position) {
    return mFragments.get(position);
  }
}
