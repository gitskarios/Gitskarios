package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;

import com.alorma.github.R;

/**
 * Created by Bernat on 13/07/2014.
 */
public class TabTitle extends TextView {

    private int rgb;
    private Rect rect;
    private float size;
    private Paint paint;

    private ColorStateList barColors;

    public TabTitle(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TabTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TabTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.TabTitle, defStyle, 0);

        if (attr.hasValue(R.styleable.TabTitle_barColor)) {
            barColors = attr.getColorStateList(R.styleable.TabTitle_barColor);
        } else {
            barColors = getTextColors();
        }

        isInEditMode();

        setGravity(Gravity.CENTER);

        rgb = getResources().getColor(R.color.accent);

        rect = new Rect();

        size = 5 * getResources().getDisplayMetrics().density;

        paint = new Paint();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        setRgb(getCurrentDrawableColor(barColors));
    }

    protected int getCurrentDrawableColor(ColorStateList colors) {
        return colors.getColorForState(getDrawableState(), getCurrentTextColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(rgb);

        canvas.getClipBounds(rect);
        if (isSelected()) {
            rect.top = (int) (rect.bottom - size);
        } else {
            rect.top = (int) (rect.bottom - size / 2);
        }
        canvas.drawRect(rect, paint);
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
        invalidate();
    }
}
