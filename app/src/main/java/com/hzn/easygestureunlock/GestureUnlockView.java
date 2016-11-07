package com.hzn.easygestureunlock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * 手势解锁控件
 * Created by huzn on 2016/10/13.
 */
public class GestureUnlockView extends View {

    // 行数，默认3
    private int lines;
    // 列数，默认3
    private int columns;
    // 每个圆形的半径，默认25dp
    private int radius;
    // 内部圆形的半径，默认10dp
    private int radiusInside;
    // 圆形正常状态下的颜色，默认Color.BLACK
    private int circleColor;
    // 内部圆形正常状态下的颜色，默认Color.GRAY
    private int circleColorInside;
    // 圆形线条宽度，默认4dp
    private int circleLineWidth;
    // 线条宽度，默认20dp
    private int lineWidth;
    // 线条颜色，默认Color.GRAY
    private int lineColor;
    // 默认状态下的文字
    private String text;
    // 解锁成功时的文字
    private String textSuccess;
    // 解锁失败时的文字
    private String textFailed;
    // 字体大小，默认16sp
    private int textSize;
    // 字体颜色，默认Color.BLACK
    private int textColor;
    // 文字与图形之间的间隔距离，默认55dp
    private int textPadding;
    // 圆形之间的间隔距离，默认30dp
    private int padding;
    // 解锁成功的颜色，默认Color.GREEN
    private int colorSuccess;
    // 解锁失败的颜色，默认Color.RED
    private int colorFailed;

    private Paint circlePaint;
    private Paint circleInsidePaint;
    private Paint linePaint;
    private TextPaint textPaint;
    private Paint.FontMetrics fm;
    private Path fixPath;
    private Path movePath;

    private int widthSize;
    private int heightSize;
    private int textHeight;
    private int contentWidth;
    private int contentHeight;
    private Rect[] rects;

    // 目前处于的状态
    private int curState = STATE_IDLE;
    // 状态：无状态
    private static final int STATE_IDLE = 0;
    // 状态：路径正在计算
    private static final int STATE_START = 1;
    // 状态：路径计算完毕
    private static final int STATE_FINISHED = 2;

    // 目前路径中，定点x,y坐标
    private int fixX;
    private int fixY;
    // 目前路径中，移动x,y坐标
    private int moveX;
    private int moveY;
    // 存储经过的点
    private ArrayList<Integer> pathIndexes;
    // 相对于圆形大小的比例，手指移动时，经过点的触发范围需要缩小
    private static final float RATIO = 0.1f;

    private String curText = "please unlock";

    public GestureUnlockView(Context context) {
        this(context, null);
    }

