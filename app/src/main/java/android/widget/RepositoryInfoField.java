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

    private RepositoryUiInfo repoInfo;
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
        if (isInEditMode()) {
            repoInfo = new RepositoryUiInfo(Iconify.IconValue.fa_adn, 8, "ADN");
        }
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        setPadding(getPaddingLeft(), (int) (12 * getResources().getDisplayMetrics().density), getPaddingRight(), getPaddingBottom());
        setCustomText();
    }

    private void setCustomText() {
        if (repoInfo != null) {
            String customText = "{" + repoInfo.getIcon() + "} " + "<b>" + repoInfo.getNum() + "</b> " + repoInfo.getText();
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
