package com.team7;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

public class CustomSeekBar extends View {

    private FoodItem foodItem;
    private Paint textPaint;
    private float percentage = 100;
    private Path clipPath;
    private boolean isActive = false;
    private Drawable drawable;
    private static final int TEXT_AREA_HEIGHT_DP = 30;  // 글자 영역 높이 (dp)
    private int textAreaHeightPx;  // 글자 영역 높이 (픽셀)

    public float GetPercentage(){
        return percentage;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
        invalidate();
    }
    public FoodItem getFoodItem()
    {
        return foodItem;
    }
    public void setFoodItem(FoodItem item)
    {
        foodItem=item;
    }
    public float getPercentage()
    {
        return percentage;
    }
    public CustomSeekBar(Context context) {
        super(context);
        init(null, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomSeekBar, defStyleAttr, 0);
            drawable = a.getDrawable(R.styleable.CustomSeekBar_iconDrawable);
            a.recycle();
        }

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        clipPath = new Path();
        textAreaHeightPx = (int) (TEXT_AREA_HEIGHT_DP * getResources().getDisplayMetrics().density);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//View의 높이 조절
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int desiredHeight = MeasureSpec.getSize(heightMeasureSpec) + textAreaHeightPx;
        int height = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);

        setMeasuredDimension(width, height);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (drawable != null) {
            drawable.setBounds(0, 0, w, h - textAreaHeightPx);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (drawable == null) return;

        float height = getHeight() - textAreaHeightPx;  // 글자 영역을 제외한 높이
        float clipHeight = height * (percentage / 100);

        clipPath.reset();
        clipPath.addRect(0, height - clipHeight, getWidth(), height, Path.Direction.CW);

        canvas.save();
        canvas.clipPath(clipPath);
        drawable.draw(canvas);
        canvas.restore();  // 캔버스 상태 복원

        float width = getWidth();
        canvas.drawText(String.valueOf((int) percentage) + "%", width / 2, getHeight() - (textAreaHeightPx) + (textPaint.getTextSize()), textPaint);
    }


    private float lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = currentY;
                // 부모에게 이벤트를 넘기지 않도록 설정
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                // 수직으로만 움직였을 때

                percentage = 100 - ((currentY / getHeight()) * 100);
                if (percentage < 0) percentage = 0;
                if (percentage > 100) percentage = 100;
                invalidate();

                lastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 터치가 끝났을 때 부모 뷰에게 다시 이벤트를 넘길 수 있게 하기
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return true;
    }

    public void setIconDrawable(Drawable newDrawable) {
        this.drawable = newDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
        }
        invalidate();
    }

    public void setIconDrawableRes(int resId) {
        setIconDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

}
