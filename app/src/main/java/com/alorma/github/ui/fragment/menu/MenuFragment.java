package com.alorma.github.ui.fragment.menu;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuFragment extends ListFragment {

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_menu, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add(new MenuItem(0, R.string.navigation_repos, Color.DKGRAY, Iconify.IconValue.fa_code));

    }
}
