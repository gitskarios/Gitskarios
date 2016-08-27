package com.alorma.github.ui.fragment.content.source;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;
import java.io.UnsupportedEncodingException;

public abstract class TextBaseFileFragment extends BaseFileFragment {

  public static final String FILE_INFO = "FILE_INFO";

  private HighlightJsView webView;
  private FileInfo fileInfo;

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
    }
  }

  @Override
  protected void onContentLoaded(String content) {
    webView.setSource(content);
  }

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
