package com.alorma.github.ui.view;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.bean.NotificationsCount;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 18/02/2015.
 */
public class NotificationsActionProvider extends ActionProvider implements BaseClient.OnResultCallback<List<Notification>>, View.OnClickListener {

	private int currentNotifications = 0;
	private NotificationImageView bt;
	private OnNotificationListener onNotificationListener;

	/**
	 * Creates a new instance.
	 *
	 * @param context Context for accessing resources.
	 */
	public NotificationsActionProvider(Context context) {
		super(context);

		GitskariosApplication.get(context).inject(this);
	}

	@Override
	public View onCreateActionView() {

		int actionBarSize = getContext().getResources().getDimensionPixelSize(android.support.v7.appcompat.R.dimen.abc_action_button_min_height_material);

		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(actionBarSize, actionBarSize);

		LinearLayout layout = new LinearLayout(getContext());
		layout.setLayoutParams(layoutParams);
		layout.setGravity(Gravity.CENTER);
		layout.setOnClickListener(this);

		bt = new NotificationImageView(getContext());
		bt.setOnClickListener(this);
		newNotificationsSize(new NotificationsCount(0));

		layout.addView(bt);

		refresh();

		return layout;
	}

	@Override
	public void onResponseOk(List<Notification> notifications, Response r) {
		if (bt != null && notifications != null) {
			newNotificationsSize(new NotificationsCount(notifications.size()));
		}
	}

	@Override
	public void onFail(RetrofitError error) {

	}

	@Override
	public void onClick(View v) {
		if (bt != null & onNotificationListener != null) {
			onNotificationListener.onNotificationRequested();
		}
	}

	@Subscribe
	public void newNotificationsSize(NotificationsCount count) {
		bt.setNotificationVisible(count.getSize() > 0);
		if (currentNotifications != count.getSize()) {
			currentNotifications = count.getSize();
		}
	}
	
	public void setOnNotificationListener(OnNotificationListener onNotificationListener) {
		this.onNotificationListener = onNotificationListener;
	}

	public void refresh() {
		GetNotificationsClient client = new GetNotificationsClient(getContext());
		client.setOnResultCallback(this);
		client.execute();
	}

	public interface OnNotificationListener {
		void onNotificationRequested();
	}

	private class NotificationImageView extends ImageView  {

		public NotificationImageView(Context context) {
			super(context);
		}

		public void setNotificationVisible(boolean notificationVisible) {

			IconicsDrawable drawable = new IconicsDrawable(getContext(), Octicons.Icon.oct_inbox);
			drawable.actionBarSize();
			if (notificationVisible) {
				drawable.colorRes(R.color.repos_accent);
			} else {
				drawable.colorRes(R.color.white);
			}
			setImageDrawable(drawable);
		}
	}
}
