package com.alorma.github.ui.fragment.commit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.commit.GetSingleCommitClient;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.fragment.base.BaseFragment;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/12/2014.
 */
public class SingleCommitFragment extends BaseFragment implements BaseClient.OnResultCallback<Commit> {

	public static final String SHA = "SHA";
	public static final String INFO = "INFO";

	public static SingleCommitFragment newInstance(RepoInfo info, String sha) {
		SingleCommitFragment f = new SingleCommitFragment();
		Bundle b = new Bundle();
		b.putParcelable(INFO, info);
		b.putString(SHA, sha);
		f.setArguments(b);
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return new TextView(getActivity());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (getArguments() != null) {
			RepoInfo info = getArguments().getParcelable(INFO);
			String sha = getArguments().getString(SHA);

			GetSingleCommitClient client = new GetSingleCommitClient(getActivity(), info, sha);
			client.setOnResultCallback(this);
			client.execute();
		}
	}

	@Override
	public void onResponseOk(Commit commit, Response r) {
		Log.i("ALORMA", "" + commit);
	}

	@Override
	public void onFail(RetrofitError error) {
		Log.e("ALORMA", "" + error);
	}
}
