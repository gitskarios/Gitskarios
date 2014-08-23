package com.alorma.github.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;

public class NewIssueActivity extends BackActivity {

    public static Intent createLauncherIntent(Context context) {
        return new Intent(context, NewIssueActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_issue);
    }
}
