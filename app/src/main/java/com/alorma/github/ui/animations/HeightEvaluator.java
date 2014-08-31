package com.alorma.github.ui.animations;

import android.animation.IntEvaluator;
import android.view.View;
import android.view.ViewGroup;

public class HeightEvaluator extends IntEvaluator {

	private View v;
	private boolean expand;

	public HeightEvaluator(View v, boolean expand) {
		this.v = v;
		this.expand = expand;
	}

	@Override
	public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
		int num = super.evaluate(fraction, startValue, endValue);
		ViewGroup.LayoutParams params = v.getLayoutParams();
		params.height = (int) ((expand ? endValue : startValue) * (expand ? fraction : 1 - fraction));
		v.setLayoutParams(params);
		return num;
	}
}