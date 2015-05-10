package com.alorma.github.ui.view;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alorma.github.R;

/**
 * Created by Bernat on 09/05/2015.
 */
public class FabToolbar extends FrameLayout implements View.OnClickListener {
    private Toolbar toolbar;
    private View floatingActionButton;
    private int fabId = -1;
    private int toolbarSize;

    public FabToolbar(Context context) {
        super(context);
    }

    public FabToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FabToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FabToolbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray attr = getContext().obtainStyledAttributes(attrs, R.styleable.FabToolbar, defStyle, 0);

        if (attr.hasValue(R.styleable.FabToolbar_ft_fabId)) {
            fabId = attr.getResourceId(R.styleable.FabToolbar_ft_fabId, 0);
        }

        attr.recycle();

        int[] toolbarSizeAttr = new int[]{android.support.v7.appcompat.R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = getContext().obtainStyledAttributes(attrs, toolbarSizeAttr);
        int padding = getResources().getDimensionPixelOffset(R.dimen.gapLarge);
        toolbarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1) + padding;
        a.recycle();

        toolbar = (Toolbar) LayoutInflater.from(getContext()).inflate(R.layout.toolbar, this, true).findViewById(R.id.toolbar);
        if (toolbar != null) {
            FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toolbarSize);
            params.gravity = Gravity.BOTTOM;
            toolbar.setLayoutParams(params);
            toolbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child.getId() == fabId) {
            floatingActionButton = child;
            floatingActionButton.setOnClickListener(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), toolbarSize);
    }

    @Override
    public void onClick(View view) {
        if (!isToolbarOpen()) {
            openToolbar();
        }
    }

    public boolean isToolbarOpen() {
        return toolbar != null && toolbar.getVisibility() == VISIBLE;
    }

    public void openToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            openToolbarMaterial();
        } else {
            openToolbarPreMaterial();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openToolbarMaterial() {
        // get the center for the clipping circle
        int cx = (floatingActionButton.getLeft() + floatingActionButton.getRight()) / 2;
        int cy = (floatingActionButton.getTop() + floatingActionButton.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(toolbar.getWidth(), toolbar.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(toolbar, cx, cy, 0, finalRadius);

        toolbar.setVisibility(VISIBLE);
        floatingActionButton.setVisibility(INVISIBLE);
        anim.start();
    }

    private void openToolbarPreMaterial() {

    }


    public void closeToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            closeToolbarMaterial();
        } else {
            closeToolbarPreMaterial();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void closeToolbarMaterial() {
        // get the center for the clipping circle
        int cx = (floatingActionButton.getLeft() + floatingActionButton.getRight()) / 2;
        int cy = (floatingActionButton.getTop() + floatingActionButton.getBottom()) / 2;

        int startRadius = Math.max(toolbar.getWidth(), toolbar.getHeight());

        // get the final radius for the clipping circle
        int finalRadius = Math.max(floatingActionButton.getWidth(), floatingActionButton.getHeight());


        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(toolbar, cx, cy, startRadius, finalRadius);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                floatingActionButton.setVisibility(VISIBLE);
                toolbar.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        floatingActionButton.setVisibility(INVISIBLE);
        anim.start();
    }

    private void closeToolbarPreMaterial() {

    }

    public Menu inflate(@MenuRes int menuRes, Toolbar.OnMenuItemClickListener menuItemClickListener) {
        toolbar.inflateMenu(menuRes);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);
        return toolbar.getMenu();
    }

    public void setToolbarTitle(@StringRes int title)  {
        toolbar.setTitle(title);
    }
}
