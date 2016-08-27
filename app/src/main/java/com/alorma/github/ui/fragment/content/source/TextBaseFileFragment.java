package com.alorma.github.ui.fragment.content.source;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.fragment.content.BaseFileFragment;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;

public abstract class TextBaseFileFragment extends BaseFileFragment {

  private HighlightJsView webView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.file_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

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

      FileInfo fileInfo = getArguments().getParcelable(FILE_INFO);
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
    }
  }

  @Override
  protected void onContentLoaded(String content) {
    webView.setSource(content);
  }
}
