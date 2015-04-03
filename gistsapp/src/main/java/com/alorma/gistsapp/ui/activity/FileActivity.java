package com.alorma.gistsapp.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alorma.gistsapp.R;
import com.alorma.gistsapp.ui.fragment.FileFragment;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FileActivity extends ActionBarActivity {

    public static Intent createLauncherIntent(Context context, String name, String content) {
        Bundle bundle = new Bundle();
        bundle.putString(FileFragment.FILE_NAME, name);
        bundle.putString(FileFragment.CONTENT, content);

        Intent intent = new Intent(context, FileActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FileFragment fileFragment = new FileFragment();
        fileFragment.setArguments(getIntent().getExtras());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, fileFragment);
        ft.commit();


        String title = getIntent().getExtras().getString(FileFragment.FILE_NAME);
        setTitle(title);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
