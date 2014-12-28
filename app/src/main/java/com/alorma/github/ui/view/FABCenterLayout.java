package com.alorma.github.ui.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alorma.github.R;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by Bernat on 26/08/2014.
 */
public class FABCenterLayout extends RelativeLayout implements ViewTreeObserver.OnScrollChangedListener {

	private FABScrollContentListener fabScrollContentListener;
	private FloatingActionButton fabView;
	private int topId;
	private OnClickListener fabClickListener;
	private boolean fabVisible;
	private String fabTag;
	private View scrolledChild;
	private boolean forceVisbility;
	private int scrollable_id;

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
				scrollable_id = attr.getResourceId(R.styleable.FABCenterLayout_scrollable_id, 0);
				if (topId != 0) {
					fabVisible = true;
					createFabView();
				}
			}
		}
	}

	private void createFabView() {
		fabView = (FloatingActionButton) LayoutInflater.from(getContext()).inflate(R.layout.fab_white, this, false);

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
						addView(fabView);
					}
				}
			}
		}
	}

	public void setFabIcon(Drawable drawable) {
		if (fabView != null) {
			fabView.setDrawable(drawable);
		}
	}

	public void setFabColor(int color) {
		if (fabView != null) {
			fabView.setColorNormal(color);
		}
	}

	public void setFabColorPressed(int color) {
		if (fabView != null) {
			fabView.setColorPressed(color);
		}
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

	public void setFabViewVisibility(int visibility, boolean forced) {
		forceVisbility = forced;
		if (fabView != null) {
			fabView.setVisibility(visibility);
		}
	}

	@Override
	public void onScrollChanged() {

		if (!forceVisbility && scrolledChild != null) {
			int scrollY = scrolledChild.getScrollY();

			int fabViewHeight = fabView != null ? fabView.getHeight() : 0;
			int minimScroll = fabViewHeight / 2;

			setFabClickListener(scrollY < minimScroll ? fabClickListener : null, "");

			float alpha = ((float) (255 - scrollY)) / 255f;

			if (scrollY < minimScroll) {
				if (fabView != null) {
					ViewCompat.setAlpha(fabView, alpha);
				}
				setFabViewVisibility(View.VISIBLE, false);
			} else {
				setFabViewVisibility(View.INVISIBLE, false);
			}

			if (fabScrollContentListener != null) {
				fabScrollContentListener.onScrollFactor(scrollY, alpha);
			}
		}
	}

	private void addChildScrollListener(View child) {
		if (child != null && fabView != null && child != fabView && child.getId() != topId && child.getId() == scrollable_id) {
			scrolledChild = child;
			child.getViewTreeObserver().addOnScrollChangedListener(this);
		}
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		addChildScrollListener(child);
	}

	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
		addChildScrollListener(child);
	}

	@Override
	public void addView(View child, int width, int height) {
		super.addView(child, width, height);
		addChildScrollListener(child);
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		super.addView(child, params);
		addChildScrollListener(child);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		addChildScrollListener(child);
	}

	@Override
	protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
		addChildScrollListener(child);
		return super.addViewInLayout(child, index, params);
	}

	@Override
	protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
		addChildScrollListener(child);
		return super.addViewInLayout(child, index, params, preventRequestLayout);
	}

	public void setFabScrollContentListener(FABScrollContentListener fabScrollContentListener) {
		this.fabScrollContentListener = fabScrollContentListener;
	}

	public void removeFab() {
		if (fabView != null) {
			removeView(fabView);
			fabView = null;
		}
	}

	public int getFabId() {
		return fabView != null ? fabView.getId() : 0;
	}

	public interface FABScrollContentListener {
		void onScrollFactor(int alpha, float factor);
	}
}
