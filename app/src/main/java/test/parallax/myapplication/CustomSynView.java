package test.parallax.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CustomSynView extends View {

    private static final float DOUBLE_PI = (float) (Math.PI * 2);
    private static final float FREQUENCY = 12;
    private static final float MIN_AMP = 15;
    //    private static final int CIRCLES = 6;
    private static final int CIRCLES = 6;
    private static final float STEP = 1.0f;
    private static final int TIME_DELTA = 10;

    private Paint linePaint;
    private RectF lineRectF;

    private float[] currAmplCircles = new float[CIRCLES];
    private float[] maxAmplCircles = new float[CIRCLES];
    private float[] deltaAmplCircles = new float[CIRCLES];
    private boolean[] increaseCircles = new boolean[CIRCLES];
    private boolean[] interruptedCircle = new boolean[CIRCLES];

    float[][][] pointsXTime = new float[8][CIRCLES][(int) (DOUBLE_PI / 0.005f)];
    float[][][] pointsYTime = new float[8][CIRCLES][(int) (DOUBLE_PI / 0.005f)];

    public CustomSynView(Context context) {
        super(context);
        init();
    }

    public CustomSynView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSynView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.line));
        linePaint.setStrokeWidth(1.5f);

        lineRectF = new RectF();

        resetValues();

        calculateAllValues();
    }

    private void calculateAllValues() {

        for (int time = 0; time < 8; time++) {

            int circleRectHeight = 505;
            //hardcoded center
            float x = 540;
            float y = 696;
            float radius = circleRectHeight / 2f;
            float angleOffset = 0;
            float angle;
            float angleStep = 0.005f;
            float dx, dy;

            for (int index = 0; index < CIRCLES; index++) {
                angle = angleOffset;
                int pointsSize = (int) (DOUBLE_PI / angleStep);
                for (int i = 0; i < pointsSize; i++) {
                    dx = (float) (x + (radius + Math.sin(angle * FREQUENCY) * currAmplCircles[index]) * Math.cos(angle + angleOffset));
                    dy = (float) (y + (radius + Math.sin(angle * FREQUENCY) * currAmplCircles[index]) * Math.sin(angle + angleOffset));

                    angle += angleStep;
                    pointsXTime[time][index][i] = dx;
                    pointsYTime[time][index][i] = dy;
                }
                circleRectHeight += 120;
                radius = circleRectHeight / 2 + index * 6;
                angleOffset += 0.1f;
            }
            makeAnimationStep();
        }
    }

    public void resetValues() {
        for (int i = 0; i < CIRCLES; i++) {
            currAmplCircles[i] = 0;
            maxAmplCircles[i] = MIN_AMP + i;
            deltaAmplCircles[i] = maxAmplCircles[i] / 4;
            increaseCircles[i] = true;
            interruptedCircle[i] = true;
        }
    }

    public void makeAnimationStep() {
        for (int index = 0; index < CIRCLES; index++) {
            float prevAmp = index > 0 ? currAmplCircles[index - 1] : 0;
            if (currAmplCircles[index] >= maxAmplCircles[index]) {
                increaseCircles[index] = false;
            }
            if (currAmplCircles[index] <= 0 && !increaseCircles[index]) {
                currAmplCircles[index] = 0;
                maxAmplCircles[index] = MIN_AMP + index;
                deltaAmplCircles[index] = maxAmplCircles[index] / 4;
                increaseCircles[index] = true;
            }

            if (increaseCircles[index]) {
//                if (index == 0 ||
//                        prevAmp >= deltaAmplCircles[index - 1] * 4
//                        || !increaseCircles[index - 1]) {
                currAmplCircles[index] += deltaAmplCircles[index];
//                }
            } else {
                currAmplCircles[index] -= deltaAmplCircles[index];
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int viewWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 1);
        int viewHeight = (int) (MeasureSpec.getSize(heightMeasureSpec) * 1);

        setMeasuredDimension(viewWidth, viewHeight);
    }

    int time = 0;
    float angleStep = 0.005f;
    int pointsSize = (int) (DOUBLE_PI / angleStep);

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int index = 0; index < CIRCLES; index++) {
            for (int i = 0; i < pointsSize; i++) {
                canvas.drawPoint(pointsXTime[time][index][i], pointsYTime[time][index][i], linePaint);
            }
        }

        time++;
        if (time == 8) {
            time = 0;
        }
        invalidate();
    }

//
//    @Override
//    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        Log.d("TAG", "invalidate");
//        int circleRectHeight = 505;
//        float x = getWidth() / 2;
//        float y = getHeight() / 2;
//        float radius = circleRectHeight / 2f;
//        float angleOffset = 0;
//        float angle;
//        float angleStep = 0.005f;
//        float dx, dy;
//
//        for (int index = 0; index < CIRCLES; index++) {
//            angle = angleOffset;
//            int pointsSize = (int) (DOUBLE_PI / angleStep);
//
//            for (int i = 0; i < pointsSize; i++) {
//                dx = (float) (x + (radius + Math.sin(angle * FREQUENCY) * currAmplCircles[index]) * Math.cos(angle + angleOffset));
//                dy = (float) (y + (radius + Math.sin(angle * FREQUENCY) * currAmplCircles[index]) * Math.sin(angle + angleOffset));
//
//                angle += angleStep;
//                canvas.drawPoint(dx, dy, linePaint);
//            }
//            circleRectHeight += 120;
//            radius = circleRectHeight / 2 + index * 6;
//            angleOffset += 0.1f;
//        }
//
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            //ignore
//        }
//        makeAnimationStep();
//    }
}
