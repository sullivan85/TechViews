package chris.sullivan.techviews.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import chris.sullivan.techviews.R;

/**
 * Created by chris on 2/17/15.
 */
public class RadialSelection extends RelativeLayout {

    private float rotate_start = 135;
    private float rotate_end = 45;
    private float rotate_radius = 100;
    private int center_icon = -2;

    private float FLIP = 360;

    public RadialSelection(Context context) { this(context, null); }
    public RadialSelection(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadAttributeSet(attrs);
    }

    private void loadAttributeSet(AttributeSet attrs)
    {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RadialSelection);

        rotate_start = FLIP - array.getFloat(R.styleable.RadialSelection_rotation_start, 135);
        rotate_end = FLIP - array.getFloat(R.styleable.RadialSelection_rotation_end, 45);
        rotate_radius = array.getDimensionPixelSize(R.styleable.RadialSelection_rotation_radius, 100);

        center_icon = array.getResourceId(R.styleable.RadialSelection_center_icon, -2);

        array.recycle();
    }

    private void placeChildren()
    {
        if(getWidth() != 0)
            placeChildren(getWidth(), getHeight());
    }

    private void placeChildren(int width, int height)
    {
        int count = getChildCount();
        // steps need to take maximum space so ignore one of the count
        // minus 2 if a center icon has been added
        int realCount = center_icon == -2 ? count - 1 : count - 2;
        float steps = Math.abs(rotate_start - rotate_end) / realCount;
        float midx = width / 2;
        float midy = height / 2;

        for(int i = 0; i < count; i++)
        {
            int newx = (int) (rotate_radius * Math.cos( Math.toRadians( rotate_start + (steps * i)) ) + midx);
            int newy = (int) (rotate_radius * Math.sin( Math.toRadians( rotate_start + (steps * i)) ) + midy);

            setChildCenter(i, new Point(newx, newy));
        }
    }

    private void setChildCenter(int index, Point pos)
    {
        View child = getChildAt(index);

        // ignore center icon by resourceId
        if(child.getId() == center_icon) return;

        // reassign respective positions minus half width/height
        child.setX(pos.x - (child.getWidth() / 2));
        child.setY(pos.y - (child.getHeight() / 2));
    }

    @Override
    protected void dispatchDraw( @NonNull Canvas canvas) {
        placeChildren(canvas.getWidth(), canvas.getHeight());
        super.dispatchDraw(canvas);
    }
}
