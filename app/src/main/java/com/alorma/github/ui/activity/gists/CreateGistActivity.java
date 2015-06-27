package com.alorma.github.ui.activity.gists;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.sdk.services.gists.PublishGistClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.FakeAdapter;
import com.alorma.github.ui.adapter.GistDetailFilesAdapter;
import com.alorma.github.ui.fragment.GistEditorFragment;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.github.mrengineer13.snackbar.SnackBar;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 02/04/2015.
 */
public class CreateGistActivity extends BackActivity implements GistEditorFragment.GistEditorListener, GistDetailFilesAdapter.GistFilesAdapterListener {
    private GistDetailFilesAdapter adapter;
    private GistEditorFragment editorFragment;
    private boolean sharingMode;
    private AlertDialog spotsDialog;
    private EditText gistDescription;
    private Switch gistPrivate;

    public static Intent createLauncherIntent(Context context) {
        return new Intent(context, CreateGistActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.gist_files_count), StaggeredGridLayoutManager.VERTICAL));

        adapter = new GistDetailFilesAdapter(this);
        adapter.setInEditMode(true);
        adapter.setGistFilesAdapterListener(this);
        recyclerView.setAdapter(adapter);

        sharingMode = Intent.ACTION_SEND.equals(getIntent().getAction());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        gistDescription = (EditText) findViewById(R.id.gistDescription);
        gistPrivate = (Switch) findViewById(R.id.gistPrivate);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabButton);

        fab.setImageDrawable(new IconicsDrawable(this, Octicons.Icon.oct_gist_new).color(Color.WHITE).actionBar());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEmptyEditor();
            }
        });

        findViewById(R.id.createGist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishGist();
            }
        });
    }

    private void launchEmptyEditor() {

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
        createEmptyFileAndAdd();

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


    private void publishGist() {
        Gist gist = new Gist();
        gist.isPublic = !gistPrivate.isChecked();
        gist.description = gistDescription.getText().toString();
        Map<String, GistFile> files = new HashMap<>();
        for (GistFile gistFile : adapter.getGistFileList()) {
            files.put(gistFile.filename, gistFile);
        }
        gist.files = files;

        spotsDialog = new SpotsDialog.Builder(this)
                .setMessage(R.string.publishing_gist)
                .setCancelable(false)
                .show();

        PublishGistClient publishGistClient = new PublishGistClient(this, gist);
        publishGistClient.setOnResultCallback(new BaseClient.OnResultCallback<Gist>() {
            @Override
            public void onResponseOk(Gist gist, Response r) {
                if (spotsDialog != null) {
                    spotsDialog.dismiss();
                }
                finish();
            }

            @Override
            public void onFail(RetrofitError error) {
                if (spotsDialog != null) {
                    spotsDialog.dismiss();
                }
                new SnackBar.Builder(CreateGistActivity.this)
                        .withMessageId(R.string.publish_gist_fail)
                        .show();
            }
        });
        publishGistClient.execute();
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
        if (adapter.getItemCount() == 1 && (TextUtils.isEmpty(file.filename) && TextUtils.isEmpty(file.content))) {
            finish();
        } else {
            if (editorFragment != null) {
                editorFragment.setGistEditorListener(null);
                getFragmentManager().beginTransaction().remove(editorFragment).commit();
            }
            adapter.updateItem(position, file);
        }
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
                invalidateOptionsMenu();
            }
        }
    }

    @Override
    public void onGistFilesSelected(int position, GistFile file) {
        launchEditor(position, file);
    }
}
