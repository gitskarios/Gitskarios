package com.alorma.github.ui.fragment.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.utils.KeyboardUtils;

/**
 * Created by Bernat on 12/08/2014.
 */
public class BaseFragment extends Fragment {
    protected MaterialDialog dialog;

    @Override
    public void onPause() {
        super.onPause();
        Activity activity = getActivity();
        if (dialog != null && dialog.isShowing() &&
                getActivity() != null && activity.getWindow() != null) {
            KeyboardUtils.lowerKeyboard(activity);
        }
    }
}
