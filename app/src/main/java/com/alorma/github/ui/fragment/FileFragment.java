package com.alorma.github.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.inapp.Base64;
import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.services.content.GetFileContentClient;
import com.alorma.github.sdk.services.content.GetMarkdownClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.utils.MarkdownUtils;
import com.alorma.github.UrlsManager;
import com.alorma.github.utils.ImageUtils;
import com.alorma.gitskarios.basesdk.client.BaseClient;

import java.io.UnsupportedEncodingException;

import dmax.dialog.SpotsDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FileFragment extends BaseFragment implements BaseClient.OnResultCallback<Content> {

    public static final String FILE_INFO = "FILE_INFO";

    private WebView webView;
    private ImageView imageView;
    private Content fileContent;

    private FileInfo fileInfo;

    private SpotsDialog progressDialog;

    public static FileFragment getInstance(FileInfo info) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putParcelable(FILE_INFO, info);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_content, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView = (WebView) view.findViewById(R.id.webview);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        if (getArguments() != null) {

            fileInfo = getArguments().getParcelable(FILE_INFO);

            webView.clearCache(true);
            webView.clearFormData();
            webView.clearHistory();
            webView.clearMatches();
            webView.clearSslPreferences();
            webView.setBackgroundColor(getResources().getColor(R.color.gray_github_light));
            webView.setVisibility(View.VISIBLE);
            WebSettings settings = webView.getSettings();
            settings.setBuiltInZoomControls(true);
            settings.setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new JavaScriptInterface(), "bitbeaker");

            new UrlsManager(getActivity()).manageUrls(webView);

            if (fileInfo.content == null) {
                getContent();
            } else {
                webView.loadUrl("file:///android_asset/diff.html");
            }
        }
    }

    protected void getContent() {
        if (fileInfo.repoInfo != null) {
            showProgressDialog(R.style.SpotDialog_OpeningFile);
            GetFileContentClient fileContentClient = new GetFileContentClient(getActivity(), fileInfo);
            fileContentClient.setOnResultCallback(this);
            fileContentClient.execute();
        }
    }

    @Override
    public void onResponseOk(Content content, Response r) {
        this.fileContent = content;

        hideProgressDialog();


        if (MarkdownUtils.isMarkdown(content.name)) {
            RequestMarkdownDTO request = new RequestMarkdownDTO();
            request.text = decodeContent();
            GetMarkdownClient markdownClient = new GetMarkdownClient(getActivity(), request, new Handler());
            markdownClient.setOnResultCallback(new BaseClient.OnResultCallback<String>() {
                @Override
                public void onResponseOk(final String s, Response r) {
                    if (getActivity() != null && isAdded()) {
                        webView.clearCache(true);
                        webView.clearFormData();
                        webView.clearHistory();
                        webView.clearMatches();
                        webView.clearSslPreferences();
                        webView.getSettings().setUseWideViewPort(false);
                        webView.setBackgroundColor(getResources().getColor(R.color.gray_github_light));
                        webView.loadDataWithBaseURL("http://github.com", s, "text/html; charset=UTF-8", null, null);
                    }
                }

                @Override
                public void onFail(RetrofitError error) {
                    ErrorHandler.onRetrofitError(getActivity(), "FileActivity", error);
                }
            });
            markdownClient.execute();
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
                Intent intent = new UrlsManager(getActivity()).manageRepos(content.git_url);
                if (intent != null) {
                    startActivity(new UrlsManager(getActivity()).manageRepos(content.git_url));
                    getActivity().finish();
                }
            }
        } else {
            if (getActivity() != null && isAdded()) {
                webView.loadUrl("file:///android_asset/source.html");
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

    }

    @Override
    public void onFail(RetrofitError error) {
        ErrorHandler.onRetrofitError(getActivity(), "FileActivity", error);
        try {
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showProgressDialog(@StyleRes int style) {
        if (progressDialog == null) {
            try {
                progressDialog = new SpotsDialog(getActivity(), style);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
