package com.junlong0716.shadow;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * Create by EdisonLi
 * 阴影控件
 * 2019/11/05
 */
public class ShadowLayout extends ViewGroup {

    // 设置默认的Gravity
    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
    // 未设置属性
    private static final int SIZE_UNSET = -1;
    // 默认大小
    private static final int SIZE_DEFAULT = 0;
    // 上下文
    private Context mContext;
    // 阴影颜色
    private int mShadowColor;
    // 点击水波纹阴影样式
    private int mPressedColor;
    // 背景颜色
    private int mBackgroundColor;
    // 阴影横向偏移量
    private float mShadowDx;
    // 阴影纵向偏移量
    private float mShadowDy;
    // 阴影辐射大小
    private float mShadowRadius;
    // margin
    private int mShadowMarginLeft = 0;
    private int mShadowMarginTop = 0;
    private int mShadowMarginRight = 0;
    private int mShadowMarginBottom = 0;
    // 圆角
    private float mCornerRadiusTL = 0;
    private float mCornerRadiusTR = 0;
    private float mCornerRadiusBL = 0;
    private float mCornerRadiusBR = 0;
    // 前景图
    private Drawable mForegroundDrawable;

    private int mForegroundDrawGravity = Gravity.FILL;
    private boolean mForegroundDrawBoundsChanged = false;
    private Rect mSelfBounds = new Rect();
    private Rect mOverlayBounds = new Rect();

    // 绘制背景的画笔
    private final Paint mBgPaint = new Paint();

