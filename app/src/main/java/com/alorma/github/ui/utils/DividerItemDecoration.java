package com.alorma.github.ui.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
 
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Grid support was added by Avi Ben - Hamo
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
 
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
 
    public static final int LIST_HORIZONTAL = LinearLayoutManager.HORIZONTAL;
 
    public static final int LIST_VERTICAL = LinearLayoutManager.VERTICAL;
 
    public static final int GRID_STROKE = 3;
 
    public static final int GRID_FILL = 4;
 
 
    private Drawable mDivider;
 
    private int mOrientation;
 
    public DividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }
 
    public void setOrientation(int orientation) {
        if (orientation != LIST_HORIZONTAL &&
                orientation != LIST_VERTICAL &&
                orientation != GRID_STROKE &&
                orientation != GRID_FILL) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }
 
    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
      switch (mOrientation) {
        case LIST_VERTICAL:
          drawVertical(c, parent);
          break;
        case LIST_HORIZONTAL:
          drawHorizontal(c, parent);
          break;
        case GRID_FILL:
          drawGridFill(c, parent);
          break;
        case GRID_STROKE:
          drawGridStroke(c, parent);
          break;
        default:
          throw new IllegalArgumentException("invalid orientation");
      }
    }
 
    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
 
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
 
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
 
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
 
    public void drawGridStroke(Canvas c, RecyclerView parent) {
      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
          final View child = parent.getChildAt(i);
          final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                  .getLayoutParams();
          final int left = child.getLeft() - params.leftMargin;
          final int right = child.getRight() + params.rightMargin;
          final int top = child.getTop() - params.topMargin;
          final int bottom = child.getBottom() + params.bottomMargin;
          mDivider.setBounds(left, top, left + mDivider.getIntrinsicHeight(), bottom);
          mDivider.draw(c);
          mDivider.setBounds(right - mDivider.getIntrinsicHeight(), top, right , bottom);
          mDivider.draw(c);
          mDivider.setBounds(left, top, right , top + mDivider.getIntrinsicHeight());
          mDivider.draw(c);
          mDivider.setBounds(left, bottom -mDivider.getIntrinsicHeight() , right , bottom);
          mDivider.draw(c);
          mDivider.draw(c);
      }
  }
    public void drawGridFill(Canvas c, RecyclerView parent) {
      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
          final View child = parent.getChildAt(i);
          final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                  .getLayoutParams();
          final int left = child.getLeft() - params.leftMargin;
          final int right = child.getRight() + params.rightMargin;
          final int top = child.getTop() - params.topMargin;
          final int bottom = child.getBottom() + params.bottomMargin;
          mDivider.setBounds(left, top, right, bottom);
          mDivider.draw(c);
      }
  }
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
 
        switch (mOrientation) {
          case LIST_VERTICAL:
          outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            break;
          case LIST_HORIZONTAL:
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            break;
          case GRID_FILL:
            outRect.set(mDivider.getIntrinsicWidth(), mDivider.getIntrinsicWidth(),mDivider.getIntrinsicWidth(),mDivider.getIntrinsicWidth());
            break;
          case GRID_STROKE:
            outRect.set(0, 0,0,0);
            break;
          default:
            throw new IllegalArgumentException("invalid orientation");
        }
    }
}