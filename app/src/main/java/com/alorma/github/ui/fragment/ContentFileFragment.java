package com.alorma.github.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;

public class ContentFileFragment extends BaseFragment {

  public static final String FILE_INFO = "FILE_INFO";

  private HighlightJsView webView;
  private View loadingView;

  private FileInfo fileInfo;

  public static ContentFileFragment getInstance(FileInfo info) {
    ContentFileFragment fragment = new ContentFileFragment();
    Bundle args = new Bundle();
    args.putParcelable(FILE_INFO, info);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.file_fragment, null, false);
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

    loadingView = view.findViewById(R.id.loading_view);

    webView = (HighlightJsView) view.findViewById(R.id.webview);

    webView.setZoomSupportEnabled(true);

    webView.getSettings().setBuiltInZoomControls(true);
    webView.getSettings().setSupportZoom(true);

    if (isDarkTheme()) {
      webView.setTheme(Theme.ANDROID_STUDIO);
    } else {
      webView.setTheme(Theme.MONO_BLUE);
    }
    webView.setHighlightLanguage(Language.AUTO_DETECT);

    if (getArguments() != null) {

      fileInfo = getArguments().getParcelable(FILE_INFO);

      webView.clearCache(true);
      webView.clearFormData();
      webView.clearHistory();
      webView.clearMatches();
      webView.clearSslPreferences();
      webView.setVisibility(View.VISIBLE);

      if (fileInfo.name.endsWith(".xml")) {
        webView.setHighlightLanguage(Language.XML);
      }

      new IntentsManager(getActivity()).manageUrls(webView);

      setSourceIntoWebview(fileInfo);

      loadingView.setVisibility(View.GONE);
    }
  }

  private void setSourceIntoWebview(FileInfo fileInfo) {
    webView.setSource(fileInfo.content);
  }
}
