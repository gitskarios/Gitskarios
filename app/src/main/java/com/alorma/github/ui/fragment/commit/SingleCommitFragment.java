package com.alorma.github.ui.fragment.commit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
			RepoInfo info = getArguments().getParcelable(INFO);
			String sha = getArguments().getString(SHA);

			recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
						
			GetSingleCommitClient client = new GetSingleCommitClient(getActivity(), info, sha);
			client.setOnResultCallback(this);
			client.execute();
		}
	}

	@Override
	public void onResponseOk(Commit commit, Response r) {
		CommitFilesAdapter adapter = new CommitFilesAdapter(getActivity(), commit.files);
		recyclerView.setAdapter(adapter);
	}

	@Override
	public void onFail(RetrofitError error) {
		
	}
}
