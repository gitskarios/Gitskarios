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

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistDetailActivity extends ActionBarActivity {

    public static Intent createLauncherIntent(Context context, String id) {
        Intent intent = new Intent(context, GistDetailActivity.class);

        intent.putExtra(GistDetailFragment.GIST_ID, id);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, GistDetailFragment.newInstance(getIntent().getStringExtra(GistDetailFragment.GIST_ID)));
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
