package com.alorma.github;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 31/07/2014.
 */
public class Interceptor extends Activity implements BaseClient.OnResultCallback<User> {

	private Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
			uri = getIntent().getData();

			if (uri != null) {
				List<String> pathSegments = uri.getPathSegments();
				switch (pathSegments.size()) {
					case 2:
						if (!checkIsReserved(pathSegments.get(0))) {
							String owner = pathSegments.get(0);
							String repo = pathSegments.get(1);

							Intent repoIntent = RepoDetailActivity.createIntentFilterLauncherIntent(this, owner, repo);
							startActivity(repoIntent);
							finish();
						} else {
							onFail();
						}
						break;
					case 1:
						if (!checkIsReserved(pathSegments.get(0))) {
							String user = pathSegments.get(0);
							executeUserSearch(user);
						} else {
							onFail();
						}
						break;
					default:
						onFail();
						break;
				}
			}
		}
	}

	private boolean checkIsReserved(String s) {
		String[] reservedGitHubString = getResources().getStringArray(R.array.reservedKeys);
		return Arrays.asList(reservedGitHubString).contains(s);
	}

	private void executeUserSearch(String user) {
		RequestUserClient requestUserClient = new RequestUserClient(this, user);
		requestUserClient.setOnResultCallback(this);
		requestUserClient.execute();
	}

	@Override
	public void onResponseOk(User user, Response r) {
		Intent profile = ProfileActivity.createIntentFilterLauncherActivity(this, user);
		startActivity(profile);
	}

	@Override
	public void onFail(RetrofitError error) {
		onFail();
	}

	private void onFail() {
		Intent intent = new Intent(getIntent().getAction());
		intent.setData(getIntent().getData());
		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);

		List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

		if (!resolveInfos.isEmpty()) {
			List<Intent> targetedShareIntents = new ArrayList<Intent>();

			for (ResolveInfo resolveInfo : resolveInfos) {
				String packageName = resolveInfo.activityInfo.packageName;
				if (!packageName.equals(getPackageName())) {
					Intent targetedShareIntent = new Intent(getIntent().getAction());
					targetedShareIntent.setData(getIntent().getData());
					targetedShareIntent.setPackage(packageName);
					targetedShareIntents.add(targetedShareIntent);
				}
			}

			Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Open with...");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));

			startActivity(chooserIntent);
		}
	}
}
