package com.alorma.github.ui.fragment.navigation;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.alorma.github.sdk.bean.dto.response.User;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public abstract class NavigationDrawerFragment extends Fragment {

    public NavigationDrawerCallbacks getmCallbacks() {
        return mCallbacks;
    }

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    protected void selectItem(int position) {
        if (mCallbacks != null) {
            mCallbacks.closeDrawer();
            mCallbacks.onNavigationDrawerItemSelected(position);
        }

        setItemSelected(position);
    }

    protected abstract void setItemSelected(int position);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    protected ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    protected Context getThemedContext() {
        return getActionBar().getThemedContext();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

        void closeDrawer();

        void profileSelected(User user);
    }
}
