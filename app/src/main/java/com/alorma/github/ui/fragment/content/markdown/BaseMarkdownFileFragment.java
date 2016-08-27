package com.alorma.github.ui.fragment.content.markdown;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.fragment.content.BaseFileFragment;
import com.alorma.github.utils.AttributesUtils;

public abstract class BaseMarkdownFileFragment extends BaseFileFragment {

  public static final String FILE_INFO = "FILE_INFO";

  private WebView webView;

  private MarkdownFileCallback markdownFileCallback;

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.markdonw_file_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    webView = (WebView) view.findViewById(R.id.webview);

    webView.getSettings().setBuiltInZoomControls(true);
    webView.getSettings().setSupportZoom(true);

    int webviewColor = AttributesUtils.getWebviewColor(getActivity());
    webView.setBackgroundColor(webviewColor);

    webView.clearCache(true);
    webView.clearFormData();
    webView.clearHistory();
    webView.clearMatches();
    webView.clearSslPreferences();
    webView.setVisibility(View.VISIBLE);

    new IntentsManager(getActivity()).manageUrls(webView);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    if (fileInfo != null && fileInfo.content != null) {
      menu.removeItem(R.id.action_show_code);
      getActivity().getMenuInflater().inflate(R.menu.file_markdown, menu);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {
      case R.id.action_show_code:
        if (markdownFileCallback != null) {
          markdownFileCallback.showAsContent(fileInfo);
        }
        break;
    }

    return false;
  }

  @Override
  protected void onContentLoaded(String s) {
    webView.loadDataWithBaseURL(null, s, "text/html", "UTF-8", null);
  }

  public void setMarkdownFileCallback(MarkdownFileCallback markdownFileCallback) {
    this.markdownFileCallback = markdownFileCallback;
  }

  public interface MarkdownFileCallback {
    void showAsContent(FileInfo fileInfo);
  }
}
