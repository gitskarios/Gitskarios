package com.alorma.github.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;

public class GoodByeActivity extends BackActivity {

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.github_logo) View githubLogo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_good_bye);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    toolbar.setTitle(R.string.good_bye_title);

    githubLogo.setOnClickListener(view -> openGithub());
  }

  private void openGithub() {
    Uri uri = Uri.parse("https://github.com/gitskarios/Gitskarios/");
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(uri);
    try {
      startActivity(intent);
    } catch (Exception e) {
      if (Fabric.isInitialized()) {
        CrashlyticsCore.getInstance().logException(e);
      }
    }
  }
}
