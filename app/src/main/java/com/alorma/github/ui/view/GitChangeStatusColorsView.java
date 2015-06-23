package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GitChangeStatus;

/**
 * Created by Bernat on 23/06/2015.
 */
public class GitChangeStatusColorsView extends View {
    private int mColorAdditions;
    private int mColorDeletions;
    private GitChangeStatus status;
    private Paint paintAdditions;
    private Paint paintDeletions;
    private Rect rectView;
    private int leftRightPadding;

    public GitChangeStatusColorsView(Context context) {
        super(context);
        init();
    }

    public GitChangeStatusColorsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GitChangeStatusColorsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GitChangeStatusColorsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.mColorAdditions = getResources().getColor(R.color.commit_additions);
        this.mColorDeletions = getResources().getColor(R.color.commit_deletions);

        this.paintAdditions = new Paint();
        this.paintAdditions.setAntiAlias(true);
        this.paintAdditions.setStyle(Paint.Style.FILL);
        this.paintAdditions.setColor(this.mColorAdditions);

        this.paintDeletions = new Paint();
        this.paintDeletions.setAntiAlias(true);
        this.paintDeletions.setStyle(Paint.Style.FILL);
        this.paintDeletions.setColor(this.mColorDeletions);

        this.rectView = new Rect();

        if (isInEditMode()) {
            this.status = new GitChangeStatus();
            this.status.additions = 67;
            this.status.deletions = 34;
            this.status.total = 101;
        }

        this.leftRightPadding = getLeftPaddingOffset() + getRightPaddingOffset();
    }

    public void setNumbers(GitChangeStatus status) {
        this.status = status;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (status != null) {
            int total = status.total > 0 ? status.total : status.changes;
            if (total == 0) {
                total = status.additions + status.deletions;
            }
            if (status.additions > 0 && status.deletions > 0 && total > 0) {
                canvas.getClipBounds(rectView);

                int additionsPixels = (int) (((float)(rectView.right - rectView.left - leftRightPadding) / (float) total) * status.additions);

                canvas.drawRect(rectView.left + getLeftPaddingOffset(), rectView.top + getTopPaddingOffset(), additionsPixels, rectView.bottom - getBottomPaddingOffset(), paintAdditions);
                canvas.drawRect(additionsPixels, rectView.top + getTopPaddingOffset(), rectView.right - getRightPaddingOffset(), rectView.bottom - getBottomPaddingOffset(), paintDeletions);

            } else if (status.deletions == 0 && status.additions > 0) {
                canvas.drawColor(mColorAdditions);
            } else if (status.additions == 0 && status.deletions > 0) {
                canvas.drawColor(mColorDeletions);
            } else {
                canvas.drawColor(Color.TRANSPARENT);
            }
        }
    }
}
