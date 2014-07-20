package com.alorma.github.ui.adapter.users;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersHolder extends RecyclerView.ViewHolder{
    public final ImageView imageView;
    public final TextView textView;

    public UsersHolder(View v) {
        super(v);
        this.imageView = (ImageView) v.findViewById(R.id.imageView);
        this.textView = (TextView) v.findViewById(R.id.forkNameRepo);
    }
}
