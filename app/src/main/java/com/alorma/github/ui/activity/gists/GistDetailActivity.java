package com.alorma.github.ui.activity.gists;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.GistDetailFragment;

import java.util.TreeMap;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistDetailActivity extends BackActivity implements GistDetailFragment.GistDetailListener {

    private Toolbar toolbar;
    private GistDetailFragment detailFragment;
    private Gist gist;

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

        detailFragment = GistDetailFragment.newInstance(getIntent().getStringExtra(GistDetailFragment.GIST_ID));
        detailFragment.setGistDetailListener(this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, detailFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (detailFragment != null && gist != null) {
            getMenuInflater().inflate(detailFragment.getMenuId(), menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = super.onOptionsItemSelected(item);
        if (!result) {
            detailFragment.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onGistLoaded(Gist gist) {
        this.gist = gist;
        TreeMap<String, GistFile> filesMap = new TreeMap<>(gist.files);
        GistFile firstFile = filesMap.firstEntry().getValue();
        toolbar.setTitle(firstFile.filename);
        toolbar.setSubtitle(getString(R.string.num_of_files, gist.files.size()));
        invalidateOptionsMenu();
    }
}
