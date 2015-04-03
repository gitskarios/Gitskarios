package com.alorma.gistsapp.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alorma.gistsapp.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;

/**
 * Created by Bernat on 03/04/2015.
 */
public class GistEditorFragment extends DialogFragment {

    private GistEditorListener gistEditorListener;
    private EditText editTitle;
    private EditText editText;

    public static GistEditorFragment newInstance(Bundle extras) {
        GistEditorFragment editorFragment = new GistEditorFragment();
        editorFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        if (extras != null) {
            editorFragment.setArguments(extras);
        }
        return editorFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.editor_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endGistFile();
            }
        });

        editTitle = (EditText) view.findViewById(R.id.editTitle);
        editText = (EditText) view.findViewById(R.id.editText);

        String title = null;
        String text = null;

        if (getArguments() != null) {
            title = getArguments().getString(Intent.EXTRA_TITLE);
            text = getArguments().getString(Intent.EXTRA_TEXT);
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fileName = editTitle.getText().toString();
                String fileContent = editText.getText().toString();

                if (!TextUtils.isEmpty(fileName) || !TextUtils.isEmpty(fileContent)) {
                    if (gistEditorListener != null) {
                        gistEditorListener.onGistEditorUpdate(fileName, fileContent);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editTitle.addTextChangedListener(watcher);
        editText.addTextChangedListener(watcher);

        editTitle.setText(title);
        editText.setText(text);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        // request a window without the title
        if (dialog != null) {
            int dialogWidth = getResources().getDimensionPixelOffset(R.dimen.editor_popup_width);
            int dialogHeight = getResources().getDisplayMetrics().heightPixels;

            dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        endGistFile();
        super.onDismiss(dialog);
    }

    private void endGistFile() {
        if (gistEditorListener != null) {
            GistFile file = new GistFile();
            file.type = "text/plain";
            file.filename = editTitle.getText().toString();
            file.content = editText.getText().toString();
            gistEditorListener.onGistEditorFinish(file);
        }
    }

    public void setGistEditorListener(GistEditorListener gistEditorListener) {
        this.gistEditorListener = gistEditorListener;
    }

    public interface GistEditorListener {
        void onGistEditorUpdate(String title, String text);
        void onGistEditorFinish(GistFile file);
    }
}
