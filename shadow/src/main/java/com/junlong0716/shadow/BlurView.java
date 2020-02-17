package com.junlong0716.shadow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ImageUtils;

/**
 * @ClassName: BlurView
 * @Description:
 * @Author: LiJunlong
 * @CreateDate: 2020-02-14 15:22
 */
public class BlurView extends View {
    private Paint mRectPaint;
    private Bitmap mResultBitmap;

    public BlurView(Context context) {
        this(context, null);
    }

    public BlurView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }


    public BlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mRectPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(Color.parseColor("#CC000000"));
        mRectPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mResultBitmap = createBitmap(w, h);
        setBackground(new BitmapDrawable(mResultBitmap));
    }

    private Bitmap createBitmap(int w, int h) {
        // 自己生产一个透明度的bitmap
        Rect rect = new Rect(0, 0, w, h);
        Bitmap rectBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rectBitmap);
        canvas.save();
        canvas.drawRect(rect, mRectPaint);
        return handleBitmap(rectBitmap, canvas);
    }

    private Bitmap handleBitmap(Bitmap rectBitmap, Canvas canvas) {
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);

        Bitmap bitmapScale = Bitmap.createBitmap(rectBitmap, 0, 0, rectBitmap.getWidth(), rectBitmap.getHeight(), matrix, true);
        canvas.restore();
        // 图形混合一下
        PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        mRectPaint.setColorFilter(filter);
        canvas.scale(0.5f, 0.5f);
        canvas.drawBitmap(bitmapScale, 0, 0, mRectPaint);

        bitmapScale = FastBlurUtil.doBlur(bitmapScale, 10, true);

        Bitmap resultBitmap = null;
        if (bitmapScale != null) {
            resultBitmap = Bitmap.createScaledBitmap(bitmapScale, rectBitmap.getWidth(), rectBitmap.getHeight(), true);
        }

        return ImageUtils.fastBlur(rectBitmap,1f,25f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
