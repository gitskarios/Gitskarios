package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.CreateRepositoryFragment;

/**
 * Created by bernat.borras on 10/11/15.
 */
public class CreateRepositoryActivity extends BackActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar);

    setTitle(R.string.create_repository_activity);

    CreateRepositoryFragment fragment = new CreateRepositoryFragment();

    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.replace(R.id.content, fragment);
    ft.commit();
  }
}
