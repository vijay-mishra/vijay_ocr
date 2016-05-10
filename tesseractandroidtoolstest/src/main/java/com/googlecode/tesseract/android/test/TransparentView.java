package com.googlecode.tesseract.android.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TransparentView extends View {

    int h=0,w=0;

    public TransparentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TransparentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransparentView(Context context,int h,int w) {
        super(context);
        this.h=w;
        this.w=h;
    }
//
//    @Override
//    public void setLayoutParams(ViewGroup.LayoutParams params) {
//        params.height=h;
//        params.width=w;
//        super.setLayoutParams(params);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();



        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);


        canvas.drawLine(0,0,70,0,paint);
        canvas.drawLine(0,0,0,70,paint);
        canvas.drawLine(0,w,0,w-70,paint);
        canvas.drawLine(0,w,70,w,paint);
        canvas.drawLine(h,0,h,70,paint);
        canvas.drawLine(h,0,h-70,0,paint);
        canvas.drawLine(h,w,h-70,w,paint);
        canvas.drawLine(h,w,h,w-70,paint);
//        canvas.rotate(270);

    }

}