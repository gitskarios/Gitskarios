package com.alorma.github.ui.activity.gists;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.services.gists.GetGistDetailClient;
import com.alorma.github.sdk.services.gists.UserGistsClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.GistsFragment;
import com.alorma.gitskarios.basesdk.client.BaseClient;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class GistsMainActivity extends BackActivity implements GistsFragment.GistsFragmentListener {

    private Toolbar toolbar;
    private GistsFragment gistsFragment;

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
            String gistId = null;
            String gistUser = null;

            gistId = uri.getLastPathSegment();
            if (uri.getPathSegments().size() > 1) {
                gistUser = uri.getPathSegments().get(0);
            }

            if (gistId != null && gistUser == null) {
                GetGistDetailClient gistDetailClient = new GetGistDetailClient(this, gistId);
                final String finalGistId = gistId;
                gistDetailClient.setOnResultCallback(new BaseClient.OnResultCallback<Gist>() {
                    @Override
                    public void onResponseOk(Gist gist, Response r) {
                        loadGistDetail(finalGistId);
                        finish();
                    }

                    @Override
                    public void onFail(RetrofitError error) {
                        UserGistsClient userGistsClient = new UserGistsClient(GistsMainActivity.this, finalGistId);
                        userGistsClient.setOnResultCallback(new BaseClient.OnResultCallback<ListGists>() {
                            @Override
                            public void onResponseOk(ListGists gists, Response r) {
                                setTitle(getString(R.string.user_gists, finalGistId));
                                showGistsFragment(finalGistId);
                            }

                            @Override
                            public void onFail(RetrofitError error) {
                                Toast.makeText(GistsMainActivity.this, R.string.gist_not_loaded, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                        userGistsClient.execute();
                    }
                });
                gistDetailClient.execute();
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

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, gistsFragment);
        ft.commit();
    }
}
