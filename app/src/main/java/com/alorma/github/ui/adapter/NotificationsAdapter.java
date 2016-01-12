package com.alorma.github.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.bean.NotificationsParent;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsAdapter extends RecyclerArrayAdapter<NotificationsParent, NotificationsAdapter.ViewHolder> {

    private final IconicsDrawable iconDrawable;
    private NotificationsAdapterListener notificationsAdapterListener;

    public NotificationsAdapter(Context context, LayoutInflater inflater) {
        super(inflater);

        iconDrawable = new IconicsDrawable(context, Octicons.Icon.oct_check);
        iconDrawable.sizeDp(14);

        iconDrawable.color(AttributesUtils.getIconsColor(context));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.notification_parent_row, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, NotificationsParent notification) {
        holder.text_repo.setText(notification.repo.full_name);
        holder.clear_all_repo.setImageDrawable(iconDrawable);

        for (Notification noti : notification.notifications) {
            View view = getInflater().inflate(R.layout.notification_row, holder.notificationsPlaceHolder, false);
            bindNotificationView(view, noti);
            holder.notificationsPlaceHolder.addView(view);
        }
    }

    private void bindNotificationView(View view, final Notification noti) {
        TextView name = (TextView) view.findViewById(R.id.text);
        String type = noti.subject.type;
        String title = noti.subject.title;
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("<b>").append("[").append(type).append("] ").append("</b>").append(title);
        name.setText(Html.fromHtml(msgBuilder.toString()));

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationsAdapterListener != null) {
                    notificationsAdapterListener.onNotificationClick(noti);
                }
            }
        });

        ImageView overflow = (ImageView) view.findViewById(R.id.overflow);
        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.notifications_row_menu);
                popupMenu.setOnMenuItemClickListener(new MenuListener(noti));
                popupMenu.show();
            }
        });
    }

    public void setNotificationsAdapterListener(NotificationsAdapterListener notificationsAdapterListener) {
        this.notificationsAdapterListener = notificationsAdapterListener;
    }

    public interface NotificationsAdapterListener {
        void onNotificationClick(Notification notification);

        void clearNotifications(Notification notification);

        void unsubscribeThreadNotification(Notification notification);

        void requestRepo(NotificationsParent item);

        void clearRepoNotifications(NotificationsParent item);
    }

    private class MenuListener implements PopupMenu.OnMenuItemClickListener {

        private Notification notification;

        public MenuListener(Notification notification) {
            this.notification = notification;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_notification_unsubscribe:
                    if (notificationsAdapterListener != null) {
                        notificationsAdapterListener.unsubscribeThreadNotification(notification);
                    }
                    break;
                case R.id.action_notification_mark_read:
                    if (notificationsAdapterListener != null) {
                        notificationsAdapterListener.clearNotifications(notification);
                    }
                    break;
            }
            return true;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView text_repo;
        private final ImageView clear_all_repo;
        private final ViewGroup notificationsPlaceHolder;

        public ViewHolder(View itemView) {
            super(itemView);
            text_repo = (TextView) itemView.findViewById(R.id.text_repo);
            clear_all_repo = (ImageView) itemView.findViewById(R.id.clear_all_repo);
            notificationsPlaceHolder = (ViewGroup) itemView.findViewById(R.id.notificationsPlaceHolder);

            text_repo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notificationsAdapterListener != null) {
                        NotificationsParent item = getItem(getAdapterPosition());
                        notificationsAdapterListener.requestRepo(item);
                    }
                }
            });

            clear_all_repo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notificationsAdapterListener != null) {
                        NotificationsParent item = getItem(getAdapterPosition());
                        notificationsAdapterListener.clearRepoNotifications(item);
                    }
                }
            });
        }
    }
}
