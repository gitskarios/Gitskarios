package com.alorma.github.ui.adapter.detail.issue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.IssueComment;
import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.nostra13.universalimageloader.core.ImageLoader;

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
        final ViewHolder vh;
        if ( convertView == null ) {
            View view = mInflater.inflate( R.layout.row_issue_discussion, parent, false );
            vh = ViewHolder.create( (LinearLayout)view );
            vh.webview = new WebView(getContext());
            vh.webview.getSettings().setUseWideViewPort(false);
            view.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }

        vh.webContainer.removeAllViews();
        vh.webContainer.addView(vh.webview);

        IssueComment item = getItem( position );

        vh.webview.loadData(item.body_html, "text/html", "UTF-8");

        ImageLoader.getInstance().displayImage(item.user.avatar_url, vh.avatar);

        vh.textAuthor.setText(item.user.login);

        return vh.rootView;
    }

    public void setLazyLoading(boolean lazyLoading) {
        this.lazyLoading = lazyLoading;
    }

    public boolean isLazyLoading() {
        return lazyLoading;
    }
    private static class ViewHolder {
        public final LinearLayout rootView;
        public WebView webview;
        public final ViewGroup webContainer;
        public final ImageView avatar;
        public final TextView textAuthor;

        private ViewHolder(LinearLayout rootView, ViewGroup webContainer, ImageView avatar, TextView textAuthor) {
            this.rootView = rootView;
            this.webContainer = webContainer;
            this.avatar = avatar;
            this.textAuthor = textAuthor;
        }

        public static ViewHolder create(LinearLayout rootView) {
            ViewGroup webContainer = (ViewGroup) rootView.findViewById(R.id.webContainer);
            ImageView avatar = (ImageView) rootView.findViewById(R.id.avatarAuthor);
            TextView textAuthor = (TextView) rootView.findViewById(R.id.textAuthor);
            return new ViewHolder( rootView, webContainer, avatar, textAuthor);
        }
    }
}
