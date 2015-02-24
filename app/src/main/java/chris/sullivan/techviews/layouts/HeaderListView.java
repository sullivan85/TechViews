package chris.sullivan.techviews.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import chris.sullivan.techviews.R;

/**
 * Created by chris on 2/21/15.
 */
public class HeaderListView extends LinearLayout {

    private View header;
    private int HEADER_ID = 55555555;

    private BaseAdapter adapter;
    private LinearLayout ll;

    public HeaderListView(Context context) { this(context, null); }
    public HeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        loadAttributes(attrs);

        getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                invalidate();
            }
        });
    }

    private void loadAttributes(AttributeSet attrs)
    {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.TechViews);
        setHeader(arr.getResourceId(R.styleable.TechViews_header_layout, -1));
        arr.recycle();
    }

    public void setHeader(View view)
    {
        this.header = view;

        header.setId( HEADER_ID );
        // set to span its height
        header.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // add it at the beginning
        addView(header, 0);
    }

    public void setHeader(int res)
    {
        if(res == -1) return;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate header
        View hTemp = inflater.inflate(res, null);
        setHeader( hTemp );
    }

    private View getHeader() { return header; }

    public void setAdapter( BaseAdapter adapter )
    {
        this.adapter = adapter;

        initContainer();

        if(adapter != null)
        {
            initContainerChildren();
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                    initContainerChildren();
                }
            });
        }
    }

    private void initContainer()
    {
        if(ll != null )
        {
            ll.removeAllViewsInLayout();
            return;
        }

        ll = new LinearLayout(getContext());
        ll.setOrientation(VERTICAL);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 0.9f );
        ll.setLayoutParams( llp );
        addView(ll);
    }

    public void injectContainerView( View v )
    {
        if( ll == null ) return;
        v.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.addView(v, 0);

        if(listen != null) setOnItemClickListener(listen);
    }

    private void initContainerChildren()
    {
        if(adapter == null) return;

        for(int i = 0; i < adapter.getCount(); i++)
        {
            View v = adapter.getView(i, null, ll);
            v.setLayoutParams(new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.addView(v);
        }

        if(listen != null) setOnItemClickListener(listen);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return child == header ? false : super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if(header != null)
        {
            canvas.save();

            // get the translated position of this view
            Rect r = new Rect();
            getLocalVisibleRect(r);

            // translate to the scrolled amount
            canvas.translate(0, r.top);
            header.draw(canvas);

            // restore any translation we made and draw normally
            canvas.restore();
        }
    }

    private OnItemClickListener listen;

    public interface OnItemClickListener
    {
        void onHeaderClicked( HeaderListView parent );
        void onItemClicked( HeaderListView parent, View child, int pos );
    }

    public void setOnItemClickListener( final OnItemClickListener listen )
    {
        this.listen = listen;
        if(header != null)
        {
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // consume touch on header
                    if(event.getY() < header.getHeight()
                            && listen != null)
                    {
                        listen.onHeaderClicked( HeaderListView.this );
                        return true;
                    }

                    // handle touch normally
                    else
                        return onTouchEvent(event);
                }
            });
        }
        if(ll != null)
        {
            for(int i = 0; i < ll.getChildCount(); i++)
            {
                final int pos = i;
                final View child = ll.getChildAt(i);
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listen != null) listen.onItemClicked(HeaderListView.this, child, pos);
                    }
                });
            }
        }
    }

}
