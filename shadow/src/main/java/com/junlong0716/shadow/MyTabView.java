package com.junlong0716.shadow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MyTabView
 * @Description:
 * @Author: LiJunlong
 * @CreateDate: 2020-01-16 20:55
 */
public class MyTabView extends View {
    private final ArrayList<MyTabEntity> mTabData = new ArrayList<>(4);
    private Paint mTabBackgroundPaint;
    private Path mTabBackgroundPath;
    private Path mSelectedPath;
    private Paint mSelectedPathPaint;


    // 计算一个tab要占用多大的宽度
    private float mTabWidth = 0f;
    // 计算一个选中的Tab占用的宽度
    private float mSelectTabWidth = 0f;

    // 圆角因子
    private static float RADIUS_BG = 0.2f;
    private static float RADIUS_SELECTED_BG = 0.1f;


    private int mTabBackgroundShiftY = 10;
    private Paint mRectFPaint;
    private Paint mTextPaint;

    public MyTabView(Context context) {
        this(context, null);
    }

    public MyTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mTabBackgroundPaint = new Paint();
        mTabBackgroundPaint.setColor(Color.parseColor("#A6000000"));
        mTabBackgroundPaint.setAntiAlias(true);

        mSelectedPathPaint = new Paint();
        mSelectedPathPaint.setColor(Color.WHITE);
        mSelectedPathPaint.setAntiAlias(true);

        mRectFPaint = new Paint();
        mRectFPaint.setColor(Color.TRANSPARENT);
        mRectFPaint.setStyle(Paint.Style.FILL);
        mRectFPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(Utils.sp2px(getContext(), 12));
        mTextPaint.setColor(Color.parseColor("#FFFFFF"));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mTabData.isEmpty()) {
            mTabWidth = getMeasuredWidth();
        } else {
            mTabWidth = getMeasuredWidth() / mTabData.size();
        }

        mSelectTabWidth = mTabWidth + mTabWidth / 10;

        calculateBackgroundPath();

        calculateSelectedPath();
    }

    private void calculateRectFArray(Canvas canvas) {
        for (int i = 0; i < mTabData.size(); i++) {
            float rectWidth = i * mTabWidth;
            RectF textRectF = new RectF(rectWidth, mTabBackgroundShiftY, rectWidth + mTabWidth, getMeasuredHeight());
            canvas.drawRect(textRectF, mRectFPaint);
            drawText(canvas, textRectF, mTabData.get(i));
        }
    }

    private void drawText(Canvas canvas, RectF textRectF, MyTabEntity myTabEntity) {
        String text = "国内租车";
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = textRectF.centerY() + distance;
        if (myTabEntity.isTabSelected()) {
            mTextPaint.setColor(Color.BLACK);
        } else {
            mTextPaint.setColor(Color.WHITE);
        }
        canvas.drawText(text, textRectF.centerX(), baseline, mTextPaint);
    }

    private void calculateBackgroundPath() {
        mTabBackgroundPath = new Path();
        // EdisonLi TODO 2020/1/16 10 为偏移量
        // 普通点 1
        int pointAX = 0;
        int pointAY = mTabBackgroundShiftY + (int) (getMeasuredHeight() * RADIUS_BG);
        mTabBackgroundPath.moveTo(pointAX, pointAY);
        // 普通点 2
        int pointBX = (int) (getMeasuredHeight() * RADIUS_BG);
        int pointBY = mTabBackgroundShiftY;
        // 控制点 1
        int controlPointAX = 0;
        int controlPointAY = mTabBackgroundShiftY;
        mTabBackgroundPath.quadTo(controlPointAX, controlPointAY, pointBX, pointBY);
        // 控制点 2
        int controlPointBX = getMeasuredWidth();
        int controlPointBY = mTabBackgroundShiftY;
        // 普通点 3
        int pointCX = controlPointBX - pointAY;
        int pointCY = mTabBackgroundShiftY;
        mTabBackgroundPath.lineTo(pointCX, pointCY);
        mTabBackgroundPath.quadTo(controlPointBX, controlPointBY, controlPointBX, pointAY);
        mTabBackgroundPath.lineTo(getMeasuredWidth(), getMeasuredHeight());
        mTabBackgroundPath.lineTo(0, getMeasuredHeight());
        mTabBackgroundPath.close();
    }


    private void calculateSelectedPath() {
        mSelectedPath = new Path();

        // 一共3个控制点 7个普通点
        int pointAX = 0;
        int pointAY = (int) (mSelectTabWidth * RADIUS_SELECTED_BG);

        mSelectedPath.moveTo(pointAX, pointAY);

        int pointBX = (int) (mSelectTabWidth * RADIUS_SELECTED_BG);
        int pointBY = 0;

        // 左上角控制点
        int pointControlXX = 0;
        int pointControlXY = 0;

        mSelectedPath.quadTo(pointControlXX, pointControlXY, pointBX, pointBY);

        // 右上角控制点
        int pointControlYX = (int) (mSelectTabWidth * (1 - RADIUS_SELECTED_BG - RADIUS_SELECTED_BG));
        int pointControlYY = 0;

        // 第三个点
        int pointCX = pointControlYX - pointBX;
        int pointCY = 0;

        mSelectedPath.lineTo(pointCX, pointCY);

        // 右下角控制点
        int pointControlZX = (int) (mSelectTabWidth * (1 - RADIUS_SELECTED_BG));
        int pointControlZY = getMeasuredHeight();

        // 第四个个点
        int longEdge = (int) Math.sqrt(pointControlZY * pointControlZY + ((pointControlZX - pointControlYX) * (pointControlZX - pointControlYX)));
        int pointDX = pointControlYX + (pointControlZX - pointControlYX) * pointBX / longEdge;
        int pointDY = pointControlZY * pointBX / longEdge;

        mSelectedPath.quadTo(pointControlYX, pointControlYY, pointDX, pointDY);

        // 第五个点 计算
        int pointEX = pointControlYX + ((pointControlZX - pointControlYX) * (longEdge - pointBX) / longEdge);
        int pointEY = pointControlZY - (pointControlZY * pointBX / longEdge);

        // 第六个点
        int pointFX = (int) mSelectTabWidth;
        int pointFY = getMeasuredHeight();

        mSelectedPath.lineTo(pointEX, pointEY);
        mSelectedPath.quadTo(pointControlZX, pointControlZY, pointFX, pointFY);

        // 第七个点
        int pointGX = 0;
        int pointGY = getMeasuredHeight();

        mSelectedPath.lineTo(pointGX, pointGY);
        mSelectedPath.close();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        canvas.drawPath(mTabBackgroundPath, mTabBackgroundPaint);
        // 绘制选中的
        canvas.drawPath(mSelectedPath, mSelectedPathPaint);
        // 用RectF分组
        calculateRectFArray(canvas);
    }

    public void setData(List<MyTabEntity> tabData) {
        mTabData.clear();
        mTabData.addAll(tabData);
        requestLayout();
    }

    public static class MyTabEntity {
        private String tabTitle;
        private boolean tabSelected;

        public MyTabEntity(String tabTitle, boolean tabSelected) {
            this.tabTitle = tabTitle;
            this.tabSelected = tabSelected;
        }

        public String getTabTitle() {
            return tabTitle;
        }

        public void setTabTitle(String tabTitle) {
            this.tabTitle = tabTitle;
        }

        public boolean isTabSelected() {
            return tabSelected;
        }

        public void setTabSelected(boolean tabSelected) {
            this.tabSelected = tabSelected;
        }
    }
}
