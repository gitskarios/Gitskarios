/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) [year] [fullname]
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.alorma.github.ui.view.timeline;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;

import com.alorma.github.R;

public abstract class TimelineView extends View {

    private int mLineColor = Color.GRAY;
    private float mLineWidth = 3f;

    private int mColorMiddle = -1;
    private float mMiddleSize = 2f;

    private int mFirstColor = -1;
    private float mStartSize = 2f;

    private int mLastColor = -1;
    private float mEndSize = 2f;

    private TimelineType timelineType = TimelineType.LINE;

    private Paint linePaint, middlePaint, firstPaint, lastPaint;
    private float startX, startY;
    private float endX, endY;
    private float centerX, centerY;

    public TimelineView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TimelineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        isInEditMode();
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.TimelineView, defStyle, 0);

            if (a != null) {
                mLineColor = a.getColor(R.styleable.TimelineView_tl_lineColor, mLineColor);

                mLineWidth = a.getDimension(R.styleable.TimelineView_tl_lineWidth, mLineWidth);

                mColorMiddle = a.getColor(R.styleable.TimelineView_tl_middleColor, mColorMiddle);

                mMiddleSize = a.getFloat(R.styleable.TimelineView_tl_middleSize, mMiddleSize);

                mFirstColor = a.getColor(R.styleable.TimelineView_tl_firstColor, mFirstColor);

                mStartSize = a.getFloat(R.styleable.TimelineView_tl_firstSize, mStartSize);

                mLastColor = a.getColor(R.styleable.TimelineView_tl_lastColor, mLastColor);

                mEndSize = a.getFloat(R.styleable.TimelineView_tl_lastSize, mEndSize);

                int type = a.getInt(R.styleable.TimelineView_tl_timeline_type, 0);

                this.timelineType = TimelineType.fromId(type);

                a.recycle();
            }
        }
        if (mColorMiddle == -1) {
            mColorMiddle = mLineColor;
        }

        if (mFirstColor == -1) {
            mFirstColor = mLineColor;
        }

        if (mLastColor == -1) {
            mLastColor = mLineColor;
        }

        createPaints();
    }

    private void createPaints() {

        linePaint = new Paint();
        linePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(mLineColor);

        middlePaint = new Paint();
        middlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        middlePaint.setColor(mColorMiddle);
        middlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        middlePaint.setStrokeWidth(mMiddleSize);

        firstPaint = new Paint();
        firstPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        firstPaint.setColor(mFirstColor);
        firstPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        firstPaint.setStrokeWidth(mStartSize);

        lastPaint = new Paint();
        lastPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        lastPaint.setColor(mLastColor);
        lastPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        lastPaint.setStrokeWidth(mEndSize);
    }

    public void setColorRes(@ColorRes int color) {
        setColor(getResources().getColor(color));
    }

    public void setColor(int color) {
        mColorMiddle = color;
        mFirstColor = color;
        mLastColor = color;
        createPaints();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        startX = contentWidth / 2 - mLineWidth / 2;
        endX = contentWidth / 2 + mLineWidth / 2;
        startY = paddingTop;
        endY = contentHeight;

        centerX = contentWidth / 2;
        centerY = contentHeight / 2;

        if (timelineType == TimelineType.START) {
            canvas.drawRect(startX, centerY, endX, endY, linePaint);
            drawStart(canvas, firstPaint, centerX, centerY, mStartSize);
        } else if (timelineType == TimelineType.MIDDLE) {
            canvas.drawRect(startX, startY, endX, endY, linePaint);
            drawMiddle(canvas, middlePaint, centerX, centerY, mMiddleSize);
        } else if (timelineType == TimelineType.END) {
            canvas.drawRect(startX, startY, endX, centerY, linePaint);
            drawEnd(canvas, lastPaint, centerX, centerY, mEndSize);
        } else {
            canvas.drawRect(startX, startY, endX, endY, linePaint);
        }
    }

    public TimelineType getTimelineType() {
        return timelineType;
    }

    public void setTimelineType(TimelineType timelineType) {
        this.timelineType = timelineType;
    }

    protected abstract void drawStart(Canvas canvas, Paint firstPaint, float centerX, float centerY, float mStartSize);

    protected abstract void drawMiddle(Canvas canvas, Paint middlePaint, float centerX, float centerY, float mMiddleSize);

    protected abstract void drawEnd(Canvas canvas, Paint lastPaint, float centerX, float centerY, float mEndSize);
}
