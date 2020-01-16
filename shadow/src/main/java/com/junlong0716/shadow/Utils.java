package com.junlong0716.shadow;

import android.content.Context;

/**
 * Created on 2018/6/25.
 * author : HeQing
 * desc : 工具类
 */

public class Utils {

    public static int dp2px(Context context, float dp) {
        if (context == null) {
            return (int) dp;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int sp2px(Context context, float sp) {
        if (context == null) {
            return (int) sp;
        }
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

}
