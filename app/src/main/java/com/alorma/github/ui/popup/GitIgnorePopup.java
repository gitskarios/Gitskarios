package com.alorma.github.ui.popup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GitIgnoreTemplates;
import com.alorma.github.sdk.services.gtignore.GitIgnoreClient;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 13/10/2014.
 */
public class GitIgnorePopup extends ListPopupWindow
        implements Observer<GitIgnoreTemplates>, AdapterView.OnItemClickListener, PopupWindow.OnDismissListener {

    private final Context context;
    private GitIgnoreTemplates ignoresList;
    private ArrayAdapter<String> ignoresAdapter;
    private OnGitIgnoresListener onGitIgnoresListener;

    public GitIgnorePopup(Context context) {
        super(context);
        this.context = context;
    }

    public GitIgnorePopup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public GitIgnorePopup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public GitIgnorePopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    @Override
    public void show() {
        if (ignoresList != null) {
            ignoresAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, ignoresList);
            setAdapter(ignoresAdapter);
            setOnItemClickListener(this);
            setOnDismissListener(this);
            super.show();
        } else {
            loadData();
        }
    }

    private void loadData() {
        GitIgnoreClient client = new GitIgnoreClient();
        client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onNext(GitIgnoreTemplates gitIgnoreTemplates) {
        GitIgnoreTemplates templates = new GitIgnoreTemplates();
        templates.add(context.getString(R.string.no_gitignore_template));
        templates.addAll(gitIgnoreTemplates);
        ignoresList = templates;
        show();
    }

    @Override
    public void onError(Throwable e) {
        if (onGitIgnoresListener != null) {
            dismiss();
            onGitIgnoresListener.onGitIgnoreFailed(e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position <= ignoresAdapter.getCount()) {
            if (onGitIgnoresListener != null) {
                dismiss();
                if (position == 0) {
                    onGitIgnoresListener.onGitIgnoreClear();
                } else {
                    onGitIgnoresListener.onGitIgnoreSelected(ignoresAdapter.getItem(position));
                }
            }
        }
    }

    public void setOnGitIgnoresListener(OnGitIgnoresListener onGitIgnoresListener) {
        this.onGitIgnoresListener = onGitIgnoresListener;
    }

    @Override
    public void onDismiss() {
        if (onGitIgnoresListener != null) {
            onGitIgnoresListener.onGitIgnoreDismissed();
        }
    }

    public interface OnGitIgnoresListener {
        void onGitIgnoreClear();

        void onGitIgnoreSelected(String s);

        void onGitIgnoreFailed(Throwable error);

        void onGitIgnoreDismissed();
    }
}
