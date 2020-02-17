package com.junlong0716.shadow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
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

    // 三种状态的Tab
    private static final int TAB_TYPE_LEFT = 0xffe1;
    private static final int TAB_TYPE_RIGHT = 0xffe2;
    private static final int TAB_TYPE_OTHER = 0xffe3;

    // tab 数据
    private final ArrayList<MyTabEntity> mTabData = new ArrayList<>(4);
    // 缓存 TAB_TYPE_OTHER 路径 因为每次OnDraw要进行计算 所以缓存
    private final SparseArray<Path> mOtherPathCache = new SparseArray<>(2);

    private Paint mTabBackgroundPaint;
    private Paint mSelectedPathPaint;
    private Paint mRectFTextPaint;
    private Paint mTextPaint;

    private Path mTabBackgroundPath;
    private Path mSelectedLeftPath;
    private Path mSelectedRightPath;


    // 计算一个tab要占用多大的宽度
    private float mTabWidth = 0f;
    // 计算一个选中的Tab占用的宽度
    private float mSelectTabWidth = 0f;
    // 圆角因子
    private static float RADIUS_UNSELECTED_BG = 0.25f;
    private static float RADIUS_SELECTED_BG = 0.1f;
    // 选中的Tab 高出 背景高度大小
    private int mTabBackgroundShiftY = 15;


    @Nullable
    private TabClickCallback mTabClickCallback;

    public MyTabView(Context context) {
        this(context, null);
    }

    public MyTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs, defStyleAttr);
        initView();
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyTabView, defStyleAttr, 0);
        try {
        } finally {
            typedArray.recycle();
        }
    }

    private void initView() {
        mTabBackgroundPaint = new Paint();
        mTabBackgroundPaint.setColor(Color.parseColor("#000000"));
        mTabBackgroundPaint.setAntiAlias(true);
        mTabBackgroundPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.INNER));

        mSelectedPathPaint = new Paint();
        mSelectedPathPaint.setColor(Color.WHITE);
        mSelectedPathPaint.setAntiAlias(true);

        mRectFTextPaint = new Paint();
        mRectFTextPaint.setColor(Color.TRANSPARENT);
        mRectFTextPaint.setStyle(Paint.Style.FILL);
        mRectFTextPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(Utils.sp2px(getContext(), 14));
        mTextPaint.setColor(Color.parseColor("#FFFFFF"));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mTabData.isEmpty()) {
            mTabWidth = getMeasuredWidth();
        } else {
            mTabWidth = getMeasuredWidth() / mTabData.size();
        }

        mOtherPathCache.clear();
        // 设置一个最大的宽度
        int maximumWidth = getMeasuredWidth() / 4;

        if (mTabWidth > maximumWidth) {
            mTabWidth = maximumWidth;

            for (int i = 0; i < 4 - mTabData.size(); i++) {
                MyTabEntity myTabEntity = new MyTabEntity("", false);
                myTabEntity.setSpaceAdd(true);
                mTabData.add(myTabEntity);
            }
        }

        mSelectTabWidth = mTabWidth + mTabWidth / 6;

        // 计算背景路线
        calculateBackgroundPath();

        // 计算选中左侧路线
        calculateSelectedPathLeft();

        // 计算选中右侧
        calculateSelectedPathRight();
    }

    private void calculateRectFArray(Canvas canvas) {
        for (int i = 0; i < mTabData.size(); i++) {
            float rectWidth = i * mTabWidth;
            RectF textRectF = new RectF(rectWidth, mTabBackgroundShiftY, rectWidth + mTabWidth, getMeasuredHeight());
            canvas.drawRect(textRectF, mRectFTextPaint);
            drawText(canvas, textRectF, mTabData.get(i));
        }
    }

    private void drawText(Canvas canvas, RectF textRectF, MyTabEntity myTabEntity) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = textRectF.centerY() + distance;
        if (myTabEntity.isTabSelected()) {
            mTextPaint.setColor(Color.BLACK);
        } else {
            mTextPaint.setColor(Color.WHITE);
        }
        canvas.drawText(myTabEntity.getTabTitle(), textRectF.centerX(), baseline, mTextPaint);
    }

    private void calculateBackgroundPath() {
        mTabBackgroundPath = new Path();
        // EdisonLi 2020/1/16 10 为偏移量
        // 普通点 1
        int pointAX = 0;
        int pointAY = mTabBackgroundShiftY + (int) (getMeasuredHeight() * RADIUS_UNSELECTED_BG);
        mTabBackgroundPath.moveTo(pointAX, pointAY);
        // 普通点 2
        int pointBX = (int) (getMeasuredHeight() * RADIUS_UNSELECTED_BG);
        int pointBY = mTabBackgroundShiftY;
        // 控制点 1
        int controlPointAX = 0;
        int controlPointAY = mTabBackgroundShiftY;
        mTabBackgroundPath.quadTo(controlPointAX, controlPointAY, pointBX, pointBY);
        // 控制点 2
        int controlPointBX = getMeasuredWidth();
        int controlPointBY = mTabBackgroundShiftY;
        // 普通点 3
        int pointCX = controlPointBX - pointBX;
        int pointCY = mTabBackgroundShiftY;
        mTabBackgroundPath.lineTo(pointCX, pointCY);
        mTabBackgroundPath.quadTo(controlPointBX, controlPointBY, controlPointBX, pointAY);
        mTabBackgroundPath.lineTo(getMeasuredWidth(), getMeasuredHeight());
        mTabBackgroundPath.lineTo(0, getMeasuredHeight());
        mTabBackgroundPath.close();
    }

    private void calculateSelectedPathRight() {
        mSelectedRightPath = new Path();
        // 同样 一共3个控制点 7个普通点
        // 第一个点是从右上角开始
        int pointAX = getMeasuredWidth();
        int pointAY = (int) (mSelectTabWidth * RADIUS_SELECTED_BG);

        mSelectedRightPath.moveTo(pointAX, pointAY);

        // 逆时针第二个点
        int pointBX = (int) (pointAX - (mSelectTabWidth * RADIUS_SELECTED_BG));
        int pointBY = 0;

        // 右上角的控制点
        int pointControlXX = pointAX;
        int pointControlXY = pointBY;

        mSelectedRightPath.quadTo(pointControlXX, pointControlXY, pointBX, pointBY);

        // 左上角控制点
        int pointControlYX = (int) (pointAX - (mSelectTabWidth * (1 - RADIUS_SELECTED_BG - RADIUS_SELECTED_BG)));
        int pointControlYY = pointBY;

        // 第三个点
        int pointCX = pointAX - (int) (mSelectTabWidth * (1 - RADIUS_SELECTED_BG - RADIUS_SELECTED_BG)) + pointAY;
        int pointCY = pointBY;

        mSelectedRightPath.lineTo(pointCX, pointCY);

        // 左下角控制点
        int pointControlZX = (int) (pointAX - (mSelectTabWidth * (1 - RADIUS_SELECTED_BG)));
        int pointControlZY = getMeasuredHeight();

        // 第四个个点
        int longEdge = (int) Math
                .sqrt(pointControlZY * pointControlZY + ((pointControlZX - pointControlYX) * (pointControlZX
                        - pointControlYX)));
        int pointDX = pointControlYX - ((pointAX - pointControlZX) - (pointAX - pointControlYX)) * pointAY / longEdge;
        int pointDY = pointControlZY * pointAY / longEdge;

        mSelectedRightPath.quadTo(pointControlYX, pointControlYY, pointDX, pointDY);

        // 第五个点 计算
        int pointEX = pointControlYX - (((pointAX - pointControlZX) - (pointAX - pointControlYX)) * (longEdge - pointAY) / longEdge);
        int pointEY = pointControlZY - (pointControlZY * pointAY / longEdge);

        // 第六个点
        int pointFX = (int) (pointAX - mSelectTabWidth);
        int pointFY = getMeasuredHeight();

        mSelectedRightPath.lineTo(pointEX, pointEY);
        mSelectedRightPath.quadTo(pointControlZX, pointControlZY, pointFX, pointFY);

        // 第七个点
        int pointGX = pointAX;
        int pointGY = getMeasuredHeight();

        mSelectedRightPath.lineTo(pointGX, pointGY);
    }

    private void calculateSelectedPathLeft() {
        mSelectedLeftPath = new Path();
        // 一共3个控制点 7个普通点
        int pointAX = 0;
        int pointAY = (int) (mSelectTabWidth * RADIUS_SELECTED_BG);
        mSelectedLeftPath.moveTo(pointAX, pointAY);
        int pointBX = (int) (mSelectTabWidth * RADIUS_SELECTED_BG);
        int pointBY = 0;

        // 左上角控制点
        int pointControlXX = pointAX;
        int pointControlXY = 0;

        mSelectedLeftPath.quadTo(pointControlXX, pointControlXY, pointBX, pointBY);

        // 右上角控制点
        int pointControlYX = (int) (mSelectTabWidth * (1 - RADIUS_SELECTED_BG - RADIUS_SELECTED_BG));
        int pointControlYY = 0;

        // 第三个点
        int pointCX = pointControlYX - pointBX;
        int pointCY = 0;

        mSelectedLeftPath.lineTo(pointCX, pointCY);

        // 右下角控制点
        int pointControlZX = (int) (mSelectTabWidth * (1 - RADIUS_SELECTED_BG));
        int pointControlZY = getMeasuredHeight();

        // 第四个个点
        int longEdge = (int) Math
                .sqrt(pointControlZY * pointControlZY + ((pointControlZX - pointControlYX) * (pointControlZX
                        - pointControlYX)));

        // 右上角 + （右下角 - 右上角）* 控制点与普通点的距离 / 斜边
        int pointDX = pointControlYX + (pointControlZX - pointControlYX) * pointBX / longEdge;
        //  右下角 * 控制点与普通点的距离 / 斜边
        int pointDY = pointControlZY * pointBX / longEdge;

        mSelectedLeftPath.quadTo(pointControlYX, pointControlYY, pointDX, pointDY);

        // 第五个点 计算
        // 右上角的控制点 + (右下角控制点 - 右上角的控制点) * (斜边 - 控制点与普通点的边长) / 斜边长
        int pointEX = pointControlYX + ((pointControlZX - pointControlYX) * (longEdge - pointBX) / longEdge);
        // 右下角控制点 - (右下角控制点 * 控制点与普通点的边长) / 斜边长
        int pointEY = pointControlZY - (pointControlZY * pointBX / longEdge);

        // 第六个点
        int pointFX = (int) mSelectTabWidth;
        int pointFY = getMeasuredHeight();

        mSelectedLeftPath.lineTo(pointEX, pointEY);
        mSelectedLeftPath.quadTo(pointControlZX, pointControlZY, pointFX, pointFY);

        // 第七个点
        int pointGX = 0;
        int pointGY = getMeasuredHeight();

        mSelectedLeftPath.lineTo(pointGX, pointGY);
        mSelectedLeftPath.close();
    }

    // 计算中间 tab 路径
    private Path calculateSelectedPathOther(int index) {
        // 8个普通点 4个控制点
        Path path = mOtherPathCache.get(index);
        if (path == null) {
            path = new Path();
            mOtherPathCache.put(index, path);
        } else {
            return path;
        }
        // 根据当前是第几个tab  计算X轴起始点
        int distanceLeft = (int) (index * mTabWidth);
        // 顶部两个控制点距离两侧距离
        int top2ControlPointDistanceWith2Sides = (int) (mTabWidth * RADIUS_SELECTED_BG);
        // 控制点 1 左上角
        int pointControlWX = distanceLeft;
        int pointControlWY = 0;
        // 控制点 2 右上角
        int pointControlXX = (int) (distanceLeft + mTabWidth);
        int pointControlXY = 0;
        // 控制点 3 左下角
        int pointControlZX = distanceLeft - top2ControlPointDistanceWith2Sides;
        int pointControlZY = getMeasuredHeight();
        // 控制点 4 右下角
        int pointControlYX = (int) (distanceLeft + mTabWidth + top2ControlPointDistanceWith2Sides);
        int pointControlYY = pointControlZY;
        // 普通点距离控制点的距离
        int commonPointDistanceWithControl = (int) (mTabWidth * RADIUS_SELECTED_BG);
        // 左上角的控制点 开始 顺时针 普通点 1
        int pointAX = pointControlWX + commonPointDistanceWithControl;
        int pointAY = 0;
        // 普通点 2
        int pointBX = pointControlXX - commonPointDistanceWithControl;
        int pointBY = 0;
        path.moveTo(pointAX, pointAY);
        path.lineTo(pointBX, pointBY);
        // 计算右侧斜边
        int longEdge = (int) Math.sqrt(pointControlYY * pointControlYY + ((pointControlYX - pointControlXX) * (pointControlYX - pointControlXX)));
        // 普通点 3
        // 右上角 + （右下角 - 右上角）* 控制点与普通点的距离 / 斜边
        int pointDX = pointControlXX + (pointControlYX - pointControlXX) * commonPointDistanceWithControl / longEdge;
        //  右下角 * 控制点与普通点的距离 / 斜边
        int pointDY = pointControlYY * commonPointDistanceWithControl / longEdge;
        path.quadTo(pointControlXX, pointControlXY, pointDX, pointDY);
        // 普通点 4
        // 右上角的控制点 + (右下角控制点 - 右上角的控制点) * (斜边 - 控制点与普通点的边长) / 斜边长
        int pointCX = pointControlXX + ((pointControlYX - pointControlXX) * (longEdge - commonPointDistanceWithControl) / longEdge);
        // 右下角控制点 - (右下角控制点 * 控制点与普通点的边长) / 斜边长
        int pointCY = pointControlYY - (pointControlYY * commonPointDistanceWithControl / longEdge);
        path.lineTo(pointCX, pointCY);
        // 普通点 5
        // 右下角的控制点 + 控制点与普通点的边长
        int pointEX = pointControlYX + commonPointDistanceWithControl;
        int pointEY = pointControlZY;
        path.quadTo(pointControlYX, pointControlYY, pointEX, pointEY);
        // 普通点 6
        // 左下角控制点 - 控制点与普通点的边长
        int pointFX = pointControlZX - commonPointDistanceWithControl;
        int pointFY = pointEY;
        path.lineTo(pointFX, pointFY);
        // 普通点 7
        // （左上角控制点 - 左下角控制点）^ 2 +height ^ 2 开根号
        int leftLongEdge = (int) Math.sqrt((pointControlWX - pointControlZX) * (pointControlWX - pointControlZX) + getMeasuredHeight() * getMeasuredHeight());
        // 左上角的控制点 - ((左上角的控制点 - 左下角的控制点) * 普通点与控制点之间的距离 / 左斜边长)
        int pointGX = pointControlWX - ((pointControlWX - pointControlZX) * (leftLongEdge - commonPointDistanceWithControl) / leftLongEdge);
        //     (左斜边长 - 普通点与控制点之间的距离) * Height / 左斜边长
        int pointGY = (leftLongEdge - commonPointDistanceWithControl) * getMeasuredHeight() / leftLongEdge;
        path.quadTo(pointControlZX, pointControlZY, pointGX, pointGY);
        // 普通点 8
        // 左上角的控制点 - (左上角的控制点 - 左下角的控制点 * 控制点与普通点的距离 / 边长)
        // 普通点与控制点的距离 * 高度  / 做斜边长
        int pointHX = pointControlWX - ((pointControlWX - pointControlZX) * commonPointDistanceWithControl / leftLongEdge);
        int pointHY = commonPointDistanceWithControl * getMeasuredHeight() / leftLongEdge;
        path.lineTo(pointHX, pointHY);
        path.quadTo(pointControlWX, pointControlWY, pointAX, pointAY);
        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        canvas.drawPath(mTabBackgroundPath, mTabBackgroundPaint);
        // 绘制选中的
        for (int i = 0; i < mTabData.size(); ++i) {
            if (mTabData.get(i).isTabSelected() && !mTabData.get(i).isSpaceAdded()) {
                switch (mTabData.get(i).getTabTyp()) {
                    case TAB_TYPE_LEFT:
                        canvas.drawPath(mSelectedLeftPath, mSelectedPathPaint);
                        break;

                    case TAB_TYPE_RIGHT:
                        canvas.drawPath(mSelectedRightPath, mSelectedPathPaint);
                        break;

                    default:
                        canvas.drawPath(calculateSelectedPathOther(i), mSelectedPathPaint);
                        break;
                }
            }
        }
        // 用RectF分组
        calculateRectFArray(canvas);
    }

    public void setData(List<MyTabEntity> tabData) {
        mTabData.clear();
        mTabData.addAll(tabData);

        for (int i = 0; i < mTabData.size(); i++) {
            if (i == 0) {
                mTabData.get(i).setTabTyp(TAB_TYPE_LEFT);
            } else if (i == mTabData.size() - 1) {
                mTabData.get(i).setTabTyp(TAB_TYPE_RIGHT);
            } else {
                mTabData.get(i).setTabTyp(TAB_TYPE_OTHER);
            }
        }

        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            calculateFingerPressedTab(event.getX());
        }
        return true;
    }

    private void calculateFingerPressedTab(float fingerUpX) {
        int pressedIndex = (int) (fingerUpX / mTabWidth);
        for (int i = 0; i < mTabData.size(); ++i) {
            mTabData.get(i).setTabSelected(false);
            if (i == pressedIndex && !mTabData.get(i).isSpaceAdded()) {
                mTabData.get(i).setTabSelected(true);
                invalidate();
                if (mTabClickCallback != null) {
                    mTabClickCallback.onTabClickCallback(pressedIndex);
                }
            }
        }
    }

    public void setOnTabClickListener(TabClickCallback tabClickListener) {
        mTabClickCallback = tabClickListener;
    }

    public interface TabClickCallback {

        void onTabClickCallback(int pressedIndex);
    }

    public static class MyTabEntity {

        private String tabTitle;
        private boolean tabSelected;
        private int tabTyp;
        private boolean isSpaceAdded = false;

        public MyTabEntity(String tabTitle, boolean tabSelected) {
            this.tabTitle = tabTitle;
            this.tabSelected = tabSelected;
        }

        public MyTabEntity(String tabTitle) {
            this.tabTitle = tabTitle;
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

        public int getTabTyp() {
            return tabTyp;
        }

        public void setTabTyp(int tabTyp) {
            this.tabTyp = tabTyp;
        }

        public boolean isSpaceAdded() {
            return isSpaceAdded;
        }

        public void setSpaceAdd(boolean spaceAdd) {
            isSpaceAdded = spaceAdd;
        }
    }
}
