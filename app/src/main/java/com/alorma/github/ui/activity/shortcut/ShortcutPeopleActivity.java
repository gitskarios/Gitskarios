package com.alorma.github.ui.activity.shortcut;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.alorma.github.R;
import com.alorma.github.ui.activity.MainActivity;

public class ShortcutPeopleActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra(MainActivity.NAVIGATION, getString(R.string.shortcut_id_people));
    startActivity(intent);
    finish();
  }
}
