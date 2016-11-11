package com.codecomplete.ubrowser;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.print.*;
import android.speech.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.webkit.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.widget.TextView.*;
import com.codecomplete.ubrowser.*;
import com.oguzdev.circularfloatingactionmenu.library.*;
import com.squareup.picasso.*;
import java.io.*;
import java.net.*;
import java.util.*;

import android.view.View.OnClickListener;
import com.codecomplete.ubrowser.R;

public class IncognitoActivity extends Activity implements OnClickListener
  {

	WebView webview;
	AutoCompleteTextView urlbar;
	String goingBack;

	SlidingDrawer slider;
	SharedPreferences savewebdata;
	SharedPreferences.Editor editor;
	
	AlertDialog.Builder log;
	String logtxt;

	private int ID_SAVEIMAGE=1000;
	private int ID_VIEWIMAGE=2000;
	private int ID_SET_AS_BG=3000;
	private int ID_SAVELINK=4000;
	private int ID_SHARELINK=5000;
	private int ID_OPENLINK=6000;	

	private int REQUEST_CODE_RESULTS=100;
	

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
		goingBack = "";

		slider = (SlidingDrawer)findViewById (R.id.mainSlidingDrawer);
		slider.bringToFront ();

		webview = (WebView)findViewById (R.id.mainWebView);
		urlbar = (AutoCompleteTextView)findViewById (R.id.urlbar);
		
		log = new AlertDialog.Builder (this);
		log.setTitle ("CONSOLE LOG")
		  .setPositiveButton ("Clear", new DialogInterface.OnClickListener (){

			  @Override
			  public void onClick(DialogInterface p1, int p2)
				{
				  // TODO: Implement this method
				  logtxt = "";
				  log.setView (new TextView (IncognitoActivity.this));
				}
			}).setNegativeButton ("Cancel", null);

		setUpWebView ();
		setUrlBarControl ();
		addFloatingMenu ();
		//alert if history will be saved
		if(savewebdata.getBoolean("incogHistory",false)){
			Toast.makeText(this, "NB :YOUR INCOGITO HISTORY WILL BE SAVED.\nChange in settings.", 4000).show();
		  }
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
		registerForContextMenu(webview);

		webview.loadUrl ("file:///android_asset/incognito.html");
	  }

	private void setUrlBarControl()
	  {
		// TODO: Implement this method
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
		Toast.makeText (getApplicationContext (), "...loading...", 
						Toast.LENGTH_LONG).show ();
	  }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	  { 
		super.onCreateContextMenu (menu, v, menuInfo); 
		final WebView.HitTestResult result = webview.getHitTestResult ();

		MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener () { 
			public boolean onMenuItemClick(MenuItem item) 
			  { 
				// do the menu action
				switch (item.getItemId ()) {
					  //SAVE IMAGE
					case 1000:
					  SaveImage (result.getExtra ());
					  break;

					  //VIEW IMAGE
					case 2000:
					  webview.loadUrl (result.getExtra ());
					  break;

					  //SET IMAGE AS BACKGROUND
					case 3000:
					  WallpaperManager wallpaperManager = WallpaperManager.getInstance (getApplicationContext ());
		      		  try {
						  InputStream is = (InputStream) new URL (result.getExtra ()).getContent ();
						  Drawable drawable= Drawable.createFromStream (is, "wallpaper_image");
						  Bitmap bitmap = Bitmap.createBitmap (drawable.getIntrinsicWidth (), drawable.getIntrinsicHeight (), Bitmap.Config.ARGB_8888); 
						  Canvas canvas = new Canvas (bitmap); 
						  drawable.setBounds (0, 0, drawable.getIntrinsicWidth (), drawable.getIntrinsicHeight ()); 
						  drawable.draw (canvas);
						  wallpaperManager.setBitmap (bitmap);
						  Toast.makeText (getApplicationContext (), "Wallpaper set", 2000).show ();
						}
					  catch (IOException e) {
						  e.printStackTrace ();
						  Toast.makeText (getApplicationContext (), "Set Wallpaper failed", 2000).show ();
						}
					  break;

					  //SAVE LINK
					case 4000:
					  android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService (CLIPBOARD_SERVICE); 
					  ClipData clip = ClipData.newPlainText (result.getExtra (), result.getExtra ()); 
					  clipboard.setPrimaryClip (clip);
					  Toast.makeText (IncognitoActivity.this, "Copied to ClipBoard", 2000).show ();
					  break;

					  //SHARE LINK
					case 5000:
					  Intent intent = new Intent (Intent.ACTION_SEND); 
					  intent.setType ("text/plain"); 
					  intent.putExtra (Intent.EXTRA_TEXT, result.getExtra ()); 
					  intent.putExtra (Intent.EXTRA_SUBJECT, "Check out this site!");
					  startActivity (Intent.createChooser (intent, "Share Via..."));
					  break;

					  //OPEN LINK
					case 6000:
					  webview.loadUrl (result.getExtra ());
					  break;
				  }
			    return true; 
			  } 
		  }; 

		//if image
		if (result.getType () == WebView.HitTestResult.IMAGE_TYPE || result.getType () == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
			// Menu options for an image.
			menu.setHeaderTitle (result.getExtra ());
			menu.add (0, ID_SAVEIMAGE, 0, "Save Image").setOnMenuItemClickListener (handler); 
			menu.add (0, ID_VIEWIMAGE, 0, "View Image").setOnMenuItemClickListener (handler);
			menu.add (0, ID_SAVELINK, 0, "Copy Image URL").setOnMenuItemClickListener (handler);
			menu.add (0, ID_SHARELINK, 0, "Share Image Url").setOnMenuItemClickListener (handler);
			menu.add (0, ID_SET_AS_BG, 0, "Set as Wallpaper").setOnMenuItemClickListener (handler);
		  }
		//if hyperlink
		else if (result.getType () == WebView.HitTestResult.ANCHOR_TYPE || result.getType () == WebView.HitTestResult.SRC_ANCHOR_TYPE) { 
			// Menu options for a hyperlink.
			menu.setHeaderTitle (result.getExtra ()); 
			menu.add (0, ID_SAVELINK, 0, "Save Link").setOnMenuItemClickListener (handler); 
			menu.add (0, ID_SHARELINK, 0, "Share Link").setOnMenuItemClickListener (handler);
			menu.add (0, ID_OPENLINK, 0, "Open").setOnMenuItemClickListener (handler);
		  } 
	  }
	  
	@Override
	public void onClick(View p1)
	  {
		// TODO: Implement this method
		switch (p1.getId ()) {
			case 1:
			  webview.goBack ();
			  goingBack = "true";
			  break;

			case 2:
			  webview.goForward ();
			  goingBack = "false";
			  break;

			case 3:
			  webview.reload ();
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
			  Toast.makeText (getApplicationContext (), "...stopping...", 
							  Toast.LENGTH_LONG).show ();
			  break;
			  
			case R.id.info:
			  try {
				  new AlertDialog.Builder (this).
					setTitle (new URL (webview.getUrl ()).getHost ()).
					setMessage ("Url: " + webview.getUrl () +
								"\nTitle: " + webview.getTitle () +
								"\nUri Scheme: " + new URL (webview.getUrl ()).getProtocol()).show();
				}
			  catch (MalformedURLException e) {}
			  break;
		  }
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
		  
		  case R.id.Isincognito:
			new AlertDialog.Builder(this).
			setTitle("Incognito Mode").setMessage("You are incognito\nDo you want to exit this mode?").
				setPositiveButton ("Yes", new DialogInterface.OnClickListener (){

					@Override
					public void onClick(DialogInterface p1, int p2)
					  {
						// TODO: Implement this method
						finish();
					  }
				  }).show ();
			break;
			
			case R.id.bookmark:
			  Bookmark ();
			  break;

			case R.id.code:
			  startActivity (new Intent ("android.intent.action.EDITOR"));
			  break;

			case R.id.history:			
			  Intent historyIntent = new Intent("android.intent.action.GET_RESULT");
			  historyIntent.putExtra("type", "history");
			  startActivityForResult(historyIntent, REQUEST_CODE_RESULTS);
			  break;

			case R.id.viewbookarks:
			  Intent bookmarksIntent = new Intent("android.intent.action.GET_RESULT");
			  bookmarksIntent.putExtra("type", "bookmarks");
			  startActivityForResult(bookmarksIntent, REQUEST_CODE_RESULTS);
			  break;

			case R.id.quickload:			
			  SpeedDial ();
			  break;

			case R.id.print:			
			  printWebDoc ();
			  break;
			  
			case R.id.shortcut:
			  addShortcut();
			  break;

			case R.id.viewsource:
			  GetHtml html = new GetHtml();
			  String content = html.getHtml(webview.getUrl());
			  Intent i=new Intent ("android.intent.action.VIEW_SOURCE");
			  i.putExtra ("html", content);
			  startActivity (i);
			  break;

			case R.id.log:
			  TextView tv=new TextView (this);
			  tv.setText (logtxt, TextView.BufferType.SPANNABLE);
			  log.setView (tv);
			  log.show ();
			  break;

			case R.id.savehtml:
			  SaveHtml ();
			  break;

			case R.id.share:
			  Intent intent = new Intent (Intent.ACTION_SEND); 
			  intent.setType ("text/plain"); 
			  intent.putExtra (Intent.EXTRA_TEXT, webview.getUrl ()); 
			  intent.putExtra (Intent.EXTRA_SUBJECT, "Check out this site!");
			  startActivity (Intent.createChooser (intent, "Share Via..."));
			  break;

		    case R.id.savepage:
			  SavePage ();
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
				  String bookmark;
				  if (!bookmarkname.getText ().toString ().isEmpty ())
					bookmark = bookmarkname.getText ().toString () + " 》 " + url;
				  else
					bookmark = getActionBar().getTitle () + " 》 " + url;

				  try {
					  File file = new File (getFilesDir () + "/bookmarks.dat");
					  if(!file.exists())file.createNewFile();

					  BufferedWriter bufferedWriter = new BufferedWriter (new FileWriter (file, true));
					  bufferedWriter.write ("\n" + bookmark);
					  bufferedWriter.close ();
					}
				  catch (IOException e) {
					  e.printStackTrace ();
					}
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

	private void SpeedDial()
	  {
		// TODO: Implement this method
		final AlertDialog ad=new AlertDialog.Builder (this).create ();
		ArrayList<String> a=new ArrayList<String> ();
		a.add ("http://cortechx.github.io");
		a.add ("http://google.com");
		a.add ("http://facebook.com");
		a.add ("http://youtube.com");
		a.add ("http://yahoo.com");
		a.add ("http://gmail.com");
		a.add ("http://jutc.com");
		a.add ("http://twitter.com");
		a.add ("http://microsoft.com");
		a.add ("http://omegarenovation.ca");
		a.add ("http://translate.google.com");
		a.add ("http://developer.android.com");

		ListView l=new ListView (this);
		final ArrayAdapter<String> aa = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, a);
		l.setAdapter (aa);
		l.setOnItemClickListener (new OnItemClickListener (){

			  @Override
			  public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
				  // TODO: Implement this method
				  webview.loadUrl (aa.getItem (p3).toString ());
				  ad.dismiss ();
				}
			});

		ad.setTitle ("SPEED DIAL");
		ad.setMessage ("Choose a webpage to go to:");
		ad.setView (l);ad.show ();
	  }
	  
	public void printWebDoc()
	  {
		// Check if Kitkat or higher
		if (Build.VERSION.SDK_INT >= 19) {
			android.print.PrintManager printManager = (PrintManager) getSystemService (Context.PRINT_SERVICE);
			// Get a print adapter instance
			android.print.PrintDocumentAdapter printAdapter = webview.createPrintDocumentAdapter ();

			// Create a print job with name and adapter instance
			String jobName = getString (R.string.app_name) + " Web Document";
			printManager.print (jobName, printAdapter,
								new PrintAttributes.Builder ().build ());
		  }
		else {
			new AlertDialog.Builder (this).
			  setTitle ("Printing Error").
			  setMessage ("Unable to print the document.\nThis is only supported on Android Kitkat(API 19) and higher").show ();
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
			else if(requestCode==REQUEST_CODE_RESULTS){
				webview.loadUrl( data.getExtras().getString("result"));
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
			  
			  //OPTIONAL SAVE HISTORY
			if(savewebdata.getBoolean("incogHistory",false)){
			   appendHistory (view.getTitle () + " 》 " + url);
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
		public void onReceivedTitle(WebView view, String title)
		  {
			// TODO: Implement this method
			getActionBar ().setTitle (title);
			super.onReceivedTitle (view, title);
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

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage)
		  {
			// TODO: Implement this method

			if (consoleMessage.messageLevel () == ConsoleMessage.MessageLevel.LOG) {
				String styledText = "<font color='gray'>" + consoleMessage.message () + "</font><br>"; 
				logtxt += Html.fromHtml (styledText);		
			  }
			else if (consoleMessage.messageLevel () == ConsoleMessage.MessageLevel.WARNING) {
				String styledText = "<font color='yellow'>" + consoleMessage.message () + "</font><br>"; 
				logtxt += Html.fromHtml (styledText);		
			  }
			else if (consoleMessage.messageLevel () == ConsoleMessage.MessageLevel.ERROR) {
				String styledText = "<font color='red'>" + consoleMessage.message () + "</font><br>"; 
				logtxt += Html.fromHtml (styledText);		
			  }

			return super.onConsoleMessage (consoleMessage);
		  }

	  }
	  
	public void appendHistory(String newEntry)
	  {
		try {
			File file = new File (getFilesDir () + "/history.dat");
			if (!file.exists ())file.createNewFile ();

			BufferedWriter bufferedWriter = new BufferedWriter (new FileWriter (file, true));
			bufferedWriter.write ("\n" + newEntry);
			bufferedWriter.close ();
		  }
		catch (IOException e) {
			e.printStackTrace ();
		  }
	  }

	public void SaveImage(String url)
	  {
		Toast.makeText (IncognitoActivity.this, "Starting Download", 2000).show ();
		Picasso.
		  with (this) .
		  load (url) .
		  into (new Target () { 
			  @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
				{ 
				  try { 
					  String root = Environment.getExternalStorageDirectory ().toString (); 
					  File myDir = new File (root + "/Download");
					  if (!myDir.exists ()) { 
						  myDir.mkdirs ();
						} 
					  String name = new Date ().toString () + ".jpg"; 
					  myDir = new File (myDir, name); 
					  FileOutputStream out = new FileOutputStream (myDir); 
					  bitmap.compress (Bitmap.CompressFormat.JPEG, 90, out); 
					  out.flush (); 
					  out.close ();
					  Toast.makeText (IncognitoActivity.this, "Saved in " + myDir.getPath (), 2000).show ();
					}
				  catch (Exception e) { 
					  e.printStackTrace ();
					} 
				} 
			  @Override 
			  public void onBitmapFailed(Drawable errorDrawable)
				{
				  Toast.makeText (IncognitoActivity.this, "Download Failed", 2000).show ();
				} 

			  @Override 
			  public void onPrepareLoad(Drawable placeHolderDrawable)
				{ } 
			});

	  }

	public void SavePage()
	  {
		Toast.makeText (this, "Starting Download", 2000).show ();
		Picture picture = webview.capturePicture (); 
		Bitmap b = Bitmap.createBitmap (picture.getWidth (), picture.getHeight (), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas (b); 
		picture.draw (c); 
		FileOutputStream fos = null; 

		try { 

			String root = Environment.getExternalStorageDirectory ().toString (); 
			File myDir = new File (root + "/UBrowser/Pages");
			if (!myDir.exists ()) { 
				myDir.mkdirs ();
			  } 
			String name = "Page" + new Date ().toString () + ".jpg"; 
			myDir = new File (myDir, name); 


			fos = new FileOutputStream (myDir); 
			if (fos != null) { 
				b.compress (Bitmap.CompressFormat.JPEG, 90, fos); 
				fos.close (); 
			  }
			Toast.makeText (IncognitoActivity.this, "Saved in " + myDir.getPath (), 2000).show ();
		  }
		catch ( Exception e ) { 
			e.printStackTrace ();
			Toast.makeText (IncognitoActivity.this, "Failed", 2000).show ();
		  } 

	  }

	public void SaveHtml()
	  {
		final EditText et= new EditText (this);
		et.setHint ("Enter a file name (without extension)");
		new AlertDialog.Builder (this).
		  setTitle ("SAVE AS").setView (et).
		  setPositiveButton ("Save", new DialogInterface.OnClickListener (){

			  @Override
			  public void onClick(DialogInterface p1, int p2)
				{
				  // TODO: Implement this method
				  Toast.makeText (IncognitoActivity.this, "Starting Download", 2000).show ();
				  String givenName=et.getText ().toString ();
				  GetHtml html= new GetHtml();
				  try {
					  //GETTING HTML CONTENT
					  String content = html.getHtml(webview.getUrl());

					  //SAVING TO A .HTML FILE
					  String root = Environment.getExternalStorageDirectory ().toString ();
					  File myDir= new File (root + "/UBrowser/HTML");
					  if (!myDir.exists ()) {
						  myDir.mkdirs ();
						}

					  String name= givenName + ".html";
					  File file = new File (myDir, name);
					  FileWriter writer = new FileWriter (file);
					  writer.write (content);
					  writer.flush ();
					  writer.close ();
					  Toast.makeText (IncognitoActivity.this, "Saved in " + myDir.getPath (), Toast.LENGTH_SHORT).show ();
					}
				  catch (IOException e) {
					  e.printStackTrace ();
					  Toast.makeText (IncognitoActivity.this, "Failed", Toast.LENGTH_SHORT).show ();
					}
				  catch ( Exception e ) { 
					  e.printStackTrace ();
					  Toast.makeText (IncognitoActivity.this, "Couldn't receive HTML content", Toast.LENGTH_SHORT).show ();
					}
				}
			}).show ();		
	  }

	private void addShortcut()
	  { 
		//Adding shortcut for website on Home screen 
		Intent shortcutIntent = new Intent (getApplicationContext(), MainActivity.class);
		shortcutIntent.setData(Uri.parse(webview.getUrl()));

		Intent addIntent = new Intent (); 
		addIntent .putExtra (Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent); 
		addIntent.putExtra (Intent.EXTRA_SHORTCUT_NAME, webview.getTitle()); 
		addIntent.putExtra (Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext (getApplicationContext (), R.drawable.shortcut)); 
		addIntent .setAction ("com.android.launcher.action.INSTALL_SHORTCUT"); 
		addIntent.putExtra ("duplicate", false); 
		//may it's already there so don't duplicate 
		getApplicationContext ().sendBroadcast (addIntent); 
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
}
