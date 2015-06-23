package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GitChangeStatus;

/**
 * Created by Bernat on 23/06/2015.
 */
public class GitChangeStatusView extends LinearLayout {

    private TextView textNumbers;

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

        textNumbers = (TextView) findViewById(R.id.textNumbers);
    }

    public void setNumbers(GitChangeStatus numbers) {

    }
}
