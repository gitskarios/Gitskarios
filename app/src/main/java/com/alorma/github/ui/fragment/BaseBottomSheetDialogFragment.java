package com.alorma.github.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.ViewGroup;
import com.alorma.github.R;

public class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new CustomWidthBottomSheetDialog(getActivity(), getTheme());
  }

  static class CustomWidthBottomSheetDialog extends BottomSheetDialog {
    public CustomWidthBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
      super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      int width = getContext().getResources().getDimensionPixelSize(R.dimen.bottom_sheet_width);
      getWindow().setLayout(width > 0 ? width : ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
    }
  }
}
