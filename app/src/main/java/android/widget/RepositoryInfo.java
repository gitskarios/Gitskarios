package android.widget;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.bean.RepositoryUiInfo;

import com.alorma.github.R;
import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 19/07/2014.
 */
public class RepositoryInfo extends ViewGroup implements View.OnClickListener {

    public static final int INFO_CONTRIBUTORS = 0;
    public static final int INFO_BRANCHES = 1;
    public static final int INFO_RELEASES = 2;
    public static final int INFO_ISSUES = 3;

    private ArrayMap<Integer, RepositoryInfoField> set;

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

        addRepoInfoField(new RepositoryUiInfo(INFO_CONTRIBUTORS, Iconify.IconValue.fa_group, 0, R.plurals.contributors));
        addRepoInfoField(new RepositoryUiInfo(INFO_BRANCHES, Iconify.IconValue.fa_code_fork, 0, R.plurals.branches));
        addRepoInfoField(new RepositoryUiInfo(INFO_RELEASES, Iconify.IconValue.fa_download, 0, R.plurals.releases));
        addRepoInfoField(new RepositoryUiInfo(INFO_ISSUES, Iconify.IconValue.fa_info_circle, 0, R.plurals.issues));
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
        if (set == null) {
            set = new ArrayMap<Integer, RepositoryInfoField>();
        }
        if (info != null) {
            RepositoryInfoField v = new RepositoryInfoField(getContext(), info);
            v.setOnClickListener(this);
            set.put(info.id, v);
            addView(v);
        }
    }

    public void addRepoInfoFieldNum(int id, int num) {
        if (set == null) {
            set = new ArrayMap<Integer, RepositoryInfoField>();
        }
        RepositoryInfoField v = set.get(id);
        if (v != null) {
            v.repoInfo.num = num;
            v.notifyDataSetChanged();
        }
    }
    public void addRepoInfoFieldIcon(int id, Iconify.IconValue icon) {
        if (set == null) {
            set = new ArrayMap<Integer, RepositoryInfoField>();
        }
        RepositoryInfoField v = set.get(id);
        if (v != null) {
            v.repoInfo.icon = icon;
            v.notifyDataSetChanged();
        }
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

