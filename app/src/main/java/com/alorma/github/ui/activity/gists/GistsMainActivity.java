package com.alorma.github.ui.activity.gists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.gists.GistsFragment;

public class GistsMainActivity extends BackActivity {

  public static Intent createLauncherIntent(Context context, String username) {
    Intent intent = new Intent(context, GistsMainActivity.class);
    intent.putExtra("username", username);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    setTitle(R.string.title_gists);

    String username = getIntent().getExtras().getString("username");

    showGistsFragment(username);
  }

  private void showGistsFragment(String username) {
    GistsFragment gistsFragment = GistsFragment.newInstance(username);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, gistsFragment);
    ft.commit();
  }
}
