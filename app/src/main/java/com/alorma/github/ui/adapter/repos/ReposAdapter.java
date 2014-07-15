package com.alorma.github.ui.adapter.repos;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.Repo;

import java.util.List;

public class ReposAdapter extends ArrayAdapter<Repo> {

    private final LayoutInflater mInflater;
    private Context context;

    public ReposAdapter(Context context, List<Repo> repos) {
        super(context, 0, 0, repos);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.row_repo, viewGroup, false);
        ReposHolder reposHolder = new ReposHolder(v);

        Repo repo = getItem(position);

        String name = repo.name;
        if (TextUtils.isEmpty(name)) {
            name = context.getResources().getString(R.string.no_gist_description);
        }
        reposHolder.text1.setText(name);
        reposHolder.text2.setText(repo.description);


        return v;
    }
}