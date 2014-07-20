package android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.bean.RepositoryUiInfo;

import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 19/07/2014.
 */
public class RepositoryInfoField extends TextView {

    public RepositoryUiInfo repoInfo;
    private Rect rect;
    private Paint paint;

    public RepositoryInfoField(Context context) {
        super(context);
        init();
    }

    public RepositoryInfoField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepositoryInfoField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RepositoryInfoField(Context context, RepositoryUiInfo info) {
        super(context, null);
        this.repoInfo = info;
        init();
    }

    private void init() {
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        int pL = (int) (24 * getResources().getDisplayMetrics().density);
        int pT = (int) (18 * getResources().getDisplayMetrics().density);
        setPadding(pL, pT, getPaddingRight(), getPaddingBottom());
        setCustomText();
    }

    private void setCustomText() {
        if (repoInfo != null) {
            String pluralText = getResources().getQuantityString(repoInfo.text, repoInfo.num, repoInfo.num);
            String customText = "{" + repoInfo.icon + "} " + pluralText;
            setText(Html.fromHtml(customText));
            Iconify.addIcons(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = (int) (48 * getResources().getDisplayMetrics().density);
        setMeasuredDimension(widthMeasureSpec, height);
    }
}
