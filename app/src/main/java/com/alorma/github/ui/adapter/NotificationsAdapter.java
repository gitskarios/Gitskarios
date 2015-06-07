package com.alorma.github.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.bean.ClearNotification;
import com.alorma.github.bean.UnsubscribeThreadNotification;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.UrlsManager;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsAdapter extends ArrayAdapter<Notification> implements StickyListHeadersAdapter {

    private final LayoutInflater mInflater;
    private final IconicsDrawable iconDrawable;

    @Inject
    Bus bus;

    public NotificationsAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
        mInflater = LayoutInflater.from(context);

        iconDrawable = new IconicsDrawable(getContext(), Octicons.Icon.oct_check);
        iconDrawable.sizeRes(R.dimen.gapLarge);
        iconDrawable.color(AttributesUtils.getSecondaryTextColor(getContext()));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.notification_row, parent, false);


        final Notification item = getItem(position);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bus != null && item != null) {
                    bus.post(item);
                    bus.post(new ClearNotification(item, false));
                }
            }
        });

        TextView text = (TextView) v.findViewById(R.id.text);
        text.setText(item.subject.title);

        ImageView iv = (ImageView) v.findViewById(R.id.clearNotifications);
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
                                bus.post(new UnsubscribeThreadNotification(item));
                                break;
                            case R.id.action_notification_mark_read:
                                bus.post(new ClearNotification(item, false));
                                break;

                        }

                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return v;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.notification_row_header, viewGroup, false);

        final Notification item = getItem(i);

        TextView tv = (TextView) v.findViewById(R.id.text);
        tv.setText(item.repository.full_name);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new UrlsManager(view.getContext()).manageRepos(Uri.parse(item.repository.html_url)));
            }
        });

        ImageView iv = (ImageView) v.findViewById(R.id.clearNotifications);
        iv.setImageDrawable(iconDrawable);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bus.post(new ClearNotification(item, true));
            }
        });

        return v;
    }

    @Override
    public long getHeaderId(int i) {
        return getItem(i).adapter_repo_parent_id;
    }

}
