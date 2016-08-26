package com.alorma.github.ui.fragment.donate;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;

public class DonateActivity extends BackActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, DonateFragment.newInstance());
    ft.commit();
  }
}
