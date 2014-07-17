package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailFragment extends Fragment implements BaseClient.OnResultCallback<Repo> {
    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";

    public static RepoDetailFragment newInstance(String owner, String repo) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);

        RepoDetailFragment f = new RepoDetailFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.repo_detail_fragment, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String owner = getArguments().getString(OWNER);
            String repo = getArguments().getString(REPO);

            GetRepoClient repoClient = new GetRepoClient(getActivity(), owner, repo);
            repoClient.setOnResultCallback(this);
            repoClient.execute();
        }
    }

    @Override
    public void onResponseOk(Repo repo, Response r) {
        Log.i("ALORMA-REPO", "Repo: " + repo);
    }

    @Override
    public void onFail(RetrofitError error) {
        Log.e("ALORMA-REPO", "Repo error: ", error.getCause());
    }
}
