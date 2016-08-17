package test.parallax.myapplication;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    private float[] mLastAcceleration = new float[]{0.0f, 0.0f};
    private float[] mLastTranslation = new float[]{0.0f, 0.0f};

    /**
     * constant use to convert nano second into second
     */
    private static final float NS2S = 1.0f / 1000000000.0f;
    /**
     * boundary minimum to avoid noise
     */
    private static final float TRANSLATION_NOISE = 0.15f;
    /**
     * boundary maximum, over it phone rotates
     */
    private static final float MAXIMUM_ACCELERATION = 3.00f;
    /**
     * duration for translation animation
     */
    private static final int ANIMATION_DURATION_IN_MILLI = 200;
    /**
     * smoothing ratio for Low-Pass filter algorithm
     */
    private static final float LOW_PASS_FILTER_SMOOTHING = 3.0f;
    /**
     * ratio used to determine radius according to ZOrder
     */
    private static final int DEFAULT_RADIUS_RATIO = 12;

    /**
     * remapped axis X according to current device orientation
     */
    private int mRemappedViewAxisX;

    /**
     * remapped axis Y according to current device orientation
     */
    private int mRemappedViewAxisY;

    /**
     * remapped orientation X according to current device orientation
     */
    private int mRemappedViewOrientationX;

    /**
     * remapped orientation Y according to current device orientations
     */
    private int mRemappedViewOrientationY;

    /**
     * use to calculate dT
     */
    private long mTimeStamp = 0;

    private CustomView customView;
    private CustomSynView customSynView;
    private CustomSynView2 customSynView2;
    private CustomSynView3 customSynView3;
    private CustomCircles circles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customView = (CustomView) findViewById(R.id.custom);
        customSynView = (CustomSynView) findViewById(R.id.custom_sync);
        customSynView2 = (CustomSynView2) findViewById(R.id.custom_sync2);
        customSynView3 = (CustomSynView3) findViewById(R.id.custom_sync3);
        circles = (CustomCircles) findViewById(R.id.circles_view);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        final int rotation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        remapAxis(rotation);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        Animation anim = new RotateAnimation(0.0f, 360.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(10000);
        anim.setRepeatMode(Animation.RESTART);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
//        customView.startAnimation(anim);
        startAnimationHandler();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                customView.increaseRotationAngle();
                handler.postDelayed(this, 20);
            }
        }, 20);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void startAnimationHandler() {
//        final int[] time = {0};
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                time[0] += 50;
//                if (time[0] >= 150) {
//                    customSynView2.startAnimation();
//                }
//                if (time[0] >= 300) {
//                    customSynView3.startAnimation();
//                }
//            }
//        }, 50);

//        customSynView.startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customSynView2.startAnimation();
            }
        }, 150);
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        customSynView2.startAnimation();
        try {
            Thread.sleep(150);
//            customSynView3.startAnimation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void remapAxis(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                mRemappedViewAxisX = 0;
                mRemappedViewAxisY = 1;
                mRemappedViewOrientationX = +1;
                mRemappedViewOrientationY = -1;
                break;

            case Surface.ROTATION_90:
                mRemappedViewAxisX = 1;
                mRemappedViewAxisY = 0;
                mRemappedViewOrientationX = -1;
                mRemappedViewOrientationY = -1;
                break;

            case Surface.ROTATION_270:
                mRemappedViewAxisX = 1;
                mRemappedViewAxisY = 0;
                mRemappedViewOrientationX = +1;
                mRemappedViewOrientationY = +1;
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        final float accelerationX = event.values[mRemappedViewAxisX];
        final float accelerationY = event.values[mRemappedViewAxisY];
        float[] translation = new float[]{0.0f, 0.0f};
        if (mTimeStamp != 0) {
            final float dT = (event.timestamp - mTimeStamp) * NS2S;

            if (Math.abs(accelerationX) > MAXIMUM_ACCELERATION) {
                translation[mRemappedViewAxisX] = mLastAcceleration[mRemappedViewAxisX] + 0.5f * MAXIMUM_ACCELERATION * dT * dT;
            } else {
                translation[mRemappedViewAxisX] = mLastAcceleration[mRemappedViewAxisX] + 0.5f * accelerationX * dT * dT;
                mLastAcceleration[mRemappedViewAxisX] = accelerationX;
            }

            if (Math.abs(accelerationY) > MAXIMUM_ACCELERATION) {
                translation[mRemappedViewAxisY] = mLastAcceleration[mRemappedViewAxisY] + 0.5f * MAXIMUM_ACCELERATION * dT * dT;
            } else {
                translation[mRemappedViewAxisY] = mLastAcceleration[mRemappedViewAxisY] + 0.5f * accelerationY * dT * dT;
                mLastAcceleration[mRemappedViewAxisY] = accelerationY;
            }

            /**
             * In order to keep small variations, the noise is dynamic.
             * We normalized translation and noise it by the means of last and new value.
             */
            final float normalizerX = (Math.abs(mLastTranslation[mRemappedViewAxisX]) + Math.abs(translation[mRemappedViewAxisX])) / 2;
            final float normalizerY = (Math.abs(mLastTranslation[mRemappedViewAxisY]) + Math.abs(translation[mRemappedViewAxisY])) / 2;

            final float translationDifX = Math.abs(mLastTranslation[mRemappedViewAxisX] - translation[mRemappedViewAxisX]) / normalizerX;
            final float translationDifY = Math.abs(mLastTranslation[mRemappedViewAxisY] - translation[mRemappedViewAxisY]) / normalizerY;

            final float dynamicNoiseX = TRANSLATION_NOISE / normalizerX;
            final float dynamicNoiseY = TRANSLATION_NOISE / normalizerY;

            float[] newTranslation = null;

            if (translationDifX > dynamicNoiseX && translationDifY > dynamicNoiseY) {
                newTranslation = translation.clone();
            } else if (translationDifX > dynamicNoiseX) {
                newTranslation = new float[2];
                newTranslation[mRemappedViewAxisX] = translation[mRemappedViewAxisX];
                newTranslation[mRemappedViewAxisY] = mLastTranslation[mRemappedViewAxisY];
            } else if (translationDifY > dynamicNoiseY) {
                newTranslation = new float[2];
                newTranslation[mRemappedViewAxisX] = mLastTranslation[mRemappedViewAxisX];
                newTranslation[mRemappedViewAxisY] = translation[mRemappedViewAxisY];
            }

            /**
             * if new translation aren't noise apply Low-Pass filter algorithm and animate parallax
             * items
             */
            if (newTranslation != null) {

                newTranslation[mRemappedViewAxisX] = mLastTranslation[mRemappedViewAxisX] + (newTranslation[mRemappedViewAxisX] - mLastTranslation[mRemappedViewAxisX]) / LOW_PASS_FILTER_SMOOTHING;
                newTranslation[mRemappedViewAxisY] = mLastTranslation[mRemappedViewAxisY] + (newTranslation[mRemappedViewAxisY] - mLastTranslation[mRemappedViewAxisY]) / LOW_PASS_FILTER_SMOOTHING;

                float[] evaluated = evaluator.evaluate(1, mLastTranslation.clone(), newTranslation.clone());
                Log.d("TAG", evaluated[0] + " " + evaluated[1]);

                customView.setOffset(evaluated[0], evaluated[1]);
//                circles.setOffset(evaluated[0], evaluated[1]);
//                customSynView.setOffset(evaluated[0], evaluated[1]);

//                ObjectAnimator.ofObject(this, "CurrentTranslationValues",
//                        new FloatArrayEvaluator(2), 0);

                mLastTranslation[mRemappedViewAxisX] = newTranslation[mRemappedViewAxisX];
                mLastTranslation[mRemappedViewAxisY] = newTranslation[mRemappedViewAxisY];
            }
        }

        mTimeStamp = event.timestamp;
    }

    FloatArrayEvaluator evaluator = new FloatArrayEvaluator(2);

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
