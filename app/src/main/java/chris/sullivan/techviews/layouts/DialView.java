package chris.sullivan.techviews.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import chris.sullivan.techviews.R;

/**
 * Created by chris on 2/22/15.
 */
public class DialView extends View {

    private final float MAX_VALUE = 270;
    private final int ZONE_START = 135;
    private final int ZONE_END = -135;
    private final int DEFAULT_PADDING = 35;

    private int stroke = 60;
    private int endpoints = stroke / 2;

    private int start = 135, end = 45, arc = 0;
    private int min = 0, max = 100;

    private Paint sp;
    private Paint c;

    private int theta = 135;

    public DialView(Context context) { this(context, null); }
    public DialView(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadAttributeSet(attrs);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                theta = getAngle( (int)ev.getX(), (int) ev.getY() );
                invalidate();
                return true;
            }
        });
    }

    // load paints and such
    private void loadAttributeSet(AttributeSet attrs)
    {
        TypedArray args = getContext().obtainStyledAttributes(attrs, R.styleable.TechViews);
        min = args.getInteger(R.styleable.TechViews_min, 0);
        max = args.getInteger(R.styleable.TechViews_max, 100);
        stroke = args.getInteger(R.styleable.TechViews_stroke_width, 60);
        endpoints = stroke / 2;
        args.recycle();

        sp = new Paint(Paint.ANTI_ALIAS_FLAG);
        sp.setStyle(Paint.Style.STROKE);
        sp.setStrokeWidth( stroke );
        sp.setColor(Color.BLUE);

        c = new Paint(Paint.ANTI_ALIAS_FLAG);
        c.setColor(Color.BLUE);
    }

    // get angle of current touch position from center
    private int getAngle(int x, int y)
    {
        int mx = getWidth() / 2;
        int my = getHeight() / 2;
        return (int)Math.toDegrees(Math.atan2(mx - x, my - y));
    }

    // get the point at specific theta
    private Point pointTheta( int cx, int cy, int theta, int radius )
    {
        return new Point(
                (int)(radius * Math.cos( Math.toRadians( theta ) ) + cx),
                (int)(radius * Math.sin( Math.toRadians( theta ) ) + cy)
        );
    }

    @Override
    protected void dispatchDraw( @NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        // get middle of this view - different than RectF middle
        int mx = getWidth() / 2;
        int my = getHeight() / 2;

        int t = start - theta;
        if( theta <= ZONE_START && theta >= ZONE_END )
        {
            arc = t;
            end = start + arc;

            if(dv != null) {
                float pe = arc / MAX_VALUE;
                int mark = (int) (pe * max + min);

                // in case min is not zero, will have to adjust max accordingly
                if(mark > max) mark = max;
                dv.onChanged(DialView.this, mark);
            }
        }

        // rect of this view with 35 padding
        RectF r = new RectF(
                DEFAULT_PADDING,
                DEFAULT_PADDING,
                canvas.getWidth() - DEFAULT_PADDING,
                canvas.getHeight() - DEFAULT_PADDING
        );

        // draw arc form start to arc
        canvas.drawArc(r, start, arc, false, sp);

        // get the end points
        int radius = (int) (r.width() / 2);
        Point s = pointTheta( mx, my, start, radius);
        Point e = pointTheta( mx, my, end, radius );

        // draw end points
        canvas.drawCircle( s.x, s.y, endpoints, c);
        canvas.drawCircle( e.x, e.y, endpoints, c);
    }

    private OnDialChangedListener dv;

    public interface OnDialChangedListener
    {
        void onChanged( DialView dv, int val );
    }

    public void setOnDialChangedListener( OnDialChangedListener dv )
    {
        this.dv = dv;
    }

}
