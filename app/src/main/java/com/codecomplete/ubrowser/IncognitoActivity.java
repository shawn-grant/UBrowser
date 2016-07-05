package com.codecomplete.ubrowser;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.speech.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.webkit.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.widget.TextView.*;
import com.codecomplete.ubrowser.*;
import com.oguzdev.circularfloatingactionmenu.library.*;
import java.net.*;
import java.util.*;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import com.codecomplete.ubrowser.R;
import android.util.*;

public class IncognitoActivity extends Activity implements OnClickListener
  {

	WebView webview;
	AutoCompleteTextView urlbar;
	String goingBack;

	SlidingDrawer slider;
	SharedPreferences savewebdata;
	SharedPreferences.Editor editor;

	String historytxt,bookmarks;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	  {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		savewebdata = getSharedPreferences ("savewebdata", Context.MODE_PRIVATE);

		if (savewebdata.getBoolean ("DarkTheme", false)) {
			setTheme (R.style.DarkTheme);
		  }
		setContentView (R.layout.main);

		editor = savewebdata.edit ();

		historytxt = savewebdata.getString ("history", "");
		bookmarks = savewebdata.getString ("bookmarks", "");
		goingBack = "";

		slider = (SlidingDrawer)findViewById (R.id.mainSlidingDrawer);
		slider.bringToFront ();

		webview = (WebView)findViewById (R.id.mainWebView);
		urlbar = (AutoCompleteTextView)findViewById (R.id.urlbar);

		setUpWebView ();
		setUrlBarControl ();
		addFloatingMenu ();
	  }

	public void setUpWebView()
	  {
		//Allowing access to web icons
		WebIconDatabase.getInstance ().open (getDir ("icons", MODE_PRIVATE).getPath ());
		//Make sure No cookies are created 
		android.webkit.CookieManager.getInstance ().setAcceptCookie (false); 

		webview.getSettings ().setJavaScriptEnabled (savewebdata.getBoolean ("Javascript", true));
		webview.getSettings ().setLoadWithOverviewMode (savewebdata.getBoolean ("Overview", true));
		webview.getSettings ().setUseWideViewPort (true);
		webview.getSettings ().setJavaScriptCanOpenWindowsAutomatically (true);
		webview.getSettings ().setBuiltInZoomControls (true);
		webview.getSettings ().setDisplayZoomControls (false);

		//Make sure no caching is done 
		webview.getSettings ().setCacheMode (webview.getSettings ().LOAD_NO_CACHE); 
		webview.getSettings ().setAppCacheEnabled (false);
		webview.clearHistory (); 
		webview.clearCache (true); 

		//Make sure no autofill for Forms/ user-name password happens for the app 
		webview.clearFormData ();
		webview.getSettings ().setSavePassword (false); 
		webview.getSettings ().setSaveFormData (false); 


		if (savewebdata.getBoolean ("Plugins", true)) {
			webview.getSettings ().setPluginState (WebSettings.PluginState.ON);
		  }
		webview.setWebViewClient (new mywebclient ());
		webview.setWebChromeClient (new MyWebChromeClient ());

		webview.loadUrl ("file:///android_asset/incognito.html");
	  }

	private void setUrlBarControl()
	  {
		// TODO: Implement this method
		final String[] list=historytxt.split ("\n");
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_dropdown_item_1line, list);
		urlbar.setAdapter (adapter);
		urlbar.setOnItemClickListener (new OnItemClickListener (){

			  @Override
			  public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
				  // TODO: Implement this method
				  String new_url=list [p3].split (" 》 ") [1];
				  Search (new_url);
				}
			});

		//search on enter pressed
		urlbar.setOnEditorActionListener (new OnEditorActionListener (){
			  @Override
			  public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
				{
				  if (p2 == EditorInfo.IME_ACTION_DONE || p2 == EditorInfo.IME_ACTION_NEXT) { 
					  String webpage=urlbar.getText ().toString ();
					  Search (webpage);
					}
				  return true;
				}
			});
	  }

	public void addFloatingMenu()
	  {
		ImageView menub=new ImageView (this);
		ImageView backIcon = new ImageView (this); 
		ImageView forwardIcon = new ImageView (this);
		forwardIcon.setRotation (180);
		ImageView refreshIcon = new ImageView (this);
		ImageView voiceIcon = new ImageView (this);

		menub.setImageResource (R.drawable.button_action);
		backIcon.setImageResource (R.drawable.back);
		forwardIcon.setImageResource (R.drawable.back);
		refreshIcon.setImageResource (R.drawable.reload);
		voiceIcon.setImageResource (R.drawable.voice);

		FloatingActionButton actionButton = new FloatingActionButton.Builder (this) .
		  setContentView (menub) .build ();

		SubActionButton backButton = new SubActionButton.Builder (this).setContentView (backIcon).build ();
		SubActionButton forwardButton = new SubActionButton.Builder (this).setContentView (forwardIcon).build ();
		SubActionButton refreshButton = new SubActionButton.Builder (this).setContentView (refreshIcon).build ();
		SubActionButton voiceButton = new SubActionButton.Builder (this).setContentView (voiceIcon).build ();

		backButton.setId (1);
		forwardButton.setId (2);
		refreshButton.setId (3);
		voiceButton.setId (4);

		backButton.setOnClickListener (this);
		forwardButton.setOnClickListener (this);
		refreshButton.setOnClickListener (this);
		voiceButton.setOnClickListener (this);

		new FloatingActionMenu.Builder (this) .
		  addSubActionView (backButton, 60, 60) .
		  addSubActionView (forwardButton, 60, 60).
		  addSubActionView (refreshButton, 60, 60).
		  addSubActionView (voiceButton, 60, 60).
		  attachTo (actionButton) .build ();
	  }

	public void Search(String webpage)
	  {

		if (URLUtil.isValidUrl (webpage)) {
			webview.loadUrl (webpage);
		  }
		else {
			webview.loadUrl ("http://www.google.com/search?sclient=tablet-gws&safe=active&site=&source=hp&q=" + webpage + "&oq=" + webpage + "&gs_l=tablet-gws.3..0i131j0l2.18370.26152.0.27669.7.7.0.0.0.0.473.1455.2-1j1j2.4.0..3..0...1c.1.64.tablet-gws..3.4.1453.5pQH1AWhaxo");
		  }

		slider.animateClose ();
		Toast.makeText (getApplicationContext (), "...loading...", 
						Toast.LENGTH_LONG).show ();
	  }

	@Override
	public void onClick(View p1)
	  {
		// TODO: Implement this method
		switch (p1.getId ()) {
			case 1:
			  webview.goBack ();
			  slider.animateClose ();
			  Toast.makeText (getApplicationContext (), "...going back...", 
							  Toast.LENGTH_LONG).show ();
			  goingBack = "true";
			  break;

			case 2:
			  webview.goForward ();
			  slider.animateClose ();
			  Toast.makeText (getApplicationContext (), "...going forward...", 
							  Toast.LENGTH_LONG).show ();
			  goingBack = "false";
			  break;

			case 3:
			  webview.reload ();
			  slider.animateClose ();
			  Toast.makeText (getApplicationContext (), "...reloading...", 
							  Toast.LENGTH_LONG).show ();
			  break;

			case 4:
			  Intent i= new Intent (RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			  i.putExtra (RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			  i.putExtra (RecognizerIntent.EXTRA_PROMPT, "what are you searching for?");
			  startActivityForResult (i, 1000);
			  break;

			case R.id.stop:
			  webview.stopLoading ();
			  slider.animateClose ();
			  Toast.makeText (getApplicationContext (), "...stopping...", 
							  Toast.LENGTH_LONG).show ();
			  break;
		  }
	  }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	  { 
		if (resultCode == RESULT_OK) {
			if (requestCode == 1000) { 
				ArrayList<String> matches = data.getStringArrayListExtra (RecognizerIntent.EXTRA_RESULTS);
				String words=matches.get (0);
				Search (words);
			  }
		  }
		super.onActivityResult (requestCode, resultCode, data);
	  }

	public class mywebclient extends WebViewClient
	  {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		  {
			// TODO: Implement this method
			webview.setVisibility (View.VISIBLE);
			findViewById (R.id.stop).setVisibility (View.VISIBLE);

			super.onPageStarted (view, url, favicon);	
		  }			

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		  {
			// TODO: Implement this method
			view.loadUrl (url);

			if (url.contains (".mp3")) {
				Intent intent = new Intent (Intent.ACTION_VIEW); 
				intent.setDataAndType (Uri.parse (url), "audio/*"); 
				startActivity (Intent.createChooser (intent, "Open Using...")); 
				return true; 
			  } 
			else if (url.contains (".mp4") || url.contains (".3gp")) { 
				Intent intent = new Intent (Intent.ACTION_VIEW); 
				intent.setDataAndType (Uri.parse (url), "video/*"); 
				startActivity (Intent.createChooser (intent, "Open Using..."));
				return true; 
			  } 
			else if (url.contains ("youtube.com")) {
				startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse (url)));
				return true;
			  }

			if ("about:blank".equals (url) && view.getTag () != null) {
				view.loadUrl (view.getTag ().toString ()); 
			  }
			else { 
				view.setTag (url); 
			  }

			return true;
		  }

		@Override
		public void onPageFinished(WebView view, String url)
		  {
			// TODO: Implement this method
			findViewById (R.id.stop).setVisibility (View.GONE);
			urlbar.setText (url);

			if ("about:blank".equals (url) && goingBack.equals ("true")) {
				view.goBack ();
				goingBack = "";
			  }
			else if ("about:blank".equals (url) && goingBack.equals ("false")) {
				view.goForward ();
				goingBack = "";
			  }

			super.onPageFinished (view, url);
		  }

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		  {
			// TODO: Implement this method
			new AlertDialog.Builder (IncognitoActivity.this).setTitle ("Error: " + errorCode).
			  setMessage (description).show ();
			super.onReceivedError (view, errorCode, description, failingUrl);
		  }

	  }

	public class MyWebChromeClient extends WebChromeClient
	  { 
		//Handle javascript alerts: 
		@Override 
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
		  {
			try {
				new AlertDialog.Builder (IncognitoActivity.this).
				  setTitle (new URL (view.getUrl ()).getHost () + " says:").
				  setMessage (message).
				  setPositiveButton ("OK", new DialogInterface.OnClickListener (){
					  @Override
					  public void onClick(DialogInterface p1, int p2)
						{
						  // TODO: Implement this method
						  result.confirm (); 
						}
					}).
				  show ();
			  }
			catch (MalformedURLException e) {e.printStackTrace ();}
			return true; 
		  }

		@Override
		public void onReceivedIcon(WebView view, Bitmap picon)
		  {
			// TODO: Implement this method
			Drawable ic=new BitmapDrawable (getResources (), picon);
			getActionBar ().setIcon (ic);
			super.onReceivedIcon (view, picon);
		  }

		@Override
		public void onReceivedTitle(WebView view, String ptitle)
		  {
			// TODO: Implement this method
			getActionBar ().setTitle (ptitle);
			super.onReceivedTitle (view, ptitle);
		  }

		@Override
		public void onProgressChanged(WebView view, int newProgress)
		  {
			// TODO: Implement this method
			if (newProgress != 100) {
				getActionBar ().setSubtitle ("Loading: " + newProgress + "%");
			  }
			else {
				getActionBar ().setSubtitle (null);
			  }
			super.onProgressChanged (view, newProgress);
		  }
	  }

	public boolean onKeyDown(int keyCode, KeyEvent event)
	  {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webview.canGoBack ()) {
				webview.goBack ();
				goingBack = "true";
			  }
			else {finish ();}
			return true;
		  }

		return super.onKeyDown (keyCode, event);
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	  {
		// TODO: Implement this method
		getMenuInflater ().inflate (R.menu.incognito_menu, menu);
		return super.onCreateOptionsMenu (menu);
	  }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	  {
		// TODO: Implement this method
		switch (item.getItemId ()) {
			case R.id.close:
			  finish ();
			  break;

			case R.id.bookmark:
			  Bookmark ();
			  break;

			case R.id.history:			
			  LoadHistory ();
			  break;

			case R.id.viewbookarks:
			  LoadBookmarks ();
			  break;

			case R.id.settings:		
			  startActivity (new Intent ("android.intent.action.SETTINGS"));
			  break;

			case R.id.about:		
			  new AlertDialog.Builder (this).
				setTitle ("ABOUT").
				setMessage ("Developer: Shawn Grant(shawn-grant.github.io)" +
							"\nCortechx,inc" +
							"\nDeveloped: November 28,2015" +
							"\nEmail: shawngrant333@gmail.com").
				show ();
			  break;

			case R.id.ABhide:
			  if (getActionBar ().isShowing ()) {
				  getActionBar ().hide ();
				}
			  else {
				  getActionBar ().show ();
				}
			  break;
		  }
		return super.onMenuItemSelected (featureId, item);
	  }

	public void Bookmark()
	  {
		final String url=webview.getUrl ();
		final EditText bookmarkname=new EditText (this);
		bookmarkname.setText (webview.getTitle ());

		new AlertDialog.Builder (this).
		  setTitle ("Add Bookmark").
		  setMessage ("name this bookmark:").
		  setView (bookmarkname).
		  setPositiveButton ("Add", new DialogInterface.OnClickListener (){

			  @Override
			  public void onClick(DialogInterface p1, int p2)
				{
				  // TODO: Implement this method
				  if (bookmarkname.getText ().toString ().isEmpty () == false)
					bookmarks += bookmarkname.getText ().toString () + " 》 " + url + "\n";
				  else
					bookmarks += webview.getTitle () + " 》 " + url + "\n";

				  editor.putString ("bookmarks", bookmarks);
				  editor.commit ();
				}
			}).setNegativeButton ("Cancel", new DialogInterface.OnClickListener (){

			  @Override
			  public void onClick(DialogInterface p1, int p2)
				{
				  // TODO: Implement this method
				  p1.dismiss ();
				}
			}).
		  show ();
	  }

	public void LoadBookmarks()
	  {
		String[] list=bookmarks.split ("\n");
		final Dialog bookmark=new Dialog (this);
		bookmark.setTitle ("Bookmarks");
		bookmark.setContentView (R.layout.bookmarks);
		LinearLayout bookmarkll=(LinearLayout) bookmark.findViewById (R.id.bookmarksLayout);

		for (int i=0;list.length > i;i++) {
			final String item=list [i];
			if (item.equals ("")) {}
			else {
				String [] parts=item.split (" 》 ");
				String title=parts [0];
				final String url=parts [1];

				ImageView iv=new ImageView (this);
				TextView tv=new TextView (this);

				iv.setImageResource (R.drawable.icon);
				tv.setTextColor (Color.BLUE);
				tv.setTextSize (30);
				tv.setText (title);

				LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,
																			LinearLayout.LayoutParams.FILL_PARENT);
				lp.setMargins (0, 0, 0, 10);
				LinearLayout ll=new LinearLayout (this);
				ll.setOrientation (LinearLayout.VERTICAL);
				ll.setBackgroundColor (Color.BLACK);
				ll.setLayoutParams (lp);
				ll.addView (iv, 100, 100);
				ll.addView (tv, new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				bookmarkll.addView (ll);

				ll.setOnClickListener (new OnClickListener (){

					  @Override
					  public void onClick(View p1)
						{
						  // TODO: Implement this method
						  webview.loadUrl (url);
						  bookmark.dismiss ();
						}
					});

				ll.setOnLongClickListener (new OnLongClickListener (){

					  @Override
					  public boolean onLongClick(View p1)
						{
						  // TODO: Implement this method
						  new AlertDialog.Builder (IncognitoActivity.this).
							setTitle ("Remove From Bookmarks").
							setMessage ("Do you want to remove this item?").
							setPositiveButton ("Remove", new DialogInterface.OnClickListener (){

								@Override
								public void onClick(DialogInterface p1, int p2)
								  {
									// TODO: Implement this method
									bookmarks = bookmarks.replace (item, "");
									editor.putString ("bookmarks", bookmarks);
									editor.commit ();
									bookmark.dismiss ();
									LoadBookmarks ();
								  }
							  }).
							setNegativeButton ("Remove All", new DialogInterface.OnClickListener (){

								@Override
								public void onClick(DialogInterface p1, int p2)
								  {
									// TODO: Implement this method
									bookmarks = "";
									editor.putString ("bookmarks", bookmarks);
									editor.commit ();
									bookmark.dismiss ();
									LoadBookmarks ();
								  }
							  }).show ();
						  return true;
						}
					});
			  }
		  }

		bookmark.show ();
	  }

	private void LoadHistory()
	  {
		// TODO: Implement this method
		final AlertDialog ad=new AlertDialog.Builder (this).create ();
	    ListView lv=new ListView (this);

		final String[] list=historytxt.split ("\n");
		final ArrayList<String> titleArray=new ArrayList<String> ();
		final ArrayList<String> urlArray=new ArrayList<String> ();

		for (int i=0;i < list.length;i++) {
			String item=list [i];
			if (item.equals ("")) {} 
			else {
				String title=item.split (" 》 ") [0];
				String url=item.split (" 》 ") [1];
				titleArray.add (title);
				urlArray.add (url);
			  }
		  }
		final ArrayAdapter<String> aa=new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, titleArray);
		lv.setAdapter (aa);
		lv.setOnItemClickListener (new OnItemClickListener (){

			  @Override
			  public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
				  // TODO: Implement this method

				  String i=urlArray.get (p3);
				  webview.loadUrl (i);
				  ad.dismiss ();
				}
			});
		lv.setOnItemLongClickListener (new OnItemLongClickListener (){

			  @Override
			  public boolean onItemLongClick(final AdapterView<?> p1a, View p2, final int p3, long p4)
				{
				  // TODO: Implement this method
				  final String i=titleArray.get (p3) + " 》 " + urlArray.get (p3);

				  new AlertDialog.Builder (IncognitoActivity.this).
					setTitle ("Remove From History").
					setMessage ("Do you want to remove this item?").
					setPositiveButton ("Remove", new DialogInterface.OnClickListener (){

						@Override
						public void onClick(DialogInterface p1, int p2)
						  {
							// TODO: Implement this method
							historytxt = historytxt.replace (i, "");
							editor.putString ("history", historytxt);
							editor.commit ();
							ad.dismiss ();
							LoadHistory ();
						  }
					  }).
					setNegativeButton ("Remove All", new DialogInterface.OnClickListener (){

						@Override
						public void onClick(DialogInterface p1, int p2)
						  {
							// TODO: Implement this method
							historytxt = "";
							editor.putString ("history", historytxt);
							editor.commit ();
							ad.dismiss ();
							LoadHistory ();
						  }
					  }).show ();
				  return true;
				}
			});

		ad.setTitle ("History");
		ad.setView (lv);
		ad.show ();
	  }
  }
