package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScroll extends ScrollView {

	private MyScrollListener myScrollListener;

	public MyScroll(Context context) {
		super(context);
		isInEditMode();
	}

	public MyScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		isInEditMode();
	}

	public MyScroll(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		isInEditMode();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public MyScroll(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		isInEditMode();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (myScrollListener != null) {
			myScrollListener.onScrollChanged(l, t, oldl, oldt);
		}
	}

	public MyScrollListener getMyScrollListener() {
		return myScrollListener;
	}

	public void setMyScrollListener(MyScrollListener myScrollListener) {
		this.myScrollListener = myScrollListener;
	}

	public interface MyScrollListener {
		void onScrollChanged(int l, int t, int oldl, int oldt);
	}
}