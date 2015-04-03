package com.alorma.gistsapp.ui.activity;

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
public class CreateGistActivity extends ActionBarActivity implements GistEditorFragment.GistEditorListener, GistDetailFilesAdapter.GistFilesAdapterListener {
    private RecyclerView recyclerView;
    private GistDetailFilesAdapter adapter;
    private GistEditorFragment editorFragment;
    private boolean sharingMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharingMode = Intent.ACTION_SEND.equals(getIntent().getAction());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.gist_files_count), StaggeredGridLayoutManager.VERTICAL));

        adapter = new GistDetailFilesAdapter(this);
        adapter.setInEditMode(true);
        adapter.setGistFilesAdapterListener(this);
        recyclerView.setAdapter(adapter);

        launchEmptyEditor();

        findViewById(R.id.fabButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchEmptyEditor();
            }
        });
    }

    private void launchEmptyEditor() {
        createEmptyFileAndAdd();

        editorFragment = null;
        editorFragment = GistEditorFragment.newInstance(getIntent().getExtras());
        editorFragment.setGistEditorListener(this);

        launchEditor(editorFragment);
    }

    private void launchEditor(int position, GistFile file) {
        editorFragment = null;
        editorFragment = GistEditorFragment.newInstance(position, file);
        editorFragment.setGistEditorListener(this);

        launchEditor(editorFragment);
    }

    private void launchEditor(GistEditorFragment editorFragment) {
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

    private void createEmptyFileAndAdd() {
        adapter.newEmptyItem();
    }

    @Override
    public void onGistEditorUpdate(String title, String text) {
        adapter.updateCurrentItem(title, text);
    }

    @Override
    public void onGistEditorUpdate(int position, GistFile file) {
        adapter.updateItem(position, file);
    }

    @Override
    public void onGistEditorFinish(GistFile file) {
        if (editorFragment != null) {
            editorFragment.setGistEditorListener(null);
            getFragmentManager().beginTransaction().remove(editorFragment).commit();

            if (TextUtils.isEmpty(file.filename) && TextUtils.isEmpty(file.content)) {
                if (sharingMode) {
                    Toast.makeText(this, R.string.editor_discard, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    adapter.clearCurrent();
                    new SnackBar.Builder(this)
                            .withMessageId(R.string.editor_discard)
                            .show();
                }
            } else {
                if (TextUtils.isEmpty(file.filename)) {
                    file.filename = "File" + adapter.getItemCount() + ".txt";
                }
                adapter.addFile(file);
            }
        }
    }

    @Override
    public void onGistFilesSelected(int position, GistFile file) {
        launchEditor(position, file);
    }
}
