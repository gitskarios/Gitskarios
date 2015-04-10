package com.alorma.github.ui.view.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.alorma.github.R;

/**
 * Created by Bernat on 10/04/2014.
 */
public class RoundTimelineView extends TimelineView {
    public RoundTimelineView(Context context) {
        super(context);
    }

    public RoundTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundTimelineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void drawStart(Canvas canvas, Paint firstPaint, float centerX, float centerY, float mStartSize) {
        canvas.drawCircle(centerX, centerY, mStartSize + getLineMargin(), firstPaint);
    }

    @Override
    public void drawMiddle(Canvas canvas, Paint middlePaint, float centerX, float centerY, float mMiddleSize) {
        canvas.drawCircle(centerX, centerY, mMiddleSize + getLineMargin(), middlePaint);
    }

    @Override
    public void drawEnd(Canvas canvas, Paint lastPaint, float centerX, float centerY, float mLastSize) {
        canvas.drawCircle(centerX, centerY, mLastSize+ getLineMargin(), lastPaint);
    }

    private float getLineMargin() {
        return getResources().getDimension(R.dimen.gapSmall);
    }
}
