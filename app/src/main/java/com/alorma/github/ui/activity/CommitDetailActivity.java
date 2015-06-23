package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.services.commit.GetSingleCommitClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.commit.CommitFilesAdapter;
import com.alorma.github.ui.fragment.commit.CommitFilesFragment;
import com.alorma.github.ui.view.GitChangeStatusColorsView;
import com.alorma.github.ui.view.GitChangeStatusView;
import com.alorma.gitskarios.basesdk.client.BaseClient;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitDetailActivity extends BackActivity implements CommitFilesAdapter.OnFileRequestListener, BaseClient.OnResultCallback<Commit> {

    private CommitFilesFragment commitFilesFragment;
    private CommitInfo info;
    private GitChangeStatusColorsView numbersView;

    public static Intent launchIntent(Context context, CommitInfo commitInfo) {
        Bundle b = new Bundle();
        b.putParcelable(CommitFilesFragment.INFO, commitInfo);

        Intent intent = new Intent(context, CommitDetailActivity.class);
        intent.putExtras(b);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commit_activity);

        if (getIntent().getExtras() != null) {
            info = getIntent().getExtras().getParcelable(CommitFilesFragment.INFO);

            setTitle(String.valueOf(info.repoInfo));

            getContent();

            numbersView = (GitChangeStatusColorsView) findViewById(R.id.commitNumbers);

            commitFilesFragment = CommitFilesFragment.newInstance(info);
            commitFilesFragment.setOnFileRequestListener(this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, commitFilesFragment);
            ft.commit();
        }
    }

    @Override
    protected void getContent() {
        super.getContent();
        GetSingleCommitClient client = new GetSingleCommitClient(this, info);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    public void onResponseOk(Commit commit, Response r) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(commit.shortSha());
        }

        if (numbersView != null) {
            numbersView.setNumbers(commit.stats);
        }

        if (commitFilesFragment != null) {
            commitFilesFragment.setFiles(commit.files);
        }
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    @Override
    public void onFileRequest(CommitFile file) {
        FileInfo info = new FileInfo();
        info.content = file.patch;
        info.name = file.getFileName();
        Intent launcherIntent = FileActivity.createLauncherIntent(this, info, false);
        startActivity(launcherIntent);
    }
}
