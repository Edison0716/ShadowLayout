package com.junlong0716.shadow;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * @ClassName: DynamicHeightViewPager
 * @Description:
 * @Author: LiJunlong
 * @CreateDate: 2020-02-17 14:27
 */
public class DynamicHeightViewPager extends ViewPager {
    private final SparseIntArray mChildHeight = new SparseIntArray(10);

    public DynamicHeightViewPager(@NonNull Context context) {
        this(context, null);
    }

    public DynamicHeightViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int h = (int) (mChildHeight.get(position) + (mChildHeight.get(position + 1) - mChildHeight.get(position)) * positionOffset);
                setDynamicHeight(h);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View currentView = getChildAt(getCurrentItem());

        if (currentView != null) {
            currentView.measure(widthMeasureSpec, heightMeasureSpec);
        }

        if (mChildHeight.size() != getChildCount()) {
            for (int i = 0; i < getChildCount(); ++i) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, heightMeasureSpec);
                mChildHeight.put(i, child.getMeasuredHeight());
            }
        }

        setMeasuredDimension(getMeasuredWidth(), measureHeight(heightMeasureSpec, currentView));
    }

    private int measureHeight(int heightMeasureSpec, View currentView) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            result = heightSize;
        } else {
            if (currentView != null) {
                result = currentView.getMeasuredHeight();
            }

            if (heightMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, heightSize);
            }
        }

        return result;
    }


    public void setDynamicHeight(int height) {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = height;
        this.setLayoutParams(layoutParams);
    }
}
