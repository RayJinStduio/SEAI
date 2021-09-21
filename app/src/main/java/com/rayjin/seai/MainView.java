package com.rayjin.seai;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MainView extends View {
    private int mWidth;
    private int mHeight;

    private int Radius; //圆弧的高度
    private int mBgColor;   //背景颜色
    private int lgColor;    //变化的最终颜色
    private Paint mPaint;  //画笔
    private LinearGradient linearGradient;
    private Rect rect=new Rect(0,0,0,0);//普通的矩形
    private Path path=new Path();//用来绘制曲面

    public MainView(Context context) {
        this(context, null);
    }

    public MainView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcView);
        mBgColor = typedArray.getColor(R.styleable.ArcView_bgColor,getResources().getColor(R.color.purple));
        lgColor = typedArray.getColor(R.styleable.ArcView_lgColor, mBgColor);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Log.d("----","onSizeChanged");
        linearGradient = new LinearGradient(0,0,getMeasuredWidth(),0,
                mBgColor,lgColor, Shader.TileMode.CLAMP
        );
        mPaint.setShader(linearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置成填充
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBgColor);

        //绘制路径
        path.moveTo(Radius,0);
        path.lineTo(mWidth - Radius, 0);
        //在(400, 200, 600, 400)区域内绘制一个300度的圆弧
        RectF rectF = new RectF(mWidth - 2 * Radius, 0, mWidth, Radius * 2);
        path.arcTo(rectF, -90, 90);
        path.lineTo(mWidth, mHeight - 2 * Radius);
        rectF = new RectF(mWidth - 2 * Radius, mHeight - 3 * Radius, mWidth, mHeight - Radius);
        path.arcTo(rectF, 0, 90);
        path.lineTo(Radius, mHeight - Radius);
        rectF = new RectF(0, mHeight -  Radius, 2 * Radius, mHeight + Radius);
        path.arcTo(rectF, 270, -90);
        path.lineTo(0, Radius);
        rectF = new RectF(0, 9, 2 * Radius, 2 * Radius);
        path.arcTo(rectF, 180, 90);
        path.lineTo(Radius, 0);
        path.setLastPoint(Radius, 0);
        canvas.drawPath(path, mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
            Radius = mWidth / 6;
        }
        setMeasuredDimension(mWidth, mHeight);;
    }
}
