package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.bean.RepositoryUiInfo;

/**
 * Created by Bernat on 19/07/2014.
 */
public class RepositoryInfo extends ViewGroup implements View.OnClickListener {

    public void setOnRepoInfoListener(OnRepoInfoListener onRepoInfoListener) {
        this.onRepoInfoListener = onRepoInfoListener;
    }

    public OnRepoInfoListener onRepoInfoListener;

    public RepositoryInfo(Context context) {
        super(context);
        init();
    }

    public RepositoryInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepositoryInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        isInEditMode();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int size = getChildCount();

        int half = (r - l) / 2;

        int itemHeight = (int) (48 * getResources().getDisplayMetrics().density);

        int top = t;
        for (int i = 0; i < size; i++) {
            View childAt = getChildAt(i);
            int bottom = top + itemHeight;
            if ((i % 2) == 0) {
                childAt.layout(l, top, half, bottom);
            } else {
                childAt.layout(half, top, r, bottom);
                top = bottom;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int size = getChildCount();
        int itemHeight = (int) (48 * getResources().getDisplayMetrics().density);
        setMeasuredDimension(width, itemHeight * (Math.round(size / 2) + size % 2));
    }

    public void addRepoInfoField(RepositoryUiInfo info) {
        View v = new RepositoryInfoField(getContext(), info);
        v.setOnClickListener(this);
        addView(v);
    }

    @Override
    public void onClick(View view) {
        if (onRepoInfoListener != null) {
            if (view instanceof RepositoryInfoField) {
                RepositoryInfoField field = (RepositoryInfoField) view;
                RepositoryUiInfo info = field.repoInfo;
                int id = info.id;

                onRepoInfoListener.onRepoInfoFieldClick(id, field, info);
            }

        }
    }

    public interface OnRepoInfoListener {
        void onRepoInfoFieldClick(int id, RepositoryInfoField view, RepositoryUiInfo info);
    }
}

