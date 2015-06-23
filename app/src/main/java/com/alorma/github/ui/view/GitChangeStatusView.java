package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GitChangeStatus;

/**
 * Created by Bernat on 23/06/2015.
 */
public class GitChangeStatusView extends LinearLayout {
    private GitChangeStatus numbers;
    private TextView textAdditions;
    private TextView textDeletions;
    private GitChangeStatusColorsView colorsView;

    public GitChangeStatusView(Context context) {
        super(context);
        init();
    }

    public GitChangeStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GitChangeStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GitChangeStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        inflate(getContext(), R.layout.github_status, this);
        textAdditions = (TextView) findViewById(R.id.textAdditions);
        textDeletions = (TextView) findViewById(R.id.textDeletions);
        colorsView = (GitChangeStatusColorsView) findViewById(R.id.colors);
        if (isInEditMode()) {
            GitChangeStatus status = new GitChangeStatus();
            status.additions = 34;
            status.deletions = 13;
            status.total = 47;
        }
    }

    public void setNumbers(GitChangeStatus numbers) {
        this.numbers = numbers;
        textAdditions.setText(getResources().getString(R.string.commit_additions, numbers.additions));
        textDeletions.setText(getResources().getString(R.string.commit_deletions, numbers.deletions));
        colorsView.setNumbers(numbers);
    }

    public GitChangeStatus getNumbers() {
        return numbers;
    }
}
