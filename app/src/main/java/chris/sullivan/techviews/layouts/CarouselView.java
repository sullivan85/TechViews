package chris.sullivan.techviews.layouts;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chris on 2/18/15.
 */
public class CarouselView extends ViewPager {

    private int rotation = 15;
    private long time = 1L;

    public CarouselView(Context context) { this(context, null); }
    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                int width = (int) (getWidth() / 3);
                setClipToPadding(false);
                setClipChildren(false);
                setPadding(width, 0, width, 0);
                setPageMargin(width / 6);
                setOffscreenPageLimit(4);

                if(getChildCount() > 0)
                    setCurrentItem(getChildCount() / 2, true);
            }
        }, 200L);

        setPageTransformer( true, new CarouselTransformer() );
    }

    class CarouselTransformer implements PageTransformer
    {
        @Override
        public void transformPage(View page, float f) {
            // views to the left of center
            if(f < -1)
            {
                page.animate()
                        .rotationY( rotation )
                        .scaleY(0.9f)
                        .scaleX(0.9f)
                        .alpha( 0.8f )
                        .setDuration( time )
                        .start();
            }

            // center-ish
            else if(f <= 0)
            {
                int amount = rotation;
                if(0 > 0.5)
                    amount *= -1;
                page.animate()
                        .rotationY( amount )
                        .scaleY(0.9f)
                        .scaleX(0.9f)
                        .alpha( 0.8f )
                        .setDuration(time)
                        .start();
            }

            // center-ish
            else if(f <= 1)
            {
                long time = (long) (f * page.getWidth());
                page.animate()
                        .rotationYBy(-page.getRotationY())
                        .scaleX(1.5f)
                        .scaleY(1.5f)
                        .alpha(1f)
                        .setDuration(time)
                        .start();
            }

            // views to the right of center
            else if(f > 1)
            {
                page.animate()
                        .rotationY( -rotation )
                        .scaleY(0.9f)
                        .scaleX(0.9f)
                        .alpha( 0.8f )
                        .setDuration(time)
                        .start();
            }
        }
    }

}
