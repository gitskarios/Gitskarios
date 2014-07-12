package com.alorma.github.ui.fragment;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.ui.fragment.navigation.NavigatedFragment;

public class GistsFragment extends NavigatedFragment {

    public static GistsFragment newInstance() {
        return new GistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContainerFragment(UserPublicGistsFragment.newInstance());
    }

    @Override
    protected int getTitle() {
        return R.string.title_gists;
    }
}
