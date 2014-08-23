package com.alorma.github.ui.adapter.detail.issue;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.IssueComment;
import com.alorma.github.sdk.bean.dto.response.ListIssueComments;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssuesCommentsAdapter extends ArrayAdapter<IssueComment>{
    private final LayoutInflater mInflater;
    private boolean lazyLoading;

    public IssuesCommentsAdapter(Context context, ListIssueComments issueComments) {
        super(context, 0, issueComments);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        IssueComment item = getItem(position);

        TextView text1 = (TextView) v.findViewById(android.R.id.text1);

        text1.setText(Html.fromHtml("" + item.body_html));

        return v;
    }

    public void setLazyLoading(boolean lazyLoading) {
        this.lazyLoading = lazyLoading;
    }

    public boolean isLazyLoading() {
        return lazyLoading;
    }
}
