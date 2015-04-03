package com.alorma.gistsapp.ui.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alorma.gistsapp.R;
import com.alorma.gistsapp.ui.adapter.GistDetailFilesAdapter;
import com.alorma.gistsapp.ui.fragment.GistEditorFragment;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.github.mrengineer13.snackbar.SnackBar;

/**
 * Created by Bernat on 02/04/2015.
 */
public class CreateGistActivity extends ActionBarActivity implements GistEditorFragment.GistEditorListener {
    private RecyclerView recyclerView;
    private GistDetailFilesAdapter adapter;
    private GistEditorFragment editorFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boolean sharingMode = Intent.ACTION_SEND.equals(getIntent().getAction());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (sharingMode) {
            String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            if (!TextUtils.isEmpty(text)) {
                launchEditor(text);
            }
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.gist_files_count), StaggeredGridLayoutManager.VERTICAL));

        adapter = new GistDetailFilesAdapter(this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchEditor(null);
            }
        });
    }

    private void launchEditor(String text) {
        editorFragment = GistEditorFragment.newInstance();
        editorFragment.setGistEditorListener(this);

        boolean fullScreen = getResources().getBoolean(R.bool.editor_fullscreen);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (fullScreen) {
            ft.replace(android.R.id.content, editorFragment);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            editorFragment.show(ft, "editor");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    @Override
    public void onGistEditorFinish(GistFile file) {
        if (editorFragment != null) {
            editorFragment.setGistEditorListener(null);
            getFragmentManager().beginTransaction().remove(editorFragment).commit();

            if (TextUtils.isEmpty(file.filename) && TextUtils.isEmpty(file.content)) {
                new SnackBar.Builder(this)
                        .withMessageId(R.string.editor_discard)
                        .show();
            }
        }
    }
}
