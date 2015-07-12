package com.alorma.github.ui.adapter.issues;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesAdapter extends RecyclerArrayAdapter<Issue, IssuesAdapter.ViewHolder> {


    public IssuesAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_issue, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Issue item = getItem(position);

        holder.title.setText(item.title);

        holder.num.setText("#" + item.number);

        if (item.user != null) {
            holder.autor.setText(Html.fromHtml(holder.itemView.getResources().getString(R.string.issue_created_by, item.user.login)));
            ImageLoader instance = ImageLoader.getInstance();
            instance.displayImage(item.user.avatar_url, holder.avatar);
        }

        int colorState = holder.itemView.getResources().getColor(R.color.issue_state_close);
        if (IssueState.open == item.state) {
            colorState = holder.itemView.getResources().getColor(R.color.issue_state_open);
        }

        holder.state.setBackgroundColor(colorState);
        holder.num.setTextColor(colorState);
        IconicsDrawable iconDrawable;
        if (item.pullRequest != null) {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_git_pull_request);
        } else if (item.state == IssueState.closed) {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_issue_closed);
        } else {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_issue_opened);
        }
        iconDrawable.colorRes(R.color.gray_github_medium);
        holder.pullRequest.setImageDrawable(iconDrawable);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView num;
        private final TextView autor;
        private final ImageView avatar;
        private final ImageView pullRequest;
        private final View state;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textTitle);
            num = (TextView) itemView.findViewById(R.id.textTime);
            autor = (TextView) itemView.findViewById(R.id.textAuthor);
            avatar = (ImageView) itemView.findViewById(R.id.avatarAuthor);
            pullRequest = (ImageView) itemView.findViewById(R.id.pullRequest);
            state = itemView.findViewById(R.id.state);
        }
    }
}
