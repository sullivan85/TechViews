package layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chris.sullivan.techviews.R;

/**
 * Created by chris on 2/18/15.
 */
public class LinkTextView extends TextView {

    // regex for finding hashtag elements
    private final String HASHTAG_REGEX = "#.+?\\b";

    // regex for finding hyper-links
    private final String HTTP_REGEX = "http.+?[^(\\s|\\\")]+";

    // regex for finding twitter-links
    private final String TWITTER_REGEX = "@.+?[^\\s]+";

    // twitter and facebook routing prefixes
    private final String TWITTER_PREFIX = "https://twitter.com/";
    private final String FACEBOOK_PREFIX = "https://facebook.com/";
    private final String GOOGLE_PLUS_PREFIX = "https://plug.google.com/";
    private final String GOOGLE_MAPS = "https://www.google.com/maps/search/";
    private final String TUMBLR_PREFIX = "https://www.tumblr.com/";
    private final String INSTAGRAM_PREFIX = "http://instagram.com/";
    private final String PINTEREST_PREFIX = "https://www.pinterest.com/search/pins/?q=";

    // if hashtag element append twitter/facebook prefixs
    private final String HASHTAG_PREFIX = "hashtag/";

    // if hashtag for tumblr
    private final String TAGGED_PREFIX = "tagged/";

    // if hashtag for google plus
    private final String EXPLORE_PREFIX = "explore/";

    private Pattern pattern;
    private Matcher matcher;

    // default visited link color used by FB AND IE
    private int visited_color = Color.parseColor("#800080");
    private int base_color = Color.BLUE;

    // what type of route this is
    public enum Route
    {
        HASHTAG,
        AT_TAG,
        INTERNET,
        CUSTOM
    }

    public LinkTextView(Context context) { this(context, null); }
    public LinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // load attributes
        loadAttributeSet(attrs);

        // allow this to accept link touching
        setMovementMethod(LinkMovementMethod.getInstance());

        if(getText() != null) setLinkText( getText() + "" );
    }

    private void loadAttributeSet(AttributeSet attrs)
    {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.LinkTextView);

        base_color = array.getColor(R.styleable.LinkTextView_alink, Color.BLUE);
        visited_color = array.getColor(R.styleable.LinkTextView_avisited, Color.parseColor("#800080"));

        array.recycle();
    }

    public void setLinkText( String string ) {
        SpannableString ss = new SpannableString(string);

        // set spans for the hashtags - can use facebook or twitter
        pattern = Pattern.compile(HASHTAG_REGEX);
        matcher = pattern.matcher( string );
        while(matcher.find())
            ss.setSpan(new LinkSpan(Route.HASHTAG, string.substring(matcher.start() + 1, matcher.end())), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // set spans for the twitter feeds @
        pattern = Pattern.compile(TWITTER_REGEX);
        matcher = pattern.matcher( string );
        while(matcher.find())
            ss.setSpan(new LinkSpan(Route.AT_TAG, string.substring(matcher.start() + 1, matcher.end())), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // set normal hyper-links
        pattern = Pattern.compile(HTTP_REGEX);
        matcher = pattern.matcher( string );
        while(matcher.find())
            ss.setSpan(new LinkSpan(Route.INTERNET, string.substring(matcher.start(), matcher.end())), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(ss);
    }

    public void setCustomClickListener( int start, int end, OnClickListener listener )
    {
        SpannableString ss = new SpannableString( getText() );
        ss.setSpan(new LinkSpan( listener ), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(ss);
    }

    public class LinkSpan extends ClickableSpan
    {

        private Route route;
        private String word;

        // change the color after visiting
        private boolean visited = false;

        // for custom clicking
        private OnClickListener listener;

        public LinkSpan( Route route )
        {
            super();
            this.route = route;
        }

        public LinkSpan( Route route, String word )
        {
            super();
            this.route = route;
            this.word = word;
        }

        public LinkSpan( OnClickListener listener )
        {
            this( Route.CUSTOM );
            this.listener = listener;
        }

        @Override
        public void onClick(View widget) {
            // toggle the visited state - to prompt color change
            visited = true;

            final Intent intent = new Intent(Intent.ACTION_VIEW);
            switch(route)
            {
                case HASHTAG:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_singlechoice);
                    adapter.add("View on Facebook");
                    adapter.add("View on Twitter");
                    adapter.add("View on Tumblr");
                    adapter.add("View on Google+");
                    adapter.add("View on Instagram");
                    adapter.add("View on Pinterest");
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which)
                            {
                                case 0:
                                    intent.setData(Uri.parse(FACEBOOK_PREFIX + HASHTAG_PREFIX + word));
                                    break;
                                case 1:
                                    intent.setData(Uri.parse(TWITTER_PREFIX + HASHTAG_PREFIX + word));
                                    break;
                                case 2:
                                    intent.setData(Uri.parse(TUMBLR_PREFIX + TAGGED_PREFIX + word));
                                    break;
                                case 3:
                                    intent.setData(Uri.parse(GOOGLE_PLUS_PREFIX + EXPLORE_PREFIX + word));
                                    break;
                                case 4:
                                    intent.setData(Uri.parse(INSTAGRAM_PREFIX + word));
                                    break;
                                case 5:
                                    intent.setData(Uri.parse(PINTEREST_PREFIX + word));
                                    break;
                            }
                            if(intent != null) getContext().startActivity(intent);
                        }
                    });
                    builder.create().show();
                    break;
                case AT_TAG:
                    AlertDialog.Builder buildit = new AlertDialog.Builder(getContext());
                    final ArrayAdapter<String> atAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_singlechoice);
                    atAdapter.add("View on Twitter");
                    atAdapter.add("View on Google Maps");
                    buildit.setAdapter(atAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which)
                            {
                                case 0:
                                    intent.setData(Uri.parse(TWITTER_PREFIX + word));
                                    break;
                                case 1:
                                    intent.setData(Uri.parse(GOOGLE_MAPS + word.replaceAll(" ", "+")));
                                    break;
                            }
                            if(intent != null) getContext().startActivity(intent);
                        }
                    });
                    buildit.create().show();
                    break;
                case INTERNET:
                    intent.setData(Uri.parse(word));
                    if(intent != null) getContext().startActivity(intent);
                    break;
                case CUSTOM:
                    if( listener != null )
                        listener.onClick(widget);
                    break;
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            // set background of text span to transparent
            ds.bgColor = Color.TRANSPARENT;

            // toggle color based on whether this has been visited or not
            ds.setColor( !visited ? base_color : visited_color );
            ds.setUnderlineText( false );
        }

    }

}
