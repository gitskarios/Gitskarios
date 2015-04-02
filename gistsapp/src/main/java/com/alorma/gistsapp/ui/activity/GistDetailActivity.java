package com.alorma.gistsapp.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alorma.gistsapp.R;
import com.alorma.gistsapp.ui.fragment.GistDetailFragment;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.GistFile;

import java.util.TreeMap;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistDetailActivity extends ActionBarActivity implements GistDetailFragment.GistDetailListener {

    private Toolbar toolbar;

    public static Intent createLauncherIntent(Context context, String id) {
        Intent intent = new Intent(context, GistDetailActivity.class);

        intent.putExtra(GistDetailFragment.GIST_ID, id);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        GistDetailFragment detailFragment = GistDetailFragment.newInstance(getIntent().getStringExtra(GistDetailFragment.GIST_ID));
        detailFragment.setGistDetailListener(this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, detailFragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    public void onGistLoaded(Gist gist) {
        TreeMap<String, GistFile> filesMap = new TreeMap<>(gist.files);
        GistFile firstFile = filesMap.firstEntry().getValue();
        toolbar.setTitle(firstFile.filename);
        toolbar.setSubtitle(getString(R.string.num_of_files, gist.files.size()));
    }
}
