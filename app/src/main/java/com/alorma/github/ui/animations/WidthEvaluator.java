package com.alorma.github.ui.animations;

import android.animation.IntEvaluator;
import android.view.View;
import android.view.ViewGroup;

public class WidthEvaluator extends IntEvaluator {

	private View v;

	public WidthEvaluator(View v) {
		this.v = v;
	}

	@Override
	public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
		int num = super.evaluate(fraction, startValue, endValue);
		ViewGroup.LayoutParams params = v.getLayoutParams();
		params.width = num;
		v.setLayoutParams(params);
		return num;
	}
}