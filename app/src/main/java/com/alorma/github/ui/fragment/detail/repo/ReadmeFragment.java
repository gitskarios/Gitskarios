package com.alorma.github.ui.fragment.detail.repo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetReadmeContentsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import retrofit.RetrofitError;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Bernat on 22/07/2014.
 */
public class ReadmeFragment extends BaseFragment implements BranchManager, TitleProvider, PermissionsManager, BackManager {

  private static final String REPO_INFO = "REPO_INFO";
  private RepoInfo repoInfo;

  private TextView htmlContentView;

  private UpdateReceiver updateReceiver;

  public static ReadmeFragment newInstance(RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);

    ReadmeFragment f = new ReadmeFragment();
    f.setArguments(bundle);
    return f;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.readme_fragment, null);
    return v;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (getArguments() != null) {
      loadArguments();

      htmlContentView = (TextView) view.findViewById(R.id.htmlContentView);

      getContent();
    }
  }

  protected void loadArguments() {
    if (getArguments() != null) {
      repoInfo = getArguments().getParcelable(REPO_INFO);
    }
  }

  private void getContent() {
    loadReadme(new GetReadmeContentsClient(getActivity(), repoInfo));
  }

  private void loadReadme(GetReadmeContentsClient repoMarkdownClient) {
    repoMarkdownClient.observable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {

      }

      @Override
      public void onNext(String htmlContent) {
        if (htmlContent != null) {
          String htmlCode = HtmlUtils.format(htmlContent).toString();
          HttpImageGetter imageGetter = new HttpImageGetter(getActivity());

          imageGetter.repoInfo(repoInfo);
          imageGetter.bind(htmlContentView, htmlCode, repoInfo.hashCode());

          htmlContentView.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
        }
      }
    });
  }

  @Override
  public void setCurrentBranch(String branch) {
    if (getActivity() != null) {
      repoInfo.branch = branch;
      loadReadme(new GetReadmeContentsClient(getActivity(), repoInfo));
    }
  }

  private void onError(String tag, RetrofitError error) {
    ErrorHandler.onError(getActivity(), "MarkdownFragment: " + tag, error);
  }

  @Override
  public int getTitle() {
    return R.string.markdown_fragment_title;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_info;
  }

  public void reload() {
    getContent();
  }

  @Override
  public void onStart() {
    super.onStart();
    updateReceiver = new UpdateReceiver();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    getActivity().registerReceiver(updateReceiver, intentFilter);
  }

  @Override
  public void onStop() {
    super.onStop();
    getActivity().unregisterReceiver(updateReceiver);
  }

  @Override
  public void setPermissions(boolean admin, boolean push, boolean pull) {

  }

  @Override
  public boolean onBackPressed() {
    return true;
  }

  public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

      if (isOnline(context)) {
        reload();
      }
    }

    public boolean isOnline(Context context) {
      ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
      NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
    }
  }
}
