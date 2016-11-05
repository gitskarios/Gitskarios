package com.alorma.github.ui.activity.repo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.repository.RepositoryReadmeModule;
import com.alorma.github.presenter.RepositoryReadmePresenter;
import com.alorma.github.sdk.bean.ReadmeInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.utils.AttributesUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.inject.Inject;

public class RepoReadmeFragment extends BaseFragment implements com.alorma.github.presenter.View<String> {

  private static final String REPO_INFO = "REPO_INFO";
  private static final String SOFT_WRAP = "SOFT_WRAP";

  private RepoInfo repoInfo;
  private boolean softWrap;

  @Inject RepositoryReadmePresenter readmePresenter;

  @BindView(R.id.webview) WebView webView;
  @BindView(R.id.htmlLoading) View loadingView;

  public static RepoReadmeFragment newInstance(RepoInfo repoInfo) {
    return newInstance(repoInfo, false);
  }

  public static RepoReadmeFragment newInstance(RepoInfo repoInfo, boolean softWrap) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);
    bundle.putBoolean(SOFT_WRAP, softWrap);

    RepoReadmeFragment f = new RepoReadmeFragment();
    f.setArguments(bundle);
    return f;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.markdonw_file_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);

    if (getArguments() != null) {
      repoInfo = getArguments().getParcelable(REPO_INFO);
      softWrap = getArguments().getBoolean(SOFT_WRAP, false);
      ReadmeInfo readmeInfo = new ReadmeInfo();
      readmeInfo.setRepoInfo(repoInfo);
      readmeInfo.setTruncate(softWrap);
      readmePresenter.execute(readmeInfo);
    }
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();
    apiComponent.plus(new RepositoryReadmeModule()).inject(this);
    readmePresenter.attachView(this);
  }

  private void onReadmeLoaded(String htmlContent) {
    if (htmlContent != null) {
      htmlContent = configureHtml(htmlContent);
      webView.getSettings().setUseWideViewPort(false);
      webView.setWebViewClient(new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
          Intent intent = new IntentsManager(getActivity()).checkUri(Uri.parse(url));
          if (intent != null) {
            startActivity(intent);
          } else {
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setData(Uri.parse(url));
            startActivity(intent1);
          }
          return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new IntentsManager(getActivity()).checkUri(request.getUrl());
            if (intent != null) {
              startActivity(intent);
            } else {
              Intent intent1 = new Intent(Intent.ACTION_VIEW);
              intent1.setData(request.getUrl());
              startActivity(intent1);
            }
          }
          return true;
        }
      });

      int webviewColor = AttributesUtils.getWebviewColor(getActivity());
      webView.setBackgroundColor(webviewColor);

      webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

      loadingView.setVisibility(View.GONE);

      CacheWrapper.setReadme(repoInfo.toString(), htmlContent);
    }
  }

  private String configureHtml(String htmlContent) {
    if (getActivity() != null) {
      String fileName = "source_pre.html";

      if (isDarkTheme()) {
        fileName = "source_pre_dark.html";
      }

      String head = getAssetFileContent(fileName);
      String end = getAssetFileContent("source_post.html");

      return head + "\n" + htmlContent + "\n" + end;
    } else {
      return htmlContent;
    }
  }

  public String getAssetFileContent(String filename) {
    StringBuilder buf = new StringBuilder();
    try {
      InputStream stream = getActivity().getAssets().open(filename);
      BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
      String str;

      while ((str = in.readLine()) != null) {
        buf.append(str);
      }

      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return buf.toString();
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
  public void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideLoading() {
    loadingView.setVisibility(View.GONE);
  }

  @Override
  public void onDataReceived(String data, boolean isFromPaginated) {
    onReadmeLoaded(data);
  }

  @Override
  public void showError(Throwable throwable) {

  }
}
