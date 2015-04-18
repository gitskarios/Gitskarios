package com.alorma.github.ui.adapter.repos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.Collection;
import java.util.List;

public class ReposAdapter extends ArrayAdapter<Repo> {

    private final LayoutInflater mInflater;
    private final int primaryDark;
    private final int secondaryTextColor;

    public ReposAdapter(Context context, List<Repo> repos) {
        super(context, 0, 0, repos);
        this.mInflater = LayoutInflater.from(context);

        this.primaryDark = AttributesUtils.getPrimaryDarkColor(context, R.style.AppTheme_Repos);
        this.secondaryTextColor = AttributesUtils.getPrimaryDarkColor(context, R.style.AppTheme_Repos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.row_repo, viewGroup, false);
        ReposHolder reposHolder = new ReposHolder(v);

        Repo repo = getItem(position);

        reposHolder.textTitle.setText(repo.name);
        reposHolder.textTitle.setTextColor(primaryDark);

        String starText = getContext().getResources().getString(R.string.star_icon_text, repo.stargazers_count);
        applyIcon(reposHolder.textStarts, Octicons.Icon.oct_star);
        reposHolder.textStarts.setText(starText);

        String forkText = getContext().getResources().getString(R.string.fork_icon_text, repo.forks_count);
        applyIcon(reposHolder.textForks, Octicons.Icon.oct_repo_forked);
        reposHolder.textForks.setText(forkText);

        if (repo.description != null) {
            reposHolder.textDescription.setVisibility(View.VISIBLE);
            reposHolder.textDescription.setText(repo.description);
        } else {
            reposHolder.textDescription.setVisibility(View.GONE);
        }

        if (repo.isPrivate) {
            reposHolder.repoPrivate.setVisibility(View.VISIBLE);
        } else {
            reposHolder.repoPrivate.setVisibility(View.GONE);
        }
        return v;
    }

    private void applyIcon(TextView textView, Octicons.Icon value) {
        IconicsDrawable drawableForks = new IconicsDrawable(getContext(), value);
        drawableForks.sizeRes(R.dimen.textSizeSmall);
        drawableForks.colorRes(R.color.repos_icons);
        textView.setCompoundDrawables(null, null, drawableForks, null);
        int offset = getContext().getResources().getDimensionPixelOffset(R.dimen.textSizeSmall);
        textView.setCompoundDrawablePadding(offset);
    }

    public void addAll(Collection<? extends Repo> collection, boolean paging) {
        if (!paging) {
            clear();
        }
        super.addAll(collection);
    }

}