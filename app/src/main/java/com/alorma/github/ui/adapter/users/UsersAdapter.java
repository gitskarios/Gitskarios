package com.alorma.github.ui.adapter.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersAdapter extends ArrayAdapter<User>{

    private final LayoutInflater mInflater;

    public UsersAdapter(Context context, List<User> users) {
        super(context, 0, 0, users);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.user_row, viewGroup, false);
        UsersHolder userHolder = new UsersHolder(v);

        User user = getItem(position);

        if (user.name != null) {
            userHolder.textView.setText(user.name);
        } else if (user.login != null) {
            userHolder.textView.setText(user.login);
        }

        Picasso.with(getContext()).load(user.avatar_url).into(userHolder.imageView);

        return v;
    }
}
