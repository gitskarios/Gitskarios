package android.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.bean.RepositoryUiInfo;

import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 19/07/2014.
 */
public class RepositoryInfo extends ViewGroup {

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

        List<RepositoryUiInfo> infos = new ArrayList<RepositoryUiInfo>();
        infos.add(new RepositoryUiInfo(Iconify.IconValue.fa_adn, 7, "ADN"));
        infos.add(new RepositoryUiInfo(Iconify.IconValue.fa_book, 3, "BOOK"));
        infos.add(new RepositoryUiInfo(Iconify.IconValue.fa_book, 3, "BOOK"));
        infos.add(new RepositoryUiInfo(Iconify.IconValue.fa_book, 3, "BOOK"));
        infos.add(new RepositoryUiInfo(Iconify.IconValue.fa_book, 3, "BOOK"));
        infos.add(new RepositoryUiInfo(Iconify.IconValue.fa_bars, 1, "BARS"));

        for (RepositoryUiInfo info : infos) {
            addView(new RepositoryInfoField(getContext(), info));
        }
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
}

