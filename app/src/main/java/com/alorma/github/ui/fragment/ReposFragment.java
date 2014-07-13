package com.alorma.github.ui.fragment;

import android.app.Fragment;
import android.app.ListFragment;

/**
 * Created by Bernat on 13/07/2014.
 */
public class ReposFragment extends ListFragment {
    public static ReposFragment newInstance(String username) {
        return new ReposFragment();
    }
}