    public GestureUnlockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureUnlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GestureUnlockView, defStyleAttr, 0);
        lines = a.getInteger(R.styleable.GestureUnlockView_guvLines, 3);
        columns = a.getInteger(R.styleable.GestureUnlockView_guvColumns, 3);
        radius = a.getDimensionPixelSize(R.styleable.GestureUnlockView_guvRadius, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));
        radiusInside = a.getDimensionPixelSize(R.styleable.GestureUnlockView_guvRadiusInside, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        circleColor = a.getColor(R.styleable.GestureUnlockView_guvCircleColor, Color.BLACK);
        circleColorInside = a.getColor(R.styleable.GestureUnlockView_guvCircleColorInside, Color.GRAY);
        circleLineWidth = a.getDimensionPixelOffset(R.styleable.GestureUnlockView_guvCircleLineWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        lineWidth = a.getDimensionPixelOffset(R.styleable.GestureUnlockView_guvLineWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        lineColor = a.getColor(R.styleable.GestureUnlockView_guvLineColor, Color.GRAY);
        text = a.getString(R.styleable.GestureUnlockView_guvText);
        textSuccess = a.getString(R.styleable.GestureUnlockView_guvTextSuccess);
        textFailed = a.getString(R.styleable.GestureUnlockView_guvTextFailed);
        textSize = a.getDimensionPixelSize(R.styleable.GestureUnlockView_guvTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));
        textColor = a.getColor(R.styleable.GestureUnlockView_guvTextColor, Color.BLACK);
        textPadding = a.getDimensionPixelSize(R.styleable.GestureUnlockView_guvTextPadding, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics()));
        padding = a.getDimensionPixelSize(R.styleable.GestureUnlockView_guvPadding, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        colorSuccess = a.getColor(R.styleable.GestureUnlockView_guvColorSuccess, Color.GREEN);
        colorFailed = a.getColor(R.styleable.GestureUnlockView_guvColorFailed, Color.RED);
        a.recycle();

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(circleLineWidth);
        circlePaint.setStyle(Paint.Style.STROKE);
        circleInsidePaint = new Paint();
        circleInsidePaint.setAntiAlias(true);
        circleInsidePaint.setColor(circleColorInside);
        circleInsidePaint.setStyle(Paint.Style.FILL);
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        fm = textPaint.getFontMetrics();
        fixPath = new Path();
        movePath = new Path();

        rects = new Rect[lines * columns];
        for (int i = 0; i < lines * columns; i++)
            rects[i] = new Rect();

        pathIndexes = new ArrayList<>(rects.length);
        curText = text;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            widthSize = circleLineWidth + radius * 2 * columns + padding * (columns - 1) + getPaddingLeft() + getPaddingRight();
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            textHeight = (int) (fm.descent - fm.ascent);
            heightSize = circleLineWidth + radius * 2 * lines + padding * (lines - 1) + textPadding + textHeight + getPaddingTop() + getPaddingBottom();
        }

        // 实际绘制内容的宽和高
        contentWidth = circleLineWidth + radius * 2 * columns + padding * (columns - 1);
        contentHeight = circleLineWidth + radius * 2 * lines + padding * (lines - 1) + textPadding + textHeight;

        // 第一个圆形的中心点x,y坐标
        int firstCenterX = widthSize / 2 - contentWidth / 2 + radius + circleLineWidth / 2;
        int firstCenterY = heightSize / 2 - contentHeight / 2 + textHeight + textPadding + radius + circleLineWidth / 2;

        // 存储每个圆形的坐标
        for (int l = 0; l < lines; l++) {
            for (int c = 0; c < columns; c++) {
                int index = l * columns + c;
                rects[index].left = firstCenterX + c * (radius * 2 + padding) - radius - circleLineWidth / 2;
                rects[index].top = firstCenterY + l * (radius * 2 + padding) - radius - circleLineWidth / 2;
                rects[index].right = firstCenterX + c * (radius * 2 + padding) + radius + circleLineWidth / 2;
                rects[index].bottom = firstCenterY + l * (radius * 2 + padding) + radius + circleLineWidth / 2;
            }
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制圆圈
        for (int i = 0; i < rects.length; i++) {
            canvas.drawCircle(
                    rects[i].centerX(),
                    rects[i].centerY(),
                    radius,
                    circlePaint);
            canvas.drawCircle(
                    rects[i].centerX(),
                    rects[i].centerY(),
                    radiusInside,
                    circleInsidePaint);
        }

        // 绘制文字提示
        if (!TextUtils.isEmpty(curText)) {
            float textWidth = textPaint.measureText(curText);
            canvas.drawText(
                    curText,
                    widthSize / 2 - textWidth / 2,
                    heightSize / 2 - contentHeight / 2 + textHeight / 2 - (fm.ascent + fm.descent),
                    textPaint);
        }

        // 绘制路径
        if (curState == STATE_START) {
            drawFixPath(canvas);
            drawMovePath(canvas);
        } else if (curState == STATE_FINISHED) {
            drawFixPath(canvas);
        }
    }

    // 绘制固定的路径
    private void drawFixPath(Canvas canvas) {
        fixPath.reset();
        Integer index = pathIndexes.get(0);
        Rect r = rects[index];
        fixPath.moveTo(r.centerX(), r.centerY());
        for (int i = 1; i < pathIndexes.size(); i++) {
            r = rects[pathIndexes.get(i)];
            fixPath.lineTo(r.centerX(), r.centerY());
        }
        canvas.drawPath(fixPath, linePaint);
    }

    // 绘制移动中的路径
    private void drawMovePath(Canvas canvas) {
        movePath.reset();
        movePath.moveTo(fixX, fixY);
        movePath.lineTo(moveX, moveY);
        canvas.drawPath(movePath, linePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (curState == STATE_IDLE) {
                    fixX = (int) event.getX();
                    fixY = (int) event.getY();
                    for (int i = 0; i < rects.length; i++) {
                        if (rects[i].contains(fixX, fixY)) {
                            curState = STATE_START;
                            // 第一个点
                            saveNextPoint(i);
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (curState == STATE_START) {
                    moveX = (int) event.getX();
                    moveY = (int) event.getY();
                    for (int i = 0; i < rects.length; i++) {
                        // 已经滑过的点不重复计算
                        if (!pathIndexes.contains(i) && getRatioRect(rects[i]).contains(moveX, moveY)) {
                            // 下一个点
                            saveNextPoint(i);
                            break;
                        }
                    }

                    // 当所有圆形都被穿过时，直接结束
                    if (pathIndexes.size() == rects.length) {
                        curState = STATE_FINISHED;
                        unlockFinished();
                    } else {
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (curState == STATE_START) {
                    curState = STATE_FINISHED;
                    unlockFinished();
                }
                break;
        }

        return curState == STATE_START || super.onTouchEvent(event);
    }

    // 获取按比例缩小后的Rect
    private Rect getRatioRect(Rect r) {
        Rect rr = new Rect();
        int rRadius = (int) (radius * RATIO);
        rr.left = r.left + rRadius;
        rr.top = r.top + rRadius;
        rr.right = r.right - rRadius;
        rr.bottom = r.bottom - rRadius;
        return rr;
    }

    // 存储下一个点，同时更新fixX，fixY
    private void saveNextPoint(int index) {
        pathIndexes.add(index);
        fixX = rects[index].centerX();
        fixY = rects[index].centerY();
    }

    /**
     * 解锁结束
     */
    private void unlockFinished() {
        boolean success = false;
        if (null != onUnlockListener)
            success = onUnlockListener.onUnlockFinished(pathIndexes);

        if (success) {
            linePaint.setColor(colorSuccess);
            textPaint.setColor(colorSuccess);
            curText = textSuccess;
        } else {
            linePaint.setColor(colorFailed);
            textPaint.setColor(colorFailed);
            curText = textFailed;
        }
        invalidate();
    }

    public void reset() {
        curState = STATE_IDLE;
        curText = text;

        linePaint.setColor(lineColor);
        textPaint.setColor(textColor);
        pathIndexes.clear();

        invalidate();
    }

    /**
     * 解锁回调接口，不设置此接口时，默认解锁失败
     */
    public interface OnUnlockListener {
        /**
         * 解锁结束时回调
         *
         * @param password 用户输入的解锁密码
         * @return 若返回true，则显示解锁成功，否则返回false，则显示解锁失败
         */
        public boolean onUnlockFinished(ArrayList<Integer> password);
    }

    private OnUnlockListener onUnlockListener;

    public void setOnUnlockListener(OnUnlockListener onUnlockListener) {
        this.onUnlockListener = onUnlockListener;
    }
}
