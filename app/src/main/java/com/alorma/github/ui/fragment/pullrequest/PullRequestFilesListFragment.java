package com.alorma.github.ui.fragment.pullrequest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.pullrequest.GetPullRequestFiles;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.adapter.commit.CommitFilesAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.gitskarios.core.Pair;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 07/09/2014.
 */
public class PullRequestFilesListFragment extends BaseFragment implements CommitFilesAdapter.OnFileRequestListener {

    public static final String INFO = "INFO";
    private RecyclerView recyclerView;
    private IssueInfo info;
    private CommitFilesAdapter adapter;

    public static PullRequestFilesListFragment newInstance(IssueInfo info) {
        PullRequestFilesListFragment f = new PullRequestFilesListFragment();
        Bundle b = new Bundle();
        b.putParcelable(INFO, info);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.files_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {

            adapter = new CommitFilesAdapter(LayoutInflater.from(getActivity()));

            info = (IssueInfo) getArguments().getParcelable(INFO);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            getContent();
        }
    }

    private void getContent() {
        GetPullRequestFiles getPullRequestFiles = new GetPullRequestFiles(info);
        getPullRequestFiles.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Pair<List<CommitFile>, Integer>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Pair<List<CommitFile>, Integer> listIntegerPair) {
                if (getActivity() != null) {
                    adapter.addAll(listIntegerPair.first);
                    adapter.setOnFileRequestListener(PullRequestFilesListFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onFileRequest(CommitFile file) {
        FileInfo info = new FileInfo();
        info.content = file.patch;
        info.name = file.getFileName();

        Intent launcherIntent = FileActivity.createLauncherIntent(getActivity(), info, false);
        startActivity(launcherIntent);
    }
}