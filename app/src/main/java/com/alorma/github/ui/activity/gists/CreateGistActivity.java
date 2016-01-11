package com.alorma.github.ui.activity.gists;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.sdk.bean.dto.response.GistFilesMap;
import com.alorma.github.sdk.services.gists.PublishGistClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.GistCreatedDetailFilesAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 02/04/2015.
 */
public class CreateGistActivity extends BackActivity implements GistCreatedDetailFilesAdapter.GistCreateAdapterListener {

    private static final int GIST_FILE_CREATOR = 540;
    private static final int GIST_FILE_EDITOR = 541;

    private GistCreatedDetailFilesAdapter adapter;
    private boolean sharingMode;
    private EditText gistDescription;
    private Switch gistPrivate;
    private RecyclerView recyclerView;
    private int editingPosition;

    public static Intent createLauncherIntent(Context context) {
        return new Intent(context, CreateGistActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gist);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GistCreatedDetailFilesAdapter(LayoutInflater.from(this));
        adapter.setGistCreateAdapterListener(this);
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
    }

    private void launchEmptyEditor() {
        Intent intent = GistEditorActivity.createLauncherIntent(this, getIntent().getExtras());
        startActivityForResult(intent, GIST_FILE_CREATOR);
    }

    private void launchEditor(GistFile file) {
        Intent intent = GistEditorActivity.createLauncherIntent(this, file);
        startActivityForResult(intent, GIST_FILE_EDITOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_gist, menu);
        MenuItem publishItem = menu.findItem(R.id.action_publish_gist);
        IconicsDrawable publishIcon = new IconicsDrawable(this, Octicons.Icon.oct_package);
        publishIcon.actionBar();
        publishIcon.color(Color.WHITE);
        publishItem.setIcon(publishIcon);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK) {
            GistFile file = (GistFile) data.getParcelableExtra(GistEditorActivity.EXTRA_FILE);
            if (file != null) {
                switch (requestCode) {
                    case GIST_FILE_CREATOR:
                        adapter.add(file);
                        break;
                    case GIST_FILE_EDITOR:
                        adapter.update(editingPosition, file);
                        break;
                }
                editingPosition = -1;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                String description = gistDescription.getText().toString();
                int gistFiles = adapter.getItemCount();

                if (gistFiles > 0) {
                    showDialogCancelGist();
                } else {
                    if (!TextUtils.isEmpty(description)) {
                        showDialogNotEmpty();
                    } else {
                        finish();
                    }
                }
                break;
            case R.id.action_publish_gist:
                publishGist();
                break;
        }

        return true;
    }

    private void showDialogNotEmpty() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.content(R.string.gist_creator_not_empty);
        builder.positiveText(R.string.gist_creator_editor_discard);
        builder.negativeText(R.string.cancel);
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                finish();
            }
        });
        builder.show();
    }

    private void showDialogCancelGist() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.content(R.string.gist_creator_cancel_job);
        builder.positiveText(R.string.ok);
        builder.negativeText(R.string.cancel);
        builder.neutralText(R.string.publish_gist);
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                finish();
            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
                super.onNeutral(dialog);
                publishGist();
            }
        });
        builder.show();
    }

    private void publishGist() {
        if (adapter != null && adapter.getItemCount() > 0) {
            Gist gist = new Gist();
            gist.isPublic = !gistPrivate.isChecked();
            gist.description = gistDescription.getText().toString();
            GistFilesMap files = new GistFilesMap();
            for (GistFile gistFile : adapter.getItems()) {
                if (!TextUtils.isEmpty(gistFile.filename) && !TextUtils.isEmpty(gistFile.content)) {
                    files.put(gistFile.filename, gistFile);
                }
            }
            gist.files = files;

            showProgressDialog(R.string.publishing_gist);

            PublishGistClient publishGistClient = new PublishGistClient(gist);
            publishGistClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Gist>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    hideProgressDialog();
                    Snackbar.make(recyclerView, R.string.publish_gist_fail, Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Gist gist) {
                    hideProgressDialog();
                    finish();
                }
            });
        }
    }

    @Override
    public void updateFile(int position, GistFile gistFile) {
        this.editingPosition = position;
        launchEditor(gistFile);
    }

    @Override
    public void removeFile(int position, final GistFile item) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.content(getString(R.string.gist_creator_remove_file, item.filename));
        builder.positiveText(R.string.ok);
        builder.negativeText(R.string.cancel);
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                adapter.remove(item);
            }
        });
        builder.show();
    }
}
