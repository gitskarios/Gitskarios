package com.alorma.github.ui.activity.gists;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.services.gists.GetGistDetailClient;
import com.alorma.github.sdk.services.gists.UserGistsClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.GistsFragment;
import com.alorma.gitskarios.core.Pair;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GistsMainActivity extends BackActivity implements GistsFragment.GistsFragmentListener {

    private Toolbar toolbar;
    private GistsFragment gistsFragment;
    private String gistId;

    public static Intent createLauncherIntent(Context context) {
        return createLauncherIntent(context, null);
    }

    public static Intent createLauncherIntent(Context context, String username) {
        Intent intent = new Intent(context, GistsMainActivity.class);

        if (username != null) {
            Uri uri = Uri.parse("http://gist.github.com/" + username);
            intent.setData(uri);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_gists);

        Uri uri = null;

        if ((Intent.ACTION_SEND.equals(getIntent().getAction())) || (Intent.ACTION_VIEW.equals(getIntent().getAction()))) {
            uri = getIntent().getData();
            if (uri == null && getIntent().getStringExtra(Intent.EXTRA_TEXT) != null) {
                try {
                    uri = Uri.parse(getIntent().getStringExtra(Intent.EXTRA_TEXT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (getIntent().getData() != null) {
            try {
                uri = getIntent().getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (uri != null) {
            String gistUser = null;

            gistId = uri.getLastPathSegment();
            if (uri.getPathSegments().size() > 1) {
                gistUser = uri.getPathSegments().get(0);
            }

            if (gistId != null && gistUser == null) {
                GetGistDetailClient gistDetailClient = new GetGistDetailClient(gistId);
                gistDetailClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Gist>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        loadGists();
                    }

                    @Override
                    public void onNext(Gist gist) {
                        loadGistDetail(gistId);
                        finish();
                    }
                });
            } else if (gistId != null) {
                loadGistDetail(gistId);
                finish();
            } else {
                showGistsFragment(null);
            }
        } else {
            showGistsFragment(null);
        }
    }

    private void loadGists() {
        UserGistsClient userGistsClient = new UserGistsClient(gistId);
        userGistsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<Pair<List<Gist>, Integer>, List<Gist>>() {
            @Override
            public List<Gist> call(Pair<List<Gist>, Integer> listIntegerPair) {
                return listIntegerPair.first;
            }
        }).subscribe(new Subscriber<List<Gist>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(GistsMainActivity.this, R.string.gist_not_loaded, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onNext(List<Gist> gists) {
                setTitle(getString(R.string.user_gists, gistId));
                showGistsFragment(gistId);
            }
        });
    }

    @Override
    public void onGistsRequest(Gist gist) {
        loadGistDetail(gist.id);
    }

    private void loadGistDetail(String gistId) {
        Intent launcherIntent = GistDetailActivity.createLauncherIntent(this, gistId);
        startActivity(launcherIntent);
    }

    private void showGistsFragment(String username) {

        gistsFragment = GistsFragment.newInstance(username);
        gistsFragment.setGistsFragmentListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, gistsFragment);
        ft.commit();
    }
}
