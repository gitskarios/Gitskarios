package com.alorma.github.ui.fragment.navigation;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.alorma.github.R;

/**
 * Created by Bernat on 12/07/2014.
 */
public class NavigatedFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setTitle("User gists");
        }
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    protected void setContainerFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

}
