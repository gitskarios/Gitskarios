package com.alorma.github.ui.fragment.gists;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.sdk.services.gists.GetGistDetailClient;
import com.alorma.github.ui.actions.ShareAction;
import com.alorma.github.ui.activity.gists.CreateGistActivity;
import com.alorma.github.ui.activity.gists.GistsFileActivity;
import com.alorma.github.ui.adapter.GistDetailFilesAdapter;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;
import java.util.ArrayList;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GistDetailFragment extends BaseFragment implements Observer<Gist>, RecyclerArrayAdapter.ItemCallback<GistFile> {

  public static final String GIST_ID = "GIST_ID";
  private GistDetailFilesAdapter adapter;
  private GistDetailListener gistDetailListener;
  private Gist gist;

  public static GistDetailFragment newInstance(String id) {
    Bundle bundle = new Bundle();
    bundle.putString(GIST_ID, id);

    GistDetailFragment f = new GistDetailFragment();
    f.setArguments(bundle);
    return f;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.gist_detail_layout, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
    recyclerView.setLayoutManager(
        new StaggeredGridLayoutManager(getResources().getInteger(R.integer.gist_files_count), StaggeredGridLayoutManager.VERTICAL));

    adapter = new GistDetailFilesAdapter(LayoutInflater.from(getActivity()));
    adapter.setCallback(this);
    recyclerView.setAdapter(adapter);

    if (getArguments() != null) {
      String id = getArguments().getString(GIST_ID);

      GetGistDetailClient detailClient = new GetGistDetailClient(id);
      detailClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this);
    }
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Gists;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Gists;
  }

  @Override
  public void onNext(Gist gist) {
    this.gist = gist;
    if (gistDetailListener != null) {
      gistDetailListener.onGistLoaded(gist);
    }
    adapter.addAll(new ArrayList<GistFile>(gist.files.values()));
  }

  @Override
  public void onError(Throwable e) {

  }

  @Override
  public void onCompleted() {

  }

  public int getMenuId() {
    return R.menu.menu_gist;
  }

  @Override
  public void onStart() {
    super.onStart();
    enableCreateGist(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item != null && item.getItemId() == R.id.action_gist_share) {
      Uri uri = Uri.parse(gist.html_url);
      new ShareAction(getActivity(), gist.description, uri.toString()).setType("Gist").execute();
    }
    return true;
  }

  @Override
  public void onStop() {
    enableCreateGist(true);
    super.onStop();
  }

  private void enableCreateGist(boolean b) {
    int flag = b ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

    ComponentName componentName = new ComponentName(getActivity(), CreateGistActivity.class);
    getActivity().getPackageManager().setComponentEnabledSetting(componentName, flag, PackageManager.DONT_KILL_APP);
  }

  public void setGistDetailListener(GistDetailListener gistDetailListener) {
    this.gistDetailListener = gistDetailListener;
  }

  @Override
  public void onItemSelected(GistFile item) {
    Intent launcherIntent = GistsFileActivity.createLauncherIntent(getActivity(), item.filename, item.content);
    startActivity(launcherIntent);
  }

  public interface GistDetailListener {
    void onGistLoaded(Gist gist);
  }
}
