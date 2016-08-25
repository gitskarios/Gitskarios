package com.alorma.github.ui.activity.gists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.gists.GistsFragment;
import com.alorma.github.utils.AttributesUtils;

public class GistsMainActivity extends BackActivity {

  public static Intent createLauncherIntent(Context context, String username) {
    Intent intent = new Intent(context, GistsMainActivity.class);
    intent.putExtra("orgName", username);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    if (getToolbar() != null) {
      getToolbar().setBackgroundColor(AttributesUtils.getPrimaryColor(this));
    }

    setTitle(R.string.title_gists);

    String username = getIntent().getExtras().getString("orgName");

    showGistsFragment(username);
  }

  private void showGistsFragment(String username) {
    GistsFragment gistsFragment = GistsFragment.newInstance(username);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, gistsFragment);
    ft.commit();
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
