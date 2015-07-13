package com.alorma.github.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.bean.ClearNotification;
import com.alorma.github.bean.UnsubscribeThreadNotification;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.squareup.otto.Bus;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import javax.inject.Inject;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsAdapter extends RecyclerArrayAdapter<Notification, NotificationsAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<NotificationsAdapter.HeaderViewHolder> {

    private final IconicsDrawable iconDrawable;
    @Inject
    Bus bus;

    public NotificationsAdapter(Context context, LayoutInflater inflater) {
        super(inflater);

        iconDrawable = new IconicsDrawable(context, Octicons.Icon.oct_check);
        iconDrawable.sizeRes(R.dimen.gapLarge);
        iconDrawable.color(AttributesUtils.getSecondaryTextColor(context));
    }

    @Override
    public long getHeaderId(int i) {
        return getItem(i).adapter_repo_parent_id;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new HeaderViewHolder(getInflater().inflate(R.layout.notification_row_header, viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int i) {
        final Notification item = getItem(i);
        headerViewHolder.tv.setText(item.repository.full_name);
        headerViewHolder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new UrlsManager(view.getContext()).manageRepos(Uri.parse(item.repository.html_url)));
            }
        });

        headerViewHolder.iv.setImageDrawable(iconDrawable);

        headerViewHolder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bus.post(new ClearNotification(item, true));
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.notification_row, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Notification notification) {
        holder.text.setText(notification.subject.title);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView text;
        private final ImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.text);
            iv = (ImageView) itemView.findViewById(R.id.clearNotifications);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bus != null && getItem(getAdapterPosition()) != null) {
                        bus.post(getItem(getAdapterPosition()));
                        bus.post(new ClearNotification(getItem(getAdapterPosition()), false));
                    }
                }
            });

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.notifications_row_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId()) {

                                case R.id.action_notification_unsubscribe:
                                    bus.post(new UnsubscribeThreadNotification(getItem(getAdapterPosition())));
                                    break;
                                case R.id.action_notification_mark_read:
                                    bus.post(new ClearNotification(getItem(getAdapterPosition()), false));
                                    break;

                            }

                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv;
        private final ImageView iv;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.text);
            iv = (ImageView) itemView.findViewById(R.id.clearNotifications);
        }
    }
}
