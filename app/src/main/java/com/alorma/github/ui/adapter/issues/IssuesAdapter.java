package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesAdapter extends LazyAdapter<Issue> {

	public IssuesAdapter(Context context, ListIssues issues) {
		super(context, issues);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflate(R.layout.row_issue, parent, false);

		TextView title = (TextView) v.findViewById(R.id.textTitle);
		TextView num = (TextView) v.findViewById(R.id.textTime);
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
		GithubIconDrawable iconDrawable;
		if (item.pullRequest != null) {
			iconDrawable = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_git_pull_request);
		} else if (item.state == IssueState.closed) {
			iconDrawable = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_issue_closed);
		} else {
			iconDrawable = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_issue_opened);
		}
		iconDrawable.colorRes(R.color.gray_github_medium);
		pullRequest.setImageDrawable(iconDrawable);

		return v;
	}
}
