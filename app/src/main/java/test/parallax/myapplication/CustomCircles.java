package test.parallax.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class CustomCircles extends View {

    private static final int CIRCLES = 6;
    private final float buttonWidthPercent = 2.0f;
    private Paint bgBtnPaint;
    private Paint linePaint;

    private int viewWidth;
    private int viewHeight;
    private int btnWidth;
    private int btnHeight;
    private Bitmap bitmapSrc;
    private Bitmap recBitmap;

    private float xOffset;
    private float yOffset;
    private boolean invalidateInProgress;

    public CustomCircles(Context context) {
        super(context);
        init();
    }

    public CustomCircles(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCircles(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomCircles(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        bgBtnPaint = new Paint();
        bgBtnPaint.setStyle(Paint.Style.FILL);
        bgBtnPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.line));
        linePaint.setStrokeWidth(1.5f);

        Resources res = getResources();
        bitmapSrc = BitmapFactory.decodeResource(res, R.drawable.btn_large);
    }

    public void setOffset(float x, float y) {
        xOffset = x;
        yOffset = y;
        if (!invalidateInProgress) {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        this.viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        this.btnWidth = (int) (viewWidth / buttonWidthPercent);
        this.btnHeight = this.btnWidth;

        recBitmap = Bitmap.createScaledBitmap(bitmapSrc, btnWidth, btnHeight, true);

        setMeasuredDimension(viewWidth, viewHeight);
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

        RectF lineRectF = new RectF();
        for (int index = 0; index < CIRCLES; index++) {

            linePaint.setAlpha(alpha);
            cX = x + xOffset * 2 + xOffset * 2 * index;
            cY = y + yOffset * 2 + yOffset * 2 * index;

            lineRectF.set(cX - radius, cY - radius, cX + radius, cY + radius);
            canvas.drawArc(lineRectF, 0, 360, false, linePaint);
            circleRectHeight += 90;
            radius = circleRectHeight / 2 + index * 6;
            alpha -= 40;
        }

        invalidateInProgress = false;
    }
}
