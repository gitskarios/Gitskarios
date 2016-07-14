package com.alorma.github.ui.fragment.releases;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ReleaseAsset;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.ui.renderers.releases.assets.ReleaseAssetRendererBuilder;
import com.alorma.github.ui.renderers.releases.assets.ReleaseAssetsRenderer;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import java.util.ArrayList;
import java.util.List;

public class ReleaseAssetsFragment extends BaseFragment implements ReleaseAssetsRenderer.OnReleaseAssetClicked, TitleProvider {

  private static final String RELEASE_ASSETS = "RELEASE_ASSETS";
  private ReleaseAssetCallback releaseAssetCallback;

  public static ReleaseAssetsFragment newInstance(List<ReleaseAsset> releaseAssets) {
    ReleaseAssetsFragment releaseAssetsFragment = new ReleaseAssetsFragment();

    Bundle args = new Bundle();
    args.putParcelableArrayList(RELEASE_ASSETS, new ArrayList<Parcelable>(releaseAssets));

    releaseAssetsFragment.setArguments(args);

    return releaseAssetsFragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.release_assets_fragment, null, false);
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    List<ReleaseAsset> assets = getArguments().getParcelableArrayList(RELEASE_ASSETS);

    if (assets != null) {
      RVRendererAdapter<ReleaseAsset> adapter =
          new RVRendererAdapter<>(LayoutInflater.from(getActivity()), new ReleaseAssetRendererBuilder(this),
              new ListAdapteeCollection<>(assets));

      recyclerView.setAdapter(adapter);
    }
  }

  @Override
  public void onReleaseAssetCLicked(ReleaseAsset asset) {
    if (releaseAssetCallback != null) {
      releaseAssetCallback.onReleaseDownloadRequest(asset);
    }
  }

  @Override
  public int getTitle() {
    return R.string.repo_release_fragment_assets_title;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_package;
  }

  public void setReleaseAssetCallback(ReleaseAssetCallback releaseAssetCallback) {
    this.releaseAssetCallback = releaseAssetCallback;
  }

  public interface ReleaseAssetCallback {
    void onReleaseDownloadRequest(ReleaseAsset asset);
  }
}
