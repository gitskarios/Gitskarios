package com.alorma.github.ui.fragment.donate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BaseFragment;
import java.util.ArrayList;

public class DonateFragment extends BaseFragment {

  public ArrayList<DonateItem> skuList;
  @BindView(R.id.pager) ViewPager pager;

  public static Fragment newInstance() {
    return new DonateFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    skuList = new ArrayList<>();
    skuList.add(new DonateItem(SkuCardFragment.SKU_BASE_DONATE + ".smallest", 1));
    skuList.add(new DonateItem(SkuCardFragment.SKU_BASE_DONATE + ".small", 2));
    skuList.add(new DonateItem(SkuCardFragment.SKU_BASE_DONATE, 5));
    skuList.add(new DonateItem(SkuCardFragment.SKU_BASE_DONATE + ".big", 10));
    skuList.add(new DonateItem(SkuCardFragment.SKU_BASE_DONATE + ".awesome", 20));
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.donate_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);

    float baseEvelevation = getResources().getDimension(R.dimen.materialize_baseline_grid_small);

    SkuItemsAdapter adapter = new SkuItemsAdapter(getChildFragmentManager(), baseEvelevation, skuList);
    pager.setAdapter(adapter);

    ShadowTransformer mFragmentCardShadowTransformer = new ShadowTransformer(pager, adapter);
    pager.setPageTransformer(false, mFragmentCardShadowTransformer);

    pager.setOffscreenPageLimit(skuList.size());

    pager.setCurrentItem(skuList.size() / 2);
  }
}