    public ShadowLayout(Context context) {
        this(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs, defStyleAttr);
        updatePaintShadow();
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayout, defStyleAttr, 0);
        mShadowColor = a
                .getColor(R.styleable.ShadowLayout_shadowColor, ContextCompat.getColor(mContext, R.color.colorAccent));
        mPressedColor = a
                .getColor(R.styleable.ShadowLayout_pressedColor, ContextCompat.getColor(mContext, R.color.colorAccent));
        mBackgroundColor = a.getColor(R.styleable.ShadowLayout_backgroundColor, Color.WHITE);
        mShadowDx = a.getFloat(R.styleable.ShadowLayout_shadowDx, 0f);
        mShadowDy = a.getFloat(R.styleable.ShadowLayout_shadowDy, 0f);
        mShadowRadius = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowRadius, SIZE_DEFAULT);

        Log.d("SHADOW_RADIUS",mShadowRadius + "");
        // Android 优先设置系统foreground属性
        Drawable foreground = a.getDrawable(R.styleable.ShadowLayout_android_foreground);
        setForeground(foreground);
        int shadowMargin = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMargin, SIZE_UNSET);
        // 设置 Margin
        if (shadowMargin >= 0) {
            mShadowMarginLeft = shadowMargin;
            mShadowMarginTop = shadowMargin;
            mShadowMarginRight = shadowMargin;
            mShadowMarginBottom = shadowMargin;
        } else {
            mShadowMarginLeft = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginLeft, SIZE_DEFAULT);
            mShadowMarginTop = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginTop, SIZE_DEFAULT);
            mShadowMarginRight = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginRight, SIZE_DEFAULT);
            mShadowMarginBottom = a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginBottom, SIZE_DEFAULT);
        }
        float cornerRadius = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadius, SIZE_UNSET);
        if (cornerRadius >= 0) {
            mCornerRadiusTL = cornerRadius;
            mCornerRadiusTR = cornerRadius;
            mCornerRadiusBL = cornerRadius;
            mCornerRadiusBR = cornerRadius;
        } else {
            mCornerRadiusTL = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusTL, SIZE_DEFAULT);
            mCornerRadiusTR = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusTR, SIZE_DEFAULT);
            mCornerRadiusBL = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusBL, SIZE_DEFAULT);
            mCornerRadiusBR = a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusBR, SIZE_DEFAULT);
        }

        a.recycle();

        // 设置初始画笔颜色值
        mBgPaint.setColor(mBackgroundColor);
        // 抗齿距
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.FILL);
        // 关闭硬件加速 涉及不支持硬件加速的阴影api
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        // 重写 onDraw() 方法
        setWillNotDraw(false);
        // 要重写onDraw()方法设置背景为null
        setBackground(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("onMeasure","onMeasure");
        // ShadowLayout的最大的宽度与高度
        int shadowLayoutMaxHeight = 0;
        int shadowLayoutMaxWidth = 0;
        // 子view的状态
        int childState = 0;

        // 设置原始默认宽高 未指定 就设置大小为0
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        // 判断 ShadowLayout 是不是 Match Parent
        boolean shadowMeasureWidthMatchParent = getLayoutParams().width == LayoutParams.MATCH_PARENT;
        boolean shadowMeasureHeightMatchParent = getLayoutParams().height == LayoutParams.MATCH_PARENT;

        // 先计算测量一下ShadowLayout的宽高
        int widthSpec = widthMeasureSpec;
        if (shadowMeasureWidthMatchParent) {
            // 子view的宽高 = ShadowLayout的宽度 - 两侧的阴影所占的宽度
            int childWidthSize = getMeasuredWidth() - mShadowMarginLeft - mShadowMarginRight;
            widthSpec = MeasureSpec.makeMeasureSpec(childWidthSize,
                    MeasureSpec.EXACTLY);
        }
        int heightSpec = heightMeasureSpec;
        if (shadowMeasureHeightMatchParent) {
            // ShadowLayout的宽高 = ShadowLayout的宽度 - 两侧的阴影所占的宽度
            int childHeightSize = getMeasuredHeight() - mShadowMarginTop - mShadowMarginBottom;
            heightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        }

        if (getChildCount() > 1) {
            throw new IllegalArgumentException("只能设置一个子View哦！");
        }

        View child = getChildAt(0);

        if (child == null) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        // 子view不为空的情况
        if (child.getVisibility() != View.GONE) {
            // 测量子view的宽度 告诉子view最大的可用的宽高 去除ShadowLayout设置的padding && margin
            measureChildWithMargins(child, widthSpec, 0, heightSpec, 0);

            // 获取子view的Margin
            CustomMarginLayoutParams childCustomMarginLayoutParams = (CustomMarginLayoutParams) child.getLayoutParams();
            if (shadowMeasureWidthMatchParent) {
                shadowLayoutMaxWidth = Math.max(shadowLayoutMaxWidth,
                        child.getMeasuredWidth() + childCustomMarginLayoutParams.leftMargin
                                + childCustomMarginLayoutParams.rightMargin);
            } else {
                shadowLayoutMaxWidth = Math.max(shadowLayoutMaxWidth,
                        child.getMeasuredWidth() + childCustomMarginLayoutParams.leftMargin
                                + childCustomMarginLayoutParams.rightMargin
                                + mShadowMarginLeft + mShadowMarginRight);
            }
            if (!shadowMeasureHeightMatchParent) {
                shadowLayoutMaxHeight = Math.max(shadowLayoutMaxHeight,
                        child.getMeasuredHeight() + childCustomMarginLayoutParams.topMargin
                                + childCustomMarginLayoutParams.bottomMargin
                                + mShadowMarginTop + mShadowMarginBottom);
            } else {
                shadowLayoutMaxHeight = Math.max(shadowLayoutMaxHeight,
                        child.getMeasuredHeight() + childCustomMarginLayoutParams.topMargin
                                + childCustomMarginLayoutParams.bottomMargin);
            }
            // 获取当前子View的计算状态
            childState = View.combineMeasuredStates(childState, child.getMeasuredState());
        }

        // 计算 ShadowLayout 自身的 padding
        shadowLayoutMaxHeight += getPaddingTop() + getPaddingBottom();
        shadowLayoutMaxWidth += getPaddingRight() + getPaddingLeft();

        // 计算 加上padding之后的 宽高与 建议最小宽高进行对比
        shadowLayoutMaxHeight = Math.max(shadowLayoutMaxHeight, getSuggestedMinimumHeight());
        shadowLayoutMaxWidth = Math.max(shadowLayoutMaxWidth, getSuggestedMinimumWidth());

        // 计算前景色 6.0 以后才对所有的View设置前景色 之前的版本只有FrameLayout才具备 这里我们就算下
        if (getForeground() != null) {
            shadowLayoutMaxWidth = Math.max(shadowLayoutMaxWidth, getForeground().getMinimumWidth());
            shadowLayoutMaxHeight = Math.max(shadowLayoutMaxHeight, getForeground().getMinimumHeight());
        }

        // 测量ShadowLayout的宽高 根据不同MeasureMode计算一下
        setMeasuredDimension(View.resolveSizeAndState(shadowLayoutMaxWidth,
                shadowMeasureWidthMatchParent ? widthMeasureSpec : widthSpec,
                childState), View.resolveSizeAndState(shadowLayoutMaxHeight, shadowMeasureHeightMatchParent ?
                heightMeasureSpec : heightSpec, childState << View.MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    public Drawable getForeground() {
        return mForegroundDrawable;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mForegroundDrawable != null && mForegroundDrawable.isStateful()) {
            mForegroundDrawable.setState(getDrawableState());
        }
    }

    @Override
    public void setForeground(Drawable foreground) {
        if (mForegroundDrawable != null) {
            mForegroundDrawable.setCallback(null);
            unscheduleDrawable(mForegroundDrawable);
        }
        mForegroundDrawable = foreground;
        updateForegroundColor();
        if (foreground != null) {
            setWillNotDraw(false);
            foreground.setCallback(this);
            if (foreground.isStateful()) {
                foreground.setState(getDrawableState());
            }
            if (mForegroundDrawGravity == Gravity.FILL) {
                Rect padding = new Rect();
                foreground.getPadding(padding);
            }
        }
        requestLayout();
        invalidate();
    }

    private void updateForegroundColor() {
        if (mForegroundDrawable != null) {
            // 5.0 水波纹
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mForegroundDrawable instanceof RippleDrawable) {
                    ((RippleDrawable) mForegroundDrawable).setColor(ColorStateList.valueOf(mPressedColor));
                }
            } else {
                // 使用着色器进行着色
                // 过时
                // mForegroundDrawable.setColorFilter(mPressedColor, PorterDuff.Mode.SRC_ATOP);
                BlendModeColorFilter blendModeColorFilter = new BlendModeColorFilter(mPressedColor, BlendMode.SRC_ATOP);
                mForegroundDrawable.setColorFilter(blendModeColorFilter);
            }
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mForegroundDrawable != null) {
                mForegroundDrawable.setHotspot(x, y);
            }
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (mForegroundDrawable != null) {
            mForegroundDrawable.jumpToCurrentState();
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == mForegroundDrawable;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("onLayout","onLayout");
        layoutChildren(l, t, r, b);
        // 布局发生改动
        if (changed) {
            mForegroundDrawBoundsChanged = true;
        }
    }

    // copy frameLayout onLayout() 计算一下阴影距离即可
    void layoutChildren(int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();
        Log.d("CHILD-COUNT", count + "");
        final View child = getChildAt(0);

        if (child == null) {
            return;
        }

        if (child.getVisibility() != GONE) {
            final CustomMarginLayoutParams lp = (CustomMarginLayoutParams) child.getLayoutParams();

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            int childLeft;
            int childTop;

            int gravity = lp.mGravity;
            if (gravity == -1) {
                gravity = DEFAULT_CHILD_GRAVITY;
            }

            final int layoutDirection = getLayoutDirection();
            final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
                            lp.leftMargin - lp.rightMargin + mShadowMarginLeft - mShadowMarginRight;
                    break;
                case Gravity.RIGHT:
                    childLeft = parentRight - width - lp.rightMargin - mShadowMarginRight;
                    break;
                case Gravity.LEFT:
                default:
                    childLeft = parentLeft + lp.leftMargin + mShadowMarginLeft;
            }

            switch (verticalGravity) {
                case Gravity.CENTER_VERTICAL:
                    childTop = parentTop + (parentBottom - parentTop - height) / 2 +
                            lp.topMargin - lp.bottomMargin + mShadowMarginTop - mShadowMarginBottom;
                    break;
                case Gravity.BOTTOM:
                    childTop = parentBottom - height - lp.bottomMargin - mShadowMarginBottom;
                    break;
                default:
                    childTop = parentTop + lp.topMargin + mShadowMarginTop;
            }

            child.layout(childLeft, childTop, childLeft + width, childTop + height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("onDraw", "onDraw");
        super.onDraw(canvas);
        canvas.save();
        Path path = ShapeUtils.roundedRect(mShadowMarginLeft, mShadowMarginTop, getMeasuredWidth() - mShadowMarginRight, getMeasuredHeight() - mShadowMarginBottom, mCornerRadiusTL, mCornerRadiusTR, mCornerRadiusBL, mCornerRadiusBR);

        drawForeground(canvas);

        canvas.restore();

        // 绘制路径
        canvas.drawPath(path, mBgPaint);
        // 裁剪
        canvas.clipPath(path);
    }

    private void drawForeground(Canvas canvas) {
        if (mForegroundDrawable == null) return;
        if (mForegroundDrawBoundsChanged) {
            mForegroundDrawBoundsChanged = false;
            int width = getRight() - getLeft();
            int height = getBottom() - getTop();
            mSelfBounds.set(0, 0, width, height);
            Gravity.apply(mForegroundDrawGravity, mForegroundDrawable.getIntrinsicWidth(), mForegroundDrawable.getIntrinsicHeight(), mSelfBounds, mOverlayBounds);
            mForegroundDrawable.setBounds(mOverlayBounds);
        }
        mForegroundDrawable.draw(canvas);
    }

    @Override
    public int getForegroundGravity() {
        return mForegroundDrawGravity;

    }

    @Override
    public void setForegroundGravity(int gravity) {
        if (mForegroundDrawGravity != gravity) {
            if ((mForegroundDrawGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                gravity = gravity | Gravity.START;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
                gravity = gravity | Gravity.TOP;
            }
            mForegroundDrawGravity = gravity;
            if (mForegroundDrawGravity == Gravity.FILL && mForegroundDrawable != null) {
                Rect padding = new Rect();
                if (mForegroundDrawable != null) {
                    mForegroundDrawable.getPadding(padding);
                }
            }
            requestLayout();
        }

    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    private void updatePaintShadow() {
        updatePaintShadow(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
    }

    private void updatePaintShadow(float mShadowRadius, float mShadowDx, float mShadowDy, int mShadowColor) {
        mBgPaint.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * 如果不创建一个LP 使用 measureChildWithMargins() 会导致问题：
     * java.lang.ClassCastException: android.view.ViewGroup$CustomMarginLayoutParams cannot be cast to android.view
     * .ViewGroup$MarginLayoutParams
     * https://blog.csdn.net/androiddevelop/article/details/44348753
     */
    @Override
    public CustomMarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CustomMarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new CustomMarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof CustomMarginLayoutParams;
    }

    private float getShadowMaxMargin() {
        int[] shadowMarginArray = new int[4];
        shadowMarginArray[0] = mShadowMarginTop;
        shadowMarginArray[1] = mShadowMarginBottom;
        shadowMarginArray[2] = mShadowMarginLeft;
        shadowMarginArray[3] = mShadowMarginRight;
        int max = shadowMarginArray[0];

        for (int i : shadowMarginArray) {
            if (max < i) {
                max = i;
            }
        }
        return max;
    }

    public void setShadowMargin(int left, int top, int right, int bottom) {
        mShadowMarginLeft = left;
        mShadowMarginRight = right;
        mShadowMarginTop = top;
        mShadowMarginBottom = bottom;
        requestLayout();
        invalidate();
    }

    public void setShadowXY(int shadowDx, int shadowDy) {
        mShadowDx = shadowDx;
        mShadowDy = shadowDy;
        updatePaintShadow();
    }

    public void setShadowDx(int shadowDx) {
        mShadowDx = shadowDx;
        updatePaintShadow();
    }

    public void setShadowDy(int shadowDy) {
        mShadowDy = shadowDy;
        updatePaintShadow();
    }

    public void setShadowRadius(int shadowRadius) {
        if (shadowRadius > getShadowMaxMargin()) {
            mShadowRadius = getShadowMaxMargin();
        } else {
            mShadowRadius = shadowRadius;
        }

        updatePaintShadow();
    }

    public void setShadowColor(@ColorInt int color) {
        mShadowColor = color;
        updatePaintShadow();
    }

    public void setShadowColor(String color) {
        mShadowColor = Color.parseColor(color);
        updatePaintShadow();
    }

    public void setCornerRadius(float radius){
        mCornerRadiusBL = radius;
        mCornerRadiusBR = radius;
        mCornerRadiusTL = radius;
        mCornerRadiusTR = radius;
        invalidate();
    }

    public void setCornerRadius(float tl, float tr, float bl, float br) {
        mCornerRadiusBL = bl;
        mCornerRadiusBR = br;
        mCornerRadiusTL = tl;
        mCornerRadiusTR = tr;
        invalidate();
    }

    /**
     * 复写LP
     */
    class CustomMarginLayoutParams extends MarginLayoutParams {

        private static final int UNSPECIFIED_GRAVITY = -1;
        private int mGravity = UNSPECIFIED_GRAVITY;

        CustomMarginLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
            mGravity = a.getInt(R.styleable.ShadowLayout_layout_gravity, UNSPECIFIED_GRAVITY);
            a.recycle();
        }

        CustomMarginLayoutParams(LayoutParams source) {
            super(source);
        }

        public int getGravity() {
            return mGravity;
        }
    }
}
