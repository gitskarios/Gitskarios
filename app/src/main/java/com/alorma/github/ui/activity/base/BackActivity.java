package com.alorma.github.ui.activity.base;

import android.view.MenuItem;

public class BackActivity extends BaseActivity {

  @Override
  public void onStart() {
    super.onStart();
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      close(true);
      return true;
    }
    return false;
  }

  @Override
  public void onBackPressed() {
    close(false);
  }

  protected void close(boolean navigateUp) {
    finish();
  }
}
