LinkTextView is a clickable textview that can handle many different links to other areas, to visiting links on the internet, to more specific hashtag and @ links.

To have LinkTextView build the links use
	setLinkText( String string )

To add custom onClickListeners
	setCustomClickListener( int start, int end, OnClickListener )

	- note, call this after setLinkText so that it isn't overridden in code
	- start and end refer to the text indeces you wish to span

Current @[keyword] handled
	Twitter
	Google Maps

Current Hashtags handled
	Twitter
	Facebook
	Google+
	Tumblr
	Instagram
	Pinterest

