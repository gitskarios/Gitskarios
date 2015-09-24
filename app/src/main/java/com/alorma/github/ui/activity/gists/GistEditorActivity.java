package com.alorma.github.ui.activity.gists;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.ui.activity.LanguagesActivity;
import com.alorma.github.ui.activity.base.BackActivity;

/**
 * Created by Bernat on 03/04/2015.
 */
public class GistEditorActivity extends BackActivity {

    private static final String EXTRA_LANGUAGE = "EXTRA_LANGUAGE";
    public static final String EXTRA_FILE = "EXTRA_FILE";

    private static final int LANGUAGE_REQUEST = 854;

    private EditText editTitle;
    private EditText editText;
    private String currentLanguage;

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
        View buttonLanguages = findViewById(R.id.buttonLanguages);

        String title = null;
        String text = null;

        if (getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString(Intent.EXTRA_TITLE);
            text = getIntent().getExtras().getString(Intent.EXTRA_TEXT);
        }

        editTitle.setText(title);
        editText.setText(text);

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
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.gist_editor_add_file_action:
                endGistFile();
                break;
        }
        return true;
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
        }
    }
}
