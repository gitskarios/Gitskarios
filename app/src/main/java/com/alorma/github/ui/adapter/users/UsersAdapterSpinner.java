package com.alorma.github.ui.adapter.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;

import java.util.Collection;
import java.util.List;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersAdapterSpinner extends ArrayAdapter<User> {

    private final LayoutInflater mInflater;

    public UsersAdapterSpinner(Context context, List<User> users) {
        super(context, 0, users);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.item_profile, viewGroup, false);
        UsersHolder userHolder = new UsersHolder(v);

        User user = getItem(position);

        userHolder.fill(user);

        return v;
    }

    public void addAll(Collection<? extends User> collection, boolean paging) {
        if (!paging) {
            clear();
        }
        super.addAll(collection);
    }
}
