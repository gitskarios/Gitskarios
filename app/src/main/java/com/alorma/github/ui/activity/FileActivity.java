package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.inapp.Base64;
import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.content.GetFileContentClient;
import com.alorma.github.sdk.services.content.GetMarkdownClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.FileFragment;
import com.alorma.github.ui.utils.MarkdownUtils;
import com.alorma.github.utils.ImageUtils;

import java.io.UnsupportedEncodingException;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FileActivity extends ActionBarActivity {

	private static final String REPO_INFO = "REPO_INFO";
	private static final String NAME = "NAME";
	private static final String PATH = "PATH";
	private static final String PATCH = "PATCH";

	public static Intent createLauncherIntent(Context context, RepoInfo repoInfo, String name, String path) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(REPO_INFO, repoInfo);
		bundle.putString(NAME, name);
		bundle.putString(PATH, path);

		Intent intent = new Intent(context, FileActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	public static Intent createLauncherIntent(Context context, String patch, String name) {
		Bundle bundle = new Bundle();
		bundle.putString(PATCH, patch);
		bundle.putString(NAME, name);

		Intent intent = new Intent(context, FileActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FileFragment fileFragment = new FileFragment();
		fileFragment.setArguments(getIntent().getExtras());
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, fileFragment);
		ft.commit();
	}

}
