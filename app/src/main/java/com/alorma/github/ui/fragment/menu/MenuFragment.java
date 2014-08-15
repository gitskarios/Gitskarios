package com.alorma.github.ui.fragment.menu;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.ui.adapter.MenuItemsAdapter;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuFragment extends ListFragment {

    private OnMenuItemSelectedListener onMenuItemSelectedListener;
    private MenuItemsAdapter adapter;
    private int currentSelectedItemId;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.custom_list_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MenuItemsAdapter(getActivity(), new ArrayList<MenuItem>());

        setListAdapter(adapter);

        int color = getResources().getColor(R.color.accent);

        currentSelectedItemId = 0;

        adapter.add(new CategoryMenuItem(-1, R.string.tab_repos_parent, color, Iconify.IconValue.fa_code));

        adapter.add(new MenuItem(0, R.string.navigation_repos, color, Iconify.IconValue.fa_code));
        adapter.add(new MenuItem(1, R.string.navigation_starred_repos, color, Iconify.IconValue.fa_star));
        adapter.add(new MenuItem(2, R.string.navigation_watched_repos, color, Iconify.IconValue.fa_eye));

        adapter.add(new CategoryMenuItem(-2, R.string.tab_people_parent, color, Iconify.IconValue.fa_code));

        adapter.add(new MenuItem(3, R.string.navigation_followers, color, Iconify.IconValue.fa_user));
        adapter.add(new MenuItem(4, R.string.navigation_following, color, Iconify.IconValue.fa_user));

        if (onMenuItemSelectedListener != null) {
            onMenuItemSelectedListener.onMenuItemSelected(adapter.getItem(1));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        MenuItem item = adapter.getItem(position);

        if (checkItem(item)) {
            currentSelectedItemId = item.id;
            switch (item.id) {
                case 0:
                    onMenuItemSelectedListener.onReposSelected();
                    break;
                case 1:
                    onMenuItemSelectedListener.onStarredSelected();
                    break;
                case 2:
                    onMenuItemSelectedListener.onWatchedSelected();
                    break;
                case 3:
                    onMenuItemSelectedListener.onFollowersSelected();
                    break;
                case 4:
                    onMenuItemSelectedListener.onFollowingSelected();
                    break;
            }
            onMenuItemSelectedListener.onMenuItemSelected(item);
        } else {
            onMenuItemSelectedListener.closeMenu();
        }
    }

    private boolean checkItem(MenuItem item) {
        return item != null && onMenuItemSelectedListener != null && item.id != currentSelectedItemId;
    }

    public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener onMenuItemSelectedListener) {
        this.onMenuItemSelectedListener = onMenuItemSelectedListener;
    }

    public interface OnMenuItemSelectedListener {
        void onReposSelected();

        void onStarredSelected();

        void onWatchedSelected();

        void onFollowersSelected();

        void onFollowingSelected();

        void onMenuItemSelected(@NonNull MenuItem item);

        void closeMenu();
    }
}
