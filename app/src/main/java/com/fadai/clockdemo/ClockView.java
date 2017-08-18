package com.fadai.clockdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/08/17
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */

public class ClockView extends View {

    private final String TAG = getClass().getSimpleName();

    private int mWidth = dp2Px(48), mHeight = dp2Px(48), mCenterX, mCenterY;

    // 默认刻度画笔、指针画笔、文字画笔；
    private Paint mDefaultPaint, mPointerPaint, mTextPaint;

    // 时钟半径、中心点半径、默认刻度长度、默认刻度宽度、特殊刻度长度、特殊刻度宽度、
    // 时指针长度、时钟指针宽度、分钟指针长度、分钟指针宽度、秒钟指针长度、秒钟指针宽度
    private float mRadius, mPointRadius,
            mDefaultScaleLength, mDefaultScaleWidth,
            mParticularlyScaleLength, mParticularlyScaleWidth,
            mHourPointerLength, mHourPointerWidth,
            mMinutePointerLength, mMinutePointerWidth,
            mSecondPointerLength, mSecondPointerWidth;

    // 当前时、分、秒
    private int mH, mM, mS;

    // 时钟颜色、默认刻度颜色、时刻度颜色、时针颜色、分针颜色、秒针颜色
    private int mClockColor, mColorDefaultScale, mColorParticularyScale, mColorHourPointer,
            mColorMinutePointer, mColorSecondPointer;

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 获取自定义属性值
        getAttrs(context, attrs);
        // 初始化
        init();
        // 开启计时
        startTime();
    }

    private void getAttrs(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClockView);

        mClockColor = a.getColor(R.styleable.ClockView_clockColor, Color.BLACK);
        mColorDefaultScale = a.getColor(R.styleable.ClockView_defaultScaleColor, mClockColor);
        mColorParticularyScale = a.getColor(R.styleable.ClockView_particularlyScaleColor, mClockColor);

        mColorHourPointer = a.getColor(R.styleable.ClockView_hourPointerColor, mClockColor);
        mColorMinutePointer = a.getColor(R.styleable.ClockView_minutePointerColor, mClockColor);
        mColorSecondPointer = a.getColor(R.styleable.ClockView_secondPointerColor, mClockColor);
    }

    private void init() {
        mDefaultPaint = new Paint();
        mDefaultPaint.setAntiAlias(true);
        mDefaultPaint.setStyle(Paint.Style.STROKE);

        mPointerPaint = new Paint();
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPointerPaint.setTextSize(14);
        mPointerPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint();
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setStyle(Paint.Style.FILL);
        mPointerPaint.setColor(mClockColor);

    }

    /**
     * 开始计时
     */
    private void startTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    getTime();
                }
            }
        }).start();
    }

    /**
     * 获取当前系统时间
     */
    private void getTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        hour = hour % 12;
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        if (hour != mH || minute != mM || second != mS) {
            setTime(hour, minute, second);
            postInvalidate();
        }
    }

    /**
     * 设置时间
     */
    private void setTime(int h, int m, int s) {
        mH = h;
        mM = m;
        mS = s;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = w / 2;
        mCenterY = h / 2;
        mRadius = (float) (w / 2 * 0.8);

        initClockPointerLength();
    }

    /**
     * 根据控件的大小，初始化时钟刻度的长度和宽度、指针的长度和宽度、时钟中心点的半径
     */
    private void initClockPointerLength() {

        /*
        * 默认时钟刻度长=半径/10;
        * 默认时钟刻度宽=长/6;
        *
        * */
        mDefaultScaleLength = mRadius / 10;
        mDefaultScaleWidth = mDefaultScaleLength / 6;

        /*
        * 特殊时钟刻度长=半径/5;
        * 特殊时钟刻度宽=长/6;
        *
        * */
        mParticularlyScaleLength = mRadius / 5;
        mParticularlyScaleWidth = mParticularlyScaleLength / 6;

        /*
        * 时针长=半径/3;
        * 时针宽=特殊时钟刻度宽;
        *
        * */
        mHourPointerLength = mRadius / 3;
        mHourPointerWidth = mParticularlyScaleWidth;

         /*
        * 分针长=半径/2;
        * 分针宽=特殊时钟刻度宽;
        *
        * */
        mMinutePointerLength = mRadius / 2;
        mMinutePointerWidth = mParticularlyScaleWidth;

        /*
        * 秒针长=半径/3*2;
        * 秒针宽=默认时钟刻度宽;
        *
        * */
        mSecondPointerLength = mRadius / 3 * 2;
        mSecondPointerWidth = mDefaultScaleWidth;

        // 中心点半径=（默认刻度宽+特殊刻度宽）/2
        mPointRadius = (mDefaultScaleWidth + mParticularlyScaleWidth) / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureSize(true, widthMeasureSpec);
        int height = getMeasureSize(false, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 获取View尺寸
     * 基本上算是标准写法
     *
     * @param isWidth 是否是width，不是的话，是height
     */
    private int getMeasureSize(boolean isWidth, int measureSpec) {

        int result = 0;

        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                if (isWidth) {
                    result = getSuggestedMinimumWidth();
                } else {
                    result = getSuggestedMinimumHeight();
                }
                break;
            case MeasureSpec.AT_MOST:
                if (isWidth)
                    result = Math.min(specSize, mWidth);
                else
                    result = Math.min(specSize, mHeight);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 坐标原点移动到View 中心
        canvas.translate(mCenterX, mCenterY);
        drawCircle(canvas);
        drawText(canvas);
        drawPointer(canvas);
    }

    /**
     * 绘制时钟的圆形和刻度
     */
    private void drawCircle(Canvas canvas) {

        mDefaultPaint.setStrokeWidth(mDefaultScaleWidth);
        mDefaultPaint.setColor(mClockColor);

        canvas.drawCircle(0, 0, mRadius, mDefaultPaint);

        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) { // 特殊时刻

                mDefaultPaint.setStrokeWidth(mParticularlyScaleWidth);
                mDefaultPaint.setColor(mColorParticularyScale);

                canvas.drawLine(0, -mRadius, 0, -mRadius + mParticularlyScaleLength, mDefaultPaint);

            } else {          // 一般时刻

                mDefaultPaint.setStrokeWidth(mDefaultScaleWidth);
                mDefaultPaint.setColor(mColorDefaultScale);

                canvas.drawLine(0, -mRadius, 0, -mRadius + mDefaultScaleLength, mDefaultPaint);

            }
            canvas.rotate(6);
        }
    }

    /**
     * 绘制特殊时刻（12点、3点、6点、9点)的文字
     */
    private void drawText(Canvas canvas) {

        setTextPaint();

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

        // 文字顶部与基线距离
        float ascent = Math.abs(fontMetrics.ascent);
        // 文字底部与基线距离
        float descent = Math.abs(fontMetrics.descent);
        // 文字高度
        float fontHeight = ascent + descent;
        // 文字竖直中心点距离基线的距离；
        float offsetY = fontHeight / 2 - Math.abs(fontMetrics.descent);
        // 文字宽度
        float fontWidth;

        // drawText(@NonNull String text, float x, float y, @NonNull Paint paint) 参数：y，为基线的y坐标，并非文字左下角的坐标
        // 文字距离圆圈的距离为 特殊刻度长度+宽度

        String h = "12";
        // y轴坐标为: -（半径-特殊刻度长度-特殊刻度宽度（作为间距）-文字顶部距离基线的距离）
        float y = -(mRadius - mParticularlyScaleLength - mParticularlyScaleWidth - ascent);
        canvas.drawText(h, 0, y, mTextPaint);

        h = "3";
        fontWidth = mTextPaint.measureText(h);
        // y轴坐标为: 半径-特殊刻度长度-特殊刻度宽度（作为间距）-文字长度/2（绘制原点在文字横向中心）
        y = mRadius - mParticularlyScaleLength - mParticularlyScaleWidth - (fontWidth / 2);
        canvas.drawText(h, y, 0 + offsetY, mTextPaint);

        h = "6";
        // y轴坐标为: 半径-特殊刻度长度-特殊刻度宽度（作为间距）-文字底部与基线的距离
        y = mRadius - mParticularlyScaleLength - mParticularlyScaleWidth - descent;
        canvas.drawText(h, 0, y, mTextPaint);

        h = "9";
        fontWidth = mTextPaint.measureText(h);
        // y轴坐标为: -（半径-特殊刻度长度-特殊刻度宽度（作为间距）-文字长度/2（绘制原点在文字横向中心））
        y = -(mRadius - mParticularlyScaleLength - mParticularlyScaleWidth - (fontWidth / 2));
        canvas.drawText(h, y, 0 + offsetY, mTextPaint);
    }

    private void setTextPaint() {
        mTextPaint.setStrokeWidth(mDefaultScaleWidth / 2);
        mTextPaint.setTextSize(mParticularlyScaleWidth * 4);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 绘制指针
     */
    private void drawPointer(Canvas canvas) {

        drawHourPointer(canvas);
        drawMinutePointer(canvas);
        drawSecondPointer(canvas);

        mPointerPaint.setColor(mClockColor);
        // 绘制中心原点，需要在指针绘制完成后才能绘制
        canvas.drawCircle(0, 0, mPointRadius, mPointerPaint);
    }

    /**
     * 绘制时针
     */
    private void drawHourPointer(Canvas canvas) {

        mPointerPaint.setStrokeWidth(mHourPointerWidth);
        mPointerPaint.setColor(mColorHourPointer);

        // 当前时间的总秒数
        float s = mH * 60 * 60 + mM * 60 + mS;
        // 百分比
        float percentage = s / (12 * 60 * 60);
        // 通过角度计算弧度值，因为时钟的角度起线是y轴负方向，而View角度的起线是x轴正方向，所以要加270度
        float angle = 270 + 360 * percentage;

        float x = (float) (mHourPointerLength * Math.cos(Math.PI * 2 / 360 * angle));
        float y = (float) (mHourPointerLength * Math.sin(Math.PI * 2 / 360 * angle));

        canvas.drawLine(0, 0, x, y, mPointerPaint);
    }

    /**
     * 绘制分针
     */
    private void drawMinutePointer(Canvas canvas) {

        mPointerPaint.setStrokeWidth(mMinutePointerWidth);
        mPointerPaint.setColor(mColorMinutePointer);

        float s = mM * 60 + mS;
        float percentage = s / (60 * 60);
        float angle = 270 + 360 * percentage;

        float x = (float) (mMinutePointerLength * Math.cos(Math.PI * 2 / 360 * angle));
        float y = (float) (mMinutePointerLength * Math.sin(Math.PI * 2 / 360 * angle));

        canvas.drawLine(0, 0, x, y, mPointerPaint);
    }

    /**
     * 绘制秒针
     */
    private void drawSecondPointer(Canvas canvas) {

        mPointerPaint.setStrokeWidth(mSecondPointerWidth);
        mPointerPaint.setColor(mColorSecondPointer);

        float s = mS;
        float percentage = s / 60;
        float angle = 270 + 360 * percentage;

        float x = (float) (mSecondPointerLength * Math.cos(Math.PI * 2 / 360 * angle));
        float y = (float) (mSecondPointerLength * Math.sin(Math.PI * 2 / 360 * angle));

        canvas.drawLine(0, 0, x, y, mPointerPaint);
    }

    private int dp2Px(int dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
