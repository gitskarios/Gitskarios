package com.alorma.github.ui.activity.gists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.gists.GistFileFragment;

public class GistsFileActivity extends BackActivity {

  public static Intent createLauncherIntent(Context context, String name, String content) {
    Bundle bundle = new Bundle();
    bundle.putString(GistFileFragment.FILE_NAME, name);
    bundle.putString(GistFileFragment.CONTENT, content);

    Intent intent = new Intent(context, GistsFileActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    GistFileFragment fileFragment = new GistFileFragment();
    fileFragment.setArguments(getIntent().getExtras());
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, fileFragment);
    ft.commit();

    String title = getIntent().getExtras().getString(GistFileFragment.FILE_NAME);
    setTitle(title);
  }

  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_Gists;
  }

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_Gists;
  }
}
