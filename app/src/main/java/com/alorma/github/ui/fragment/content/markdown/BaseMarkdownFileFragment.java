package com.alorma.github.ui.fragment.content.markdown;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.utils.AttributesUtils;
import java.io.UnsupportedEncodingException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseMarkdownFileFragment extends BaseFragment {

  public static final String FILE_INFO = "FILE_INFO";

  private WebView webView;

  private FileInfo fileInfo;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.markdonw_file_fragment, null, false);
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
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    webView = (WebView) view.findViewById(R.id.webview);

    webView.getSettings().setBuiltInZoomControls(true);
    webView.getSettings().setSupportZoom(true);

    int webviewColor = AttributesUtils.getWebviewColor(getActivity());
    webView.setBackgroundColor(webviewColor);

    if (getArguments() != null) {

      fileInfo = getArguments().getParcelable(FILE_INFO);
      if (fileInfo == null) {
        getActivity().finish();
      }

      webView.clearCache(true);
      webView.clearFormData();
      webView.clearHistory();
      webView.clearMatches();
      webView.clearSslPreferences();
      webView.setVisibility(View.VISIBLE);

      new IntentsManager(getActivity()).manageUrls(webView);

      if (fileInfo.content == null) {
        getContent();
      }
    }
  }

  private void setSourceIntoWebview(String content) {
    webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
  }

  protected void getContent() {
    if (fileInfo.repoInfo != null) {
      getContentObservable(fileInfo).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::setSourceIntoWebview, Throwable::printStackTrace);
    }
  }

  protected abstract Observable<String> getContentObservable(FileInfo fileInfo);

  protected String decodeContent(String encoded) {
    String decoded = encoded;
    byte[] data = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT);
    try {
      decoded = new String(data, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return decoded;
  }
}
