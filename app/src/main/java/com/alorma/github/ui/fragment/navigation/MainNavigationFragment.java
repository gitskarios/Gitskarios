package com.alorma.github.ui.fragment.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alorma.github.R;

/**
 * Created by Bernat on 12/07/2014.
 */
public class MainNavigationFragment extends NavigationDrawerFragment implements AdapterView.OnItemClickListener {

    private ListView mDrawerListView;

    private int mCurrentSelectedPosition = 0;
    private String[] items;

    public static MainNavigationFragment newInstance() {
        return new MainNavigationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(this);

        items = new String[]{
                getString(R.string.navigation_gists),
                getString(R.string.navigation_profile),
                getString(R.string.navigation_profile),
                getString(R.string.navigation_profile)
        };

        ArrayAdapter adapter = new ArrayAdapter<String>(
                getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                items
        );
        mDrawerListView.setAdapter(adapter);


        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        selectItem(1);

        return mDrawerListView;
    }

    @Override
    protected void setItemSelected(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }
}
