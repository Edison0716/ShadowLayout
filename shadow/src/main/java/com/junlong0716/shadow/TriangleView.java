package com.junlong0716.shadow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * @Description:
 * @Author: EdisonLi 35068
 * @CreateDate: 2020/1/16 9:48
 * @Version:
 */
public class TriangleView extends View {

    private Path mSelectedPath;
    private Path mUnSelectPath;
    private Paint mTextRectPaint;
    private Paint mSelectedPathPaint;
    private Paint mUnselectedPathPaint;
    private Paint mTextPaint;
    private RectF mTextRectF;

    // 是否是选中状态
    private boolean mIsSelected = false;

    // 圆角因子
    private static float RADIUS1 = 0.12f;

    // 选中状态 右上角横坐标
    private int mPointControlYX;

    // 背景是否需要圆角
    private boolean mBgHasRightRadius = false;
    private boolean mBgHasLeftRadius = false;

    public TriangleView(Context context) {
        this(context, null);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mSelectedPathPaint = new Paint();
        mSelectedPathPaint.setColor(Color.WHITE);
        mSelectedPathPaint.setAntiAlias(true);

        mUnselectedPathPaint = new Paint();
        mUnselectedPathPaint.setColor(Color.parseColor("#A6000000"));
        mUnselectedPathPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(Utils.sp2px(getContext(), 14));
        mTextPaint.setColor(Color.parseColor("#FFFFFF"));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Align.CENTER);

        mTextRectPaint = new Paint();
        mTextRectPaint.setColor(Color.TRANSPARENT);
        mTextRectPaint.setStyle(Paint.Style.FILL);

        mTextRectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculatePath();
        calculateUnSelectedPath();
    }

    private void calculateUnSelectedPath() {
        mUnSelectPath = new Path();

        // EdisonLi TODO 2020/1/16 10 为偏移量
        // 普通点 1
        int pointAX = 0;
        int pointAY = 10 + (int) (getMeasuredHeight() * RADIUS1);

        mUnSelectPath.moveTo(pointAX, pointAY);

        // 普通点 2
        int pointBX = (int) (getMeasuredHeight() * RADIUS1);
        int pointBY = 10;

        // 控制点 1
        int controlPointAX = 0;
        int controlPointAY = 10;

        if (mBgHasLeftRadius) {
            mUnSelectPath.quadTo(controlPointAX, controlPointAY, pointBX, pointBY);
        } else {
            mUnSelectPath.lineTo(controlPointAX, controlPointAY);
        }

        // 控制点 2
        int controlPointBX = getMeasuredWidth();
        int controlPointBY = 10;

        // 普通点 3
        int pointCX = controlPointBX - pointAY;
        int pointCY = 10;

        mUnSelectPath.lineTo(pointCX, pointCY);

        if (mBgHasRightRadius) {
            mUnSelectPath.quadTo(controlPointBX, controlPointBY, controlPointBX, pointAY);
        } else {
            mUnSelectPath.lineTo(controlPointBX, controlPointBY);
        }

        mUnSelectPath.lineTo(getMeasuredWidth(), getMeasuredHeight());
        mUnSelectPath.lineTo(0, getMeasuredHeight());
        mUnSelectPath.close();
    }

    private void calculatePath() {
        mSelectedPath = new Path();

        // 一共3个控制点 7个普通点
        int pointAX = 0;
        int pointAY = (int) (getMeasuredHeight() * RADIUS1);

        mSelectedPath.moveTo(pointAX, pointAY);

        int pointBX = (int) (getMeasuredHeight() * RADIUS1);
        int pointBY = 0;

        // 左上角控制点
        int pointControlXX = 0;
        int pointControlXY = 0;

        mSelectedPath.quadTo(pointControlXX, pointControlXY, pointBX, pointBY);

        // 右上角控制点
        mPointControlYX = (int) (getMeasuredWidth() * (1 - RADIUS1 - RADIUS1));
        int pointControlYY = 0;

        // 第三个点
        int pointCX = mPointControlYX - pointBX;
        int pointCY = 0;

        mSelectedPath.lineTo(pointCX, pointCY);

        // 右下角控制点
        int pointControlZX = (int) (getMeasuredWidth() * (1 - RADIUS1));
        int pointControlZY = getMeasuredHeight();

        // 第四个个点
        int longEdge = (int) Math
                .sqrt(pointControlZY * pointControlZY + ((pointControlZX - mPointControlYX) * (pointControlZX
                        - mPointControlYX)));
        int pointDX = mPointControlYX + ((pointControlZX - mPointControlYX) * pointBX / longEdge);
        int pointDY = pointControlZY * pointBX / longEdge;

        mSelectedPath.quadTo(mPointControlYX, pointControlYY, pointDX, pointDY);

        // 第五个点 计算
        int pointEX = mPointControlYX + ((pointControlZX - mPointControlYX) * (longEdge - pointBX) / longEdge);
        int pointEY = pointControlZY - (pointControlZY * pointBX / longEdge);

        // 第六个点
        int pointFX = getMeasuredWidth();
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
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (mIsSelected) {
            mTextPaint.setColor(Color.parseColor("#333333"));
        } else {
            mTextPaint.setColor(Color.parseColor("#FFFFFF"));
        }

        drawUnselectedView(canvas);

        if (mIsSelected) {
            drawSelectedView(canvas);
        }

        mTextRectPaint.setColor(Color.TRANSPARENT);

        mTextRectF.set(0, 0, mPointControlYX, getMeasuredHeight());

        canvas.drawRect(mTextRectF, mTextRectPaint);

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        String text = "国内租车";
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = mTextRectF.centerY() + distance;
        canvas.drawText(text, mTextRectF.centerX(), baseline, mTextPaint);
    }

    private void drawUnselectedView(Canvas canvas) {
        canvas.drawPath(mUnSelectPath, mUnselectedPathPaint);
    }

    private void drawSelectedView(Canvas canvas) {
        canvas.drawPath(mSelectedPath, mSelectedPathPaint);
    }

    public void setChildViewSelected(boolean b) {
        if (mIsSelected != b) {
            mIsSelected = b;
            invalidate();
        }
    }

    public void setBgHasRightRadius(boolean b) {
        if (mBgHasRightRadius != b) {
            mBgHasRightRadius = b;
            calculateUnSelectedPath();
            invalidate();
        }
    }

    public void setBgHasLeftRadius(boolean b) {
        if (mBgHasLeftRadius != b) {
            calculateUnSelectedPath();
            invalidate();
        }
    }
}
