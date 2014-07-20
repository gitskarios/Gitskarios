package com.alorma.github.ui.adapter.repos;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EnhancedTextDelegate;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.List;

public class ReposAdapter extends ArrayAdapter<Repo> {

    private final LayoutInflater mInflater;

    private int textTitleColor;

    public ReposAdapter(Context context, List<Repo> repos) {
        super(context, 0, 0, repos);
        this.mInflater = LayoutInflater.from(context);
        this.textTitleColor = context.getResources().getColor(R.color.accentDark);
    }
    public ReposAdapter(Context context, List<Repo> repos, int color) {
        super(context, 0, 0, repos);
        this.mInflater = LayoutInflater.from(context);
        if (color == -1) {
            color = context.getResources().getColor(R.color.accentDark);
        }
        this.textTitleColor = color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.row_repo, viewGroup, false);
        ReposHolder reposHolder = new ReposHolder(v);

        Repo repo = getItem(position);

        reposHolder.textTitle.setText(repo.name);

        if (textTitleColor == 0) {
            textTitleColor = getContext().getResources().getColor(R.color.gray_github_dark);
        }
        reposHolder.textTitle.setTextColor(textTitleColor);

        IconDrawable typeDrawable = new IconDrawable(getContext(), Iconify.IconValue.fa_book);
        if (repo.fork) {
            typeDrawable = new IconDrawable(getContext(), Iconify.IconValue.fa_code_fork);
        }
        typeDrawable.colorRes(R.color.gray_github_medium);
        reposHolder.imageRepoType.setImageDrawable(typeDrawable);

        reposHolder.textDescription.setText(repo.description);

        String starText = getContext().getResources().getString(R.string.star_icon_text, repo.stargazers_count);
        reposHolder.textStarts.setText(starText);

        String forkText = getContext().getResources().getString(R.string.fork_icon_text, repo.forks_count);
        reposHolder.textForks.setText(forkText);

        reposHolder.textDescription.setText(repo.description);

        reposHolder.textLanguage.setText(repo.language);
        return v;
    }

    public void setTextTitleColor(int textTitleColor) {
        this.textTitleColor = textTitleColor;
        notifyDataSetChanged();
    }
}