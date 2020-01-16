package com.junlong0716.shadow;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Description:
 * @Author: EdisonLi 35068
 * @CreateDate: 2020/1/16 14:58
 * @Version:
 */
public class MyTabLayout extends FrameLayout {

    public MyTabLayout(@NonNull Context context) {
        this(context,null);
    }

    public MyTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        removeAllViews();

        //addView();
    }
}
