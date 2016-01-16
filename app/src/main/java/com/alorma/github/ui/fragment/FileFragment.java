package com.alorma.github.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.Toast;

import com.alorma.github.Base64;
import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.services.content.GetFileContentClient;
import com.alorma.github.sdk.services.content.GetMarkdownClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.utils.MarkdownUtils;
import com.alorma.github.ui.view.CopyWebView;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.ImageUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FileFragment extends BaseFragment {

    public static final String FILE_INFO = "FILE_INFO";
    public static final String FROM_URL = "FROM_URL";

    private CopyWebView webView;
    private ImageView imageView;
    private Content fileContent;
    private View loadingView;

    private FileInfo fileInfo;
    private boolean fromUrl;

    public static FileFragment getInstance(FileInfo info, boolean fromUrl) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putParcelable(FILE_INFO, info);
        args.putBoolean(FROM_URL, fromUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.file_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingView = view.findViewById(R.id.loading_view);

        webView = (CopyWebView) view.findViewById(R.id.webview);
        webView.setWebViewListener(new CopyWebView.WebViewListener() {
            @Override
            public void onTextCopy(String text) {
                // put selected text into clipdata
                ClipboardManager clipboard = (ClipboardManager) webView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", text);
                clipboard.setPrimaryClip(clip);
            }
        });

        webView.setBackgroundColor(AttributesUtils.getWebviewColor(getActivity()));

        imageView = (ImageView) view.findViewById(R.id.imageView);

        if (getArguments() != null) {

            fileInfo = (FileInfo) getArguments().getParcelable(FILE_INFO);
            fromUrl = getArguments().getBoolean(FROM_URL);

            webView.clearCache(true);
            webView.clearFormData();
            webView.clearHistory();
            webView.clearMatches();
            webView.clearSslPreferences();
            webView.setVisibility(View.VISIBLE);
            WebSettings settings = webView.getSettings();
            settings.setBuiltInZoomControls(true);
            settings.setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new JavaScriptInterface(), "bitbeaker");

            new UrlsManager(getActivity()).manageUrls(webView);

            if (fileInfo.content == null) {
                if (fromUrl) {
                    getBranches();
                } else {
                    getContent();
                }
            } else {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String pref_theme = defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
                if ("theme_dark".equalsIgnoreCase(pref_theme)) {
                    webView.loadUrl("file:///android_asset/diff_dark.html");
                } else {
                    webView.loadUrl("file:///android_asset/diff.html");
                }
                loadingView.setVisibility(View.GONE);
            }
        }
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
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
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

    private void getBranches() {
        if (fileInfo.repoInfo != null) {
            showProgressDialog();

            GetRepoBranchesClient branchesClient = new GetRepoBranchesClient(fileInfo.repoInfo);
            branchesClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ParseBranchesCallback(fileInfo.path));
        }
    }

    protected void getContent() {
        if (fileInfo.repoInfo != null) {
            showProgressDialog();
            GetFileContentClient fileContentClient = new GetFileContentClient(fileInfo);
            fileContentClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Content>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Content content) {
                    onContentLoaded(content);
                }
            });
        }
    }

    public void onContentLoaded(Content content) {
        this.fileContent = content;

        hideProgressDialog();

        if (MarkdownUtils.isMarkdown(content.name)) {
            RequestMarkdownDTO request = new RequestMarkdownDTO();
            request.text = decodeContent();
            GetMarkdownClient markdownClient = new GetMarkdownClient(request);
            markdownClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(String s) {
                    if (getActivity() != null && isAdded()) {
                        webView.clearCache(true);
                        webView.clearFormData();
                        webView.clearHistory();
                        webView.clearMatches();
                        webView.clearSslPreferences();
                        webView.getSettings().setUseWideViewPort(false);
                        webView.loadDataWithBaseURL("http://github.com", configureHtml(s), "text/html; charset=UTF-8", null, null);
                    }
                }
            });
        } else if (ImageUtils.isImage(content.name)) {
            if (getActivity() != null && isAdded()) {
                try {
                    byte[] imageAsBytes = Base64.decode(content.content);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    webView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                    // TODO STOP loading
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.error_loading_image, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else if (content.isSubmodule()) {
            if (getActivity() != null && isAdded()) {
                Intent intent = new UrlsManager(getActivity()).manageRepos(Uri.parse(content.git_url));
                if (intent != null) {
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        } else {
            if (getActivity() != null && isAdded()) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String pref_theme = defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));

                if ("theme_dark".equalsIgnoreCase(pref_theme)) {
                    webView.loadUrl("file:///android_asset/source_dark.html");
                } else {
                    webView.loadUrl("file:///android_asset/source.html");
                }
            }
        }
    }

    private String decodeContent() {
        if (fileInfo.content == null) {
            byte[] data = android.util.Base64.decode(fileContent.content, android.util.Base64.DEFAULT);
            try {
                fileContent.content = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return fileContent.content;
        } else {
            return fileInfo.content;
        }
    }

    protected void showProgressDialog() {
        loadingView.setVisibility(View.VISIBLE);
    }

    protected void hideProgressDialog() {
        loadingView.setVisibility(View.GONE);
    }

    protected class JavaScriptInterface {
        @JavascriptInterface
        public String getCode() {
            return TextUtils.htmlEncode(decodeContent().replace("\t", "    "));
        }

        @JavascriptInterface
        public String getRawCode() {
            return decodeContent();
        }

        @JavascriptInterface
        public String getFilename() {
            return fileContent.name;
        }

        @JavascriptInterface
        public int getLineHighlight() {
            return 0;
        }
    }

    private class ParseBranchesCallback extends Subscriber<List<Branch>> {
        private String path;

        public ParseBranchesCallback(String path) {
            this.path = path;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Branch> branches) {
            for (Branch branch : branches) {
                if (path != null && path.contains(branch.name)) {
                    fileInfo.repoInfo.branch = branch.name;

                    fileInfo.path = fileInfo.path.replace(branch.name + "/", "");
                    getContent();
                    break;
                }
            }
        }
    }
}
