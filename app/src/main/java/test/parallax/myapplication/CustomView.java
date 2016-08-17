package test.parallax.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

public class CustomView extends View {

    private static final int CIRCLES = 6;
    private final float buttonWidthPercent = 2.0f;
    private int viewWidth;
    private int viewHeight;
    private int btnWidth;
    private int btnHeight;
    private Bitmap bitmapSrc;
    private Bitmap recBitmap;

    private Paint linePaint;
    private RectF lineRectF;

    private float xOffset;
    private float yOffset;

    private float rotationAngle = 0;

    private boolean invalidateInProgress;
    private Animation anim;

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(1.5f);

        lineRectF = new RectF();
        Resources res = getResources();
        bitmapSrc = BitmapFactory.decodeResource(res, R.drawable.btn_large);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        viewWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 1);
        viewHeight = (int) (MeasureSpec.getSize(heightMeasureSpec) * 1);

        this.btnWidth = (int) (viewWidth / buttonWidthPercent);
        this.btnHeight = this.btnWidth;

        recBitmap = Bitmap.createScaledBitmap(bitmapSrc, btnWidth, btnHeight, true);

        setMeasuredDimension(viewWidth, viewHeight);
    }

    public void setOffset(float x, float y) {
        xOffset = x;
        yOffset = y;
        if (!invalidateInProgress) {
            invalidate();
        }
    }

    public void increaseRotationAngle() {
        if (rotationAngle == 360) {
            rotationAngle = 0.25f;
        } else {
            this.rotationAngle += 0.25f;
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        invalidateInProgress = true;

        int startX = viewWidth / 2 - btnWidth / 2;
        int startY = viewHeight / 2 - btnHeight / 2;
        canvas.drawBitmap(recBitmap, startX + xOffset * 2, startY + yOffset * 2, new Paint());

        int circleRectHeight = btnHeight + 90;
        float x = getWidth() / 2;
        float y = getHeight() / 2;
        float radius = circleRectHeight / 2f;
        float cX, cY;
        int alpha = 255;

        for (int index = 0; index < CIRCLES; index++) {

            cX = x + xOffset * 2 + xOffset * 2 * index;
            cY = y + yOffset * 2 + yOffset * 2 * index;

            linePaint.setAlpha(alpha);
            lineRectF.set(cX - radius, cY - radius, cX + radius, cY + radius);

            float stepOffset = 360/ 8 * 0.9f;
            float stepEmpty = 360 / 8 * 0.1f;
            int sectors = 360 / 8;
            for (int step = 0; step < 8; step++) {
                float startAngle = step == 0 ? stepEmpty / 2 + rotationAngle : sectors * step + rotationAngle;
                canvas.drawArc(lineRectF, startAngle, stepOffset, false, linePaint);
            }

            circleRectHeight += 90;
            radius = circleRectHeight / 2;
            alpha -= 40;
        }

        invalidateInProgress = false;
    }
}
