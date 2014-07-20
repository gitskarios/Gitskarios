package com.alorma.github.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;

import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.adapter.users.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 20/07/2014.
 */
public class PopUpContributors extends ListPopupWindow implements AdapterView.OnItemClickListener {

    private ArrayList<User> users;
    private Context context;

    public PopUpContributors(Context context, ListContributors contributors) {
        super(context);
        this.context = context;

        if (contributors != null) {
            users = new ArrayList<User>(contributors.size());
            for (Contributor contributor : contributors) {
                users.add(contributor.author);
            }
            setAdapter(new UsersAdapter(context, users));

            setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (users != null && users.size() >= i) {
            Intent launcherIntent = ProfileActivity.createLauncherIntent(context, users.get(i));
            context.startActivity(launcherIntent);
        }
    }
}
