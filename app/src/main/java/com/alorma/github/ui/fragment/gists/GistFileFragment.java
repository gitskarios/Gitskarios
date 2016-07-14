package com.alorma.github.ui.fragment.gists;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GistFileFragment extends BaseFragment {

  public static final String FILE_NAME = "FILE_NAME";
  public static final String CONTENT = "CONTENT";

  private String content;
  private String fileName;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_content, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    HighlightJsView webView = (HighlightJsView) view.findViewById(R.id.webview);

    if (getArguments() != null) {
      fileName = getArguments().getString(FILE_NAME);
      content = getArguments().getString(CONTENT);

      webView.clearCache(true);
      webView.clearFormData();
      webView.clearHistory();
      webView.clearMatches();
      webView.clearSslPreferences();
      webView.getSettings().setUseWideViewPort(false);
      webView.setVisibility(View.VISIBLE);

      webView.setHighlightLanguage(Language.AUTO_DETECT);
      webView.setSource(content);

      webView.getSettings().setDefaultTextEncodingName("utf-8");
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

  private String configureHtml(String htmlContent) {
    String fileName = "source_pre.html";
    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    String pref_theme = defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
    if ("theme_dark".equalsIgnoreCase(pref_theme)) {
      fileName = "source_pre_dark.html";
    }

    String head = getAssetFileContent(fileName);
    String end = getAssetFileContent("source_post.html");

    return head + "\n" + htmlContent + "\n" + end;
  }

  public String getAssetFileContent(String filename) {
    StringBuilder buf = new StringBuilder();
    try {
      InputStream json = getActivity().getAssets().open(filename);
      BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
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

  protected class JavaScriptInterface {
    @JavascriptInterface
    public String getCode() {
      return TextUtils.htmlEncode(content.replace("\t", "    "));
    }

    @JavascriptInterface
    public String getRawCode() {
      return content;
    }

    @JavascriptInterface
    public String getFilename() {
      return fileName;
    }
  }
}
