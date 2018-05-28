package com.ccr.aricheditor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


import com.ccr.aricheditor.R;

import java.util.ArrayList;

/**
 * @Created on 2018/5/26.
 * @autthor Acheng
 * @Email 345887272@qq.com
 * @Description
 */

public class CustomSeekbar extends View {
    private final String TAG = "CustomSeekbar";
    private int width;
    private int height;
    private int downX = 0;
    private int downY = 0;
    private int upX = 0;
    private int upY = 0;
    private int moveX = 0;
    private int moveY = 0;
    private float scale = 0;
    private int perWidth = 0;
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint buttonPaint;
    private Canvas canvas;
    private Bitmap bitmap;
    private Bitmap thumb;
    private Bitmap spot;
    private Bitmap spot_on;
    private int hotarea = 100;//点击的热区
    private int cur_sections = 2;
    private ResponseOnTouch responseOnTouch;
    private int bitMapHeight = 38;//第一个点的起始位置起始，图片的长宽是76，所以取一半的距离
    private int textMove = 60;//字与下方点的距离，因为字体字体是40px，再加上10的间隔
    private int[] colors = new int[]{0xffdf5600,0x33000000};//进度条的橙色,进度条的灰色,字体的灰色
    private int textSize;
    private int circleRadius;
    private ArrayList<String> section_title;
    public CustomSeekbar(Context context) {
        super(context);
    }
    public CustomSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CustomSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cur_sections = 0;
        bitmap = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);
        thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_0);
        spot = BitmapFactory.decodeResource(getResources(),R.drawable.spot);
        spot_on = BitmapFactory.decodeResource(getResources(),R.drawable.spot_on);
        bitMapHeight = thumb.getHeight()/2;
        textMove = bitMapHeight+22;
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);//锯齿不显示
        mPaint.setStrokeWidth(3);
        mTextPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(0xffb5b5b4);
        buttonPaint = new Paint(Paint.DITHER_FLAG);
        buttonPaint.setAntiAlias(true);
        //initData(null);
    }
    /**
     * 实例化后调用，设置bar的段数和文字
     */
    public void initData(ArrayList<String> section){
        if(section != null){
            section_title = section;
        }else {
            String[] str = new String[]{"低", "中", "高"};
            section_title = new ArrayList<String>();
            for (int i = 0; i < str.length; i++) {
                section_title.add(str[i]);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        width = widthSize;
        float scaleX = widthSize / 1080;
        float scaleY = heightSize / 1920;
        scale = Math.max(scaleX,scaleY);
        //控件的高度
        //        height = 185;
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62, getResources().getDisplayMetrics());
        setMeasuredDimension(width, height);
        width = width-bitMapHeight/2;
        perWidth = (width - section_title.size()*spot.getWidth() - thumb.getWidth()/2) / (section_title.size()-1);
        hotarea = perWidth/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(0);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
        mPaint.setAlpha(255);
        mPaint.setColor(colors[1]);
        canvas.drawLine(bitMapHeight, height * 2 / 3, width - bitMapHeight - spot_on.getWidth() / 2, height * 2 / 3, mPaint);
        int section = 0;
        while(section < section_title.size()){
            if(section < cur_sections) {
                mPaint.setColor(colors[0]);
                canvas.drawLine(thumb.getWidth()/2 + section * perWidth + (section+1) * spot_on.getWidth(),height * 2 / 3,
                        thumb.getWidth()/2 + section * perWidth + (section+1) * spot_on.getWidth() + perWidth,height * 2 / 3,mPaint);
                canvas.drawBitmap(spot_on, thumb.getWidth()/2 + section * perWidth + section * spot_on.getWidth(),height * 2 / 3 - spot_on.getHeight()/2,mPaint);
            }else{
                mPaint.setAlpha(255);
                if(section == section_title.size()-1){
                    canvas.drawBitmap(spot,  width - spot_on.getWidth() - bitMapHeight/2, height * 2 / 3 - spot.getHeight() / 2, mPaint);
                }else {
                    canvas.drawBitmap(spot, thumb.getWidth()/2 + section * perWidth + section * spot_on.getWidth(), height * 2 / 3 - spot.getHeight() / 2, mPaint);
                }
            }

            if(section == section_title.size()-1) {
                canvas.drawText(section_title.get(section), width - spot_on.getWidth()- bitMapHeight/4 - textSize / 2, height * 2 / 3 - textMove, mTextPaint);
            }else{
                canvas.drawText(section_title.get(section), thumb.getWidth()/2 + section * perWidth + section * spot_on.getWidth(), height * 2 / 3 - textMove, mTextPaint);
            }
            section++;
        }
        if(cur_sections == section_title.size()-1){
            canvas.drawBitmap(thumb, width - spot_on.getWidth() - bitMapHeight/2 - thumb.getWidth() / 2,
                    height * 2 / 3 - bitMapHeight, buttonPaint);
        }else {
            canvas.drawBitmap(thumb, thumb.getWidth()/2 + cur_sections * perWidth + cur_sections * spot_on.getWidth() - thumb.getWidth()/4 ,
                    height * 2 / 3 - bitMapHeight, buttonPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_1);
                downX = (int) event.getX();
                downY = (int) event.getY();
                responseTouch(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_1);
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                responseTouch(moveX, moveY);
                break;
            case MotionEvent.ACTION_UP:
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_0);
                upX = (int) event.getX();
                upY = (int) event.getY();
                responseTouch(upX, upY);
                responseOnTouch.onTouchResponse(cur_sections);
                break;
        }
        return true;
    }
    private void responseTouch(int x, int y){
        if(x <= width-bitMapHeight/2) {
            cur_sections = (x + perWidth / 3) / perWidth;
        }else{
            cur_sections = section_title.size()-1;
        }
        invalidate();
    }

    //设置监听
    public void setResponseOnTouch(ResponseOnTouch response){
        responseOnTouch = response;
    }

    //设置进度
    public void setProgress(int progress){
        cur_sections = progress;
        invalidate();
    }
}