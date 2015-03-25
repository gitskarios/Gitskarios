package com.alorma.github.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ActionProvider;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.bean.NotificationsCount;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.nineoldandroids.animation.ValueAnimator;
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

		bt = new NotificationImageView(getContext(), getContext().getResources().getColor(R.color.repos_accent));
		bt.setOnClickListener(this);

		layout.addView(bt);

		GetNotificationsClient client = new GetNotificationsClient(getContext());
		client.setOnResultCallback(this);
		client.execute();

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
		if (currentNotifications != count.getSize()) {
			currentNotifications = count.getSize();
			bt.showNotificationBubble(count.getSize() > 0);
		}
	}
	
	public void setOnNotificationListener(OnNotificationListener onNotificationListener) {
		this.onNotificationListener = onNotificationListener;
	}

	public interface OnNotificationListener {
		void onNotificationRequested();
	}

	private class NotificationImageView extends ImageView implements ValueAnimator.AnimatorUpdateListener {

		private final Rect rect;
		private Paint paint;
		private boolean show;

		private long animationDuration = 500;
		private ValueAnimator animator;
		private int value;

		public NotificationImageView(Context context, int color) {
			super(context);
			rect = new Rect();
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(color);

			GithubIconDrawable drawable = new GithubIconDrawable(context, GithubIconify.IconValue.octicon_inbox);
			drawable.actionBarSize();
			drawable.colorRes(R.color.white);
			setImageDrawable(drawable);

			this.show = false;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			if (show || (animator != null && animator.isRunning())) {
				canvas.getClipBounds(rect);

				int radius = value;
				int top = rect.top + (int) animator.getAnimatedValue();
				int right = rect.right - (int) animator.getAnimatedValue();

				canvas.drawCircle(right, top, radius, paint);
			}
		}

		public void showNotificationBubble(boolean show) {
			this.show = show;

			int start = show ? 0 : 16;
			int end = show ? 16 : 0;

			animator = ValueAnimator.ofInt(start, end);
			animator.setDuration(animationDuration);
			animator.setInterpolator(show ? new BounceInterpolator() : new AccelerateDecelerateInterpolator());
			animator.addUpdateListener(this);
			animator.start();

			this.postInvalidate();
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			value = (int) animation.getAnimatedValue();
			this.postInvalidate();
		}
	}
}
