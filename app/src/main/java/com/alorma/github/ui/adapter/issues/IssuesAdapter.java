package com.alorma.github.ui.adapter.issues;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesAdapter extends ArrayAdapter<Issue>{
    private final LayoutInflater mInflater;

    public IssuesAdapter(Context context, ListIssues issues) {
        super(context, 0, issues);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.row_issue, parent, false);

        TextView title = (TextView) v.findViewById(R.id.textTitle);
        TextView num = (TextView) v.findViewById(R.id.textNum);
        TextView autor = (TextView) v.findViewById(R.id.textAuthor);
        ImageView avatar = (ImageView) v.findViewById(R.id.avatarAuthor);
        ImageView pullRequest = (ImageView) v.findViewById(R.id.pullRequest);
        View state = v.findViewById(R.id.state);

        Issue item = getItem(position);

        title.setText(item.title);

        num.setText("#" + item.number);

        if (item.user != null) {
            autor.setText(Html.fromHtml(getContext().getString(R.string.issue_created_by, item.user.login)));
            ImageLoader instance = ImageLoader.getInstance();
            instance.displayImage(item.user.avatar_url, avatar);
        }

        int colorState = getContext().getResources().getColor(R.color.issue_state_close);
        if (IssueState.open == item.state) {
            colorState = getContext().getResources().getColor(R.color.issue_state_open);
        }

        state.setBackgroundColor(colorState);
        num.setTextColor(colorState);

        if (item.pullRequest != null) {
            IconDrawable iconDrawable = new IconDrawable(getContext(), Iconify.IconValue.fa_code_fork);
            iconDrawable.colorRes(R.color.gray_github_medium);
            pullRequest.setImageDrawable(iconDrawable);
        } else {
            IconDrawable iconDrawable = new IconDrawable(getContext(), Iconify.IconValue.fa_info_circle);
            iconDrawable.colorRes(R.color.gray_github_light_selected);
            pullRequest.setImageDrawable(iconDrawable);
        }

        return v;
    }
}
