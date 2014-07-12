package com.alorma.github.ui.activity;

import android.os.Bundle;

import com.alorma.github.ui.fragment.UserPublicGistsFragment;
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
}
