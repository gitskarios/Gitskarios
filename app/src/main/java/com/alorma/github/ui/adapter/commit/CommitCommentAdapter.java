package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.utils.AttributesUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Bernat on 23/06/2015.
 */
public class CommitCommentAdapter extends LazyAdapter<CommitComment> {

    private final LayoutInflater mInflater;
    private RepoInfo repoInfo;

    public CommitCommentAdapter(Context context, List<CommitComment> objects, RepoInfo repoInfo) {
        super(context, 0, objects);
        this.repoInfo = repoInfo;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.commit_comment_row, parent, false);

        TextView textContent = (TextView) v.findViewById(R.id.textContent);
        TextView textAuthor = (TextView) v.findViewById(R.id.textAuthor);
        ImageView imageAuthor = (ImageView) v.findViewById(R.id.avatarAuthor);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        CommitComment commitComment = getItem(position);

        if (commitComment.user != null) {

            textAuthor.setText(commitComment.user.login);

            if (commitComment.user.avatar_url != null) {
                ImageLoader.getInstance().displayImage(commitComment.user.avatar_url, imageAuthor);
            } else {
                IconicsDrawable iconDrawable = new IconicsDrawable(getContext(), Octicons.Icon.oct_octoface);
                iconDrawable.color(AttributesUtils.getSecondaryTextColor(getContext()));
                iconDrawable.sizeDp(36);
                iconDrawable.setAlpha(128);
                imageAuthor.setImageDrawable(iconDrawable);
            }
        }

        if (commitComment.body_html != null) {
            String htmlCode = HtmlUtils.format(commitComment.body_html).toString();
            HttpImageGetter imageGetter = new HttpImageGetter(getContext());
            imageGetter.repoInfo(repoInfo);
            imageGetter.bind(textContent, htmlCode, commitComment.hashCode());
            textContent.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
        }

        toolbar.setVisibility(View.INVISIBLE);
        /*toolbar.inflateMenu(R.menu.menu_commit_content);

        if (toolbar.getMenu() != null) {
            MenuItem replyItem = toolbar.getMenu().findItem(R.id.action_reply);
            IconicsDrawable replyDrawable = new IconicsDrawable(toolbar.getContext(), Octicons.Icon.oct_mail_reply);
            replyDrawable.colorRes(R.color.accent);
            replyDrawable.actionBar();
            replyItem.setIcon(replyDrawable);
        }*/

        return v;
    }
}
