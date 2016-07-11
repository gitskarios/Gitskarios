package com.alorma.github.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BaseActivity;

public class OtpCodeActivity extends BaseActivity {

  public static final String EXTRA_MESSAGE = "MESSAGE";

  @Bind(R.id.otpCode) View buttonOtpCode;
  @Bind(R.id.otpCodeText) TextInputLayout otpCodeTextView;

  public static Intent createLauncherIntent(Context context, String message) {
    Intent intent = new Intent(context, OtpCodeActivity.class);

    intent.putExtra(EXTRA_MESSAGE, message);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome_otp);
    ButterKnife.bind(this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    String message = getIntent().getStringExtra(EXTRA_MESSAGE);

    if (message != null) {
      otpCodeTextView.setHint(message);
    }

    buttonOtpCode.setOnClickListener(view -> onOtpCode());
  }

  private void onOtpCode() {
    if (otpCodeTextView != null && otpCodeTextView.getEditText() != null) {
      String code = otpCodeTextView.getEditText().getText().toString();

      Intent data = new Intent();
      data.putExtra(EXTRA_MESSAGE, code);
      setResult(RESULT_OK, data);
      finish();
    }
  }
}
