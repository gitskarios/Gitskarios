package com.alorma.github.ui.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alorma.github.R;
import com.getbase.floatingactionbutton.AddFloatingActionButton;

/**
 * Created by Bernat on 26/08/2014.
 */
public class FABCenterLayout extends RelativeLayout {
	private AddFloatingActionButton fabView;
	private int topId;
	private OnClickListener fabClickListener;
	private boolean fabVisible;
	private String fabTag;

	public FABCenterLayout(Context context) {
		super(context);
		init(null, 0);
	}

	public FABCenterLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public FABCenterLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		if (attrs != null) {
			TypedArray attr = getContext().obtainStyledAttributes(attrs, R.styleable.FABCenterLayout, defStyle, 0);

			if (attr.hasValue(R.styleable.FABCenterLayout_top_id)) {
				topId = attr.getResourceId(R.styleable.FABCenterLayout_top_id, 0);
				if (topId != 0) {
					fabVisible = true;
					createFabView();
				}
			}
		}
	}

	private void createFabView() {
		fabView = (AddFloatingActionButton) LayoutInflater.from(getContext()).inflate(R.layout.fab_white, this, false);

		fabView.setOnClickListener(fabClickListener);
		setFabTag();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (getChildCount() > 1) {
			if (topId != 0 && fabVisible && fabView != null) {
				View topView = findViewById(topId);

				if (topView != null) {
					int bottom = topView.getHeight();

					if (bottom > 0) {
						int int16 = getResources().getDimensionPixelOffset(R.dimen.gapLarge);
						fabView.layout(r - fabView.getWidth() - int16, bottom - fabView.getHeight() / 2, r - int16, bottom + fabView.getHeight() / 2);
						removeView(fabView);
						fabView.setAlpha(0f);
						addView(fabView);
						startFabTransition();
					}
				}
			}
		}
	}

	private void startFabTransition() {
		PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);
		ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(fabView, pvh);
		oa.setDuration(500);
		oa.setInterpolator(new AccelerateDecelerateInterpolator());
		oa.start();
	}

	public void setFabClickListener(OnClickListener fabClickListener, final String tag) {
		this.fabClickListener = fabClickListener;
		this.fabTag = tag;
		if (fabView != null) {
			fabView.setOnClickListener(fabClickListener);
			setFabTag();
		}
	}

	private void setFabTag() {
		if (fabView != null && fabTag != null) {
			fabView.setTag(fabTag);
			fabView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(v.getContext(), String.valueOf(v.getTag()), Toast.LENGTH_SHORT).show();
					return true;
				}
			});
		}
	}
}
