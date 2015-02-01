package com.alorma.github.ui.fragment.commit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.commit.GetSingleCommitClient;
import com.alorma.github.ui.adapter.commit.CommitFilesAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/12/2014.
 */
public class SingleCommitFragment extends BaseFragment implements BaseClient.OnResultCallback<Commit> {

	public static final String SHA = "SHA";
	public static final String INFO = "INFO";
	private RecyclerView recyclerView;
	private TextView textMessage;
	private TextView textAdditions;
	private TextView textDeletions;
	private UpdateReceiver updateReceiver;
	private RepoInfo info;
	private String sha;

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
		return inflater.inflate(R.layout.single_commit_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (getArguments() != null) {
			info = getArguments().getParcelable(INFO);
			sha = getArguments().getString(SHA);

			textMessage = (TextView) view.findViewById(R.id.message);
			textAdditions = (TextView) view.findViewById(R.id.additions);
			textDeletions = (TextView) view.findViewById(R.id.deletions);

			recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

			getContent();
		}
	}

	private void getContent() {
		GetSingleCommitClient client = new GetSingleCommitClient(getActivity(), info, sha);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	public void onResponseOk(Commit commit, Response r) {
		getActivity().setTitle(commit.sha.substring(0, 8));

		textMessage.setText(commit.commit.message);

		String additions = getResources().getString(R.string.commit_additions, commit.stats.additions);
		String deletions = getResources().getString(R.string.commit_deletions, commit.stats.deletions);

		textAdditions.setText(additions);
		textDeletions.setText(deletions);

		CommitFilesAdapter adapter = new CommitFilesAdapter(getActivity(), commit.files);
		recyclerView.setAdapter(adapter);
	}

	@Override
	public void onFail(RetrofitError error) {

	}


	public void reload() {
		getContent();
	}

	@Override
	public void onStart() {
		super.onStart();
		updateReceiver = new UpdateReceiver();
		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		getActivity().registerReceiver(updateReceiver, intentFilter);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(updateReceiver);
	}

	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (isOnline(context)) {
				reload();
			}
		}

		public boolean isOnline(Context context) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
		}
	}
}
