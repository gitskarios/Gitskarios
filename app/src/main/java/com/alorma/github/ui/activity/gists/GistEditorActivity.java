package com.alorma.github.ui.activity.gists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.ui.activity.LanguagesActivity;
import com.alorma.github.ui.activity.base.BackActivity;

/**
 * Created by Bernat on 03/04/2015.
 */
public class GistEditorActivity extends BackActivity {

    public static final String EXTRA_FILE = "EXTRA_FILE";
    private static final String EXTRA_LANGUAGE = "EXTRA_LANGUAGE";
    private static final int LANGUAGE_REQUEST = 854;

    private EditText editTitle;
    private EditText editText;
    private String currentLanguage;
    private Button buttonLanguages;

    public static Intent createLauncherIntent(Context context, Bundle extras) {
        Intent intent = new Intent(context, GistEditorActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        return intent;
    }

    public static Intent createLauncherIntent(Context context, GistFile file) {
        Bundle bundle = new Bundle();
        bundle.putString(Intent.EXTRA_TITLE, file.filename);
        bundle.putString(Intent.EXTRA_TEXT, file.content);
        if (file.language != null) {
            bundle.putString(EXTRA_LANGUAGE, file.language);
        }
        return createLauncherIntent(context, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.gapSmall));

        editTitle = (EditText) findViewById(R.id.editTitle);
        editText = (EditText) findViewById(R.id.editText);
        buttonLanguages = (Button) findViewById(R.id.buttonLanguages);

        String title = null;
        String text = null;

        if (getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString(Intent.EXTRA_TITLE);
            text = getIntent().getExtras().getString(Intent.EXTRA_TEXT);
            currentLanguage = getIntent().getExtras().getString(EXTRA_LANGUAGE);
        }

        editTitle.setText(title);
        editText.setText(text);
        if (currentLanguage != null) {
            buttonLanguages.setText(currentLanguage);
        }

        buttonLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LanguagesActivity.class);
                startActivityForResult(intent, LANGUAGE_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.gist_editor_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String filename = editTitle.getText().toString();
                String content = editText.getText().toString();

                if (!TextUtils.isEmpty(filename) || !TextUtils.isEmpty(content)) {
                    showDialogCancelFile();
                } else {
                    finish();
                }
                break;
            case R.id.gist_editor_add_file_action:
                endGistFile();
                break;
        }
        return true;
    }

    private void showDialogCancelFile() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.content(R.string.gist_file_editor_cancel_job);
        builder.positiveText(R.string.ok);
        builder.negativeText(R.string.cancel);
        builder.neutralText(R.string.gist_editor_add_file_action);
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                finish();
            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
                super.onNeutral(dialog);
                endGistFile();
            }
        });
        builder.show();
    }

    private void endGistFile() {
        GistFile file = new GistFile();
        file.type = "text/plain";
        file.filename = editTitle.getText().toString();
        file.content = editText.getText().toString();
        file.language = currentLanguage;

        Intent data = new Intent();
        data.putExtra(EXTRA_FILE, file);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == LANGUAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            currentLanguage = data.getStringExtra(LanguagesActivity.EXTRA_LANGUAGE);

            buttonLanguages.setText(currentLanguage);
        }
    }
}
