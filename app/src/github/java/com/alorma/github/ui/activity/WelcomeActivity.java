package com.alorma.github.ui.activity;

import android.accounts.AccountAuthenticatorActivity;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;

public class WelcomeActivity extends AccountAuthenticatorActivity
    implements WelcomePresenterViewInterface {

  @Bind(R.id.openLogin) View buttonLogin;
  @Bind(R.id.login_username) TextInputLayout loginUsername;
  @Bind(R.id.login_password) TextInputLayout loginPassword;
  private WelcomePresenter welcomePresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    ButterKnife.bind(this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    welcomePresenter = new WelcomePresenter(this);

    buttonLogin.setEnabled(false);

    TextWatcher buttonEnablerTextWatcher = new ButtonEnablerTextWatcher();
    addTextWatcher(loginUsername, buttonEnablerTextWatcher);
    addTextWatcher(loginPassword, buttonEnablerTextWatcher);

    buttonLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String username = getFromTextInputLayout(loginUsername);
        String passwords = getFromTextInputLayout(loginPassword);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(passwords)) {
          login(username, passwords);
        }
      }
    });
  }

  private void login(String username, String passwords) {
    welcomePresenter.login(username, passwords);
  }

  @Override
  public void onErrorUnauthorized() {

  }

  @Override
  public void onErrorTwoFactorException() {
    new MaterialDialog.Builder(this).title(R.string.write_otp_code_title)
        .content(R.string.write_otp_code)
        .input(getString(R.string.write_otp_code_hint), null, false,
            new MaterialDialog.InputCallback() {
              @Override
              public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                welcomePresenter.setOtpCode(String.valueOf(input));
              }
            })
        .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
        .show();
  }

  @Override
  public void onGenericError() {

  }

  @Override
  public void finishAccess(final User user) {
    Toast.makeText(this, user.login, Toast.LENGTH_SHORT).show();
    setResult(Activity.RESULT_OK);

    MainActivity.startActivity(this);
    finish();
  }

  private class ButtonEnablerTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      String username = getFromTextInputLayout(loginUsername);
      String passwords = getFromTextInputLayout(loginPassword);

      buttonLogin.setEnabled(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(passwords));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }

  public String getFromTextInputLayout(TextInputLayout inputLayout) {
    if (inputLayout != null
        && inputLayout.getEditText() != null
        && inputLayout.getEditText().getText() != null) {
      return inputLayout.getEditText().getText().toString();
    }
    return null;
  }

  public void addTextWatcher(TextInputLayout inputLayout, TextWatcher textWatcher) {
    if (inputLayout != null && inputLayout.getEditText() != null && textWatcher != null) {
      inputLayout.getEditText().addTextChangedListener(textWatcher);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    welcomePresenter.start(this);
  }

  @Override
  protected void onStop() {

    welcomePresenter.stop();
    super.onStop();
  }
}
