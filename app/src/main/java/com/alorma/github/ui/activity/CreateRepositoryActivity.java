package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import com.alorma.github.BuildConfig;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.RepoRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repo.CreateRepositoryClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.CreateRepositoryFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bernat.borras on 10/11/15.
 */
public class CreateRepositoryActivity extends BackActivity
        implements CreateRepositoryFragment.CreateRepositoryInterface, View.OnClickListener {

    @Bind(R.id.create)
    Button create;
    private RepoRequestDTO dto;
    private Repo repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_repository_activity);
        ButterKnife.bind(this);

        setTitle(R.string.create_repository_activity);

        CreateRepositoryFragment fragment = new CreateRepositoryFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }

    @Override
    public void onRepositoryReady(RepoRequestDTO dto) {
        this.dto = dto;
        create.setEnabled(true);
        create.setOnClickListener(this);
    }

    @Override
    public void onRepositoryNotReady() {
        create.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        CreateRepositoryClient client = new CreateRepositoryClient(dto);
        client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Repo>() {
            @Override
            public void onCompleted() {
                openRepo(repo);
            }

            @Override
            public void onError(Throwable e) {
                if (BuildConfig.DEBUG) {
                    Snackbar.make(create, "Error creating repository: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    e.printStackTrace();
                } else {
                    Snackbar.make(create, "Error creating repository", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNext(Repo repo) {
                CreateRepositoryActivity.this.repo = repo;
            }
        });
    }

    private void openRepo(Repo repo) {
        Intent intent = RepoDetailActivity.createLauncherIntent(this, repo.toInfo());
        startActivity(intent);
        finish();
    }
}
