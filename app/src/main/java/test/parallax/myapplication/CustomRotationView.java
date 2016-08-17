package test.parallax.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CustomRotationView extends View {

    private Paint linePaint;
    private RectF lineRectF;

    private float xOffset;
    private float yOffset;

    private boolean invalidateInProgress;

    public CustomRotationView(Context context) {
        super(context);
        init();
    }

    public CustomRotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRotationView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    }


}
