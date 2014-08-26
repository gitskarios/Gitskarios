package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.alorma.github.R;

/**
 * Created by Bernat on 26/08/2014.
 */
public class FABCenterLayout extends RelativeLayout {
    private ImageView fabView;
    private int topId;
    private OnClickListener fabClickListener;

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
        isInEditMode();

        if (attrs != null) {
            TypedArray attr = getContext().obtainStyledAttributes(attrs, R.styleable.FABCenterLayout, defStyle, 0);

            if (attr.hasValue(R.styleable.FABCenterLayout_top_id)) {
                topId = attr.getResourceId(R.styleable.FABCenterLayout_top_id, 0);
                if (topId != 0) {
                    fabView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.fab, this, false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        fabView.setBackground(getResources().getDrawable(R.drawable.fab_inv_button));
                    } else {
                        fabView.setBackgroundDrawable(getResources().getDrawable(R.drawable.fab_inv_button));
                    }
                    fabView.setOnClickListener(fabClickListener);
                }
            }
        }


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (getChildCount() > 1) {
            if (topId != 0 && fabView != null) {
                //if (fabView.getParent() == null) {

                    View topView = findViewById(topId);

                    if (topView != null) {
                        int bottom = topView.getHeight();

                        if (bottom > 0) {
                            int int56 = getResources().getDimensionPixelOffset(R.dimen.fab);
                            int int16 = getResources().getDimensionPixelOffset(R.dimen.gapLarge);
                            fabView.layout(r - int56 - int16, bottom - int56 / 2, r - int16, bottom + int56 / 2);
                            removeView(fabView);
                            addView(fabView);
                        }
                    }
                //}
            }
        }
    }

    public void setFABDrawable(Drawable drawable) {
        if (fabView != null) {
            fabView.setImageDrawable(drawable);
        }
    }

     public void setFABBackground(Drawable drawable) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
             fabView.setBackground(drawable);
         } else {
             fabView.setBackgroundDrawable(drawable);
         }
     }

    public void setFabClickListener(OnClickListener fabClickListener) {
        this.fabClickListener = fabClickListener;
    }
}
