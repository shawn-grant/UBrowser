package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.webkit.*;
import android.widget.*;
import android.widget.TextView.*;
import java.util.*;

import android.view.View.OnClickListener;
import android.app.ActionBar.*;

public class EditorActivity extends Activity
  {
	WebView webview;
	ViewFlipper flipper;
	EditText html,javascript;

	@Override
	public void onCreate(Bundle savedInstanceState)
	  {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		SharedPreferences savewebdata = getSharedPreferences ("savewebdata", Context.MODE_PRIVATE);

		if (savewebdata.getBoolean ("DarkTheme", false)) {
			setTheme (R.style.DarkTheme);
		  }
		setContentView (R.layout.htmlcoder);

		webview = (WebView) findViewById (R.id.editorwebView);
		flipper = (ViewFlipper) findViewById (R.id.editorViewFlipper);
		html = (EditText) findViewById (R.id.htmlEditText);
		javascript = (EditText) findViewById (R.id.jsEditText);

		webview.getSettings ().setJavaScriptEnabled (true);
		webview.getSettings ().setLoadWithOverviewMode (true);
		webview.getSettings ().setBuiltInZoomControls (true);
		webview.getSettings ().setUseWideViewPort (true);
		webview.setWebViewClient (new webclient ());
		webview.setWebChromeClient (new MyWebChromeClient ());

		ArrayList<String> a=new ArrayList<String> ();
		a.add ("<html>\n\n</html>");
		a.add ("<div>\n\n</div>");
		a.add ("<head>\n\n</head>");
		a.add ("<body>\n\n</body>");
		a.add ("<script>\n\n</script>");
		a.add ("<style>\n\n</style>");
		a.add ("<h1></h1>");
		a.add ("<h2></h2>");
		a.add ("<p></p>");
		a.add ("<a></a>");
		a.add ("<img>");
		a.add ("<ul>\n\n</ul>");
		a.add ("<ol>\n\n</ol>");

		final ArrayAdapter<String> aa = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, a);

		AddTabs ();
		setTemplate ();
	  }

	public void AddTabs()
	  {
		getActionBar ().setNavigationMode (ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab htmlTab = getActionBar ().newTab ();
		ActionBar.Tab jsTab = getActionBar ().newTab ();
		ActionBar.Tab webTab = getActionBar ().newTab ();

		htmlTab.setText ("HTML");
		jsTab.setText ("JS");
		webTab.setText ("DESIGN");

		htmlTab.setTabListener (new ActionBar.TabListener (){

			  @Override
			  public void onTabSelected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				  flipper.setDisplayedChild (0);
				}

			  @Override
			  public void onTabUnselected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				}

			  @Override
			  public void onTabReselected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				}
			});

		jsTab.setTabListener (new ActionBar.TabListener (){

			  @Override
			  public void onTabSelected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				  flipper.setDisplayedChild (1);
				}

			  @Override
			  public void onTabUnselected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				}

			  @Override
			  public void onTabReselected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				}
			});

		webTab.setTabListener (new ActionBar.TabListener (){

			  @Override
			  public void onTabSelected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				  flipper.setDisplayedChild (2);
				  webview.loadData (html.getText ().toString (), "text/html", "UTF-8");
				  webview.loadUrl ("javascript:" + javascript.getText ().toString ());
				}

			  @Override
			  public void onTabUnselected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				}

			  @Override
			  public void onTabReselected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				}
			});

		getActionBar ().addTab (htmlTab);
		getActionBar ().addTab (jsTab);
		getActionBar ().addTab (webTab);
	  }

	public void setTemplate()
	  {
		html.setText ("<html>\n  <head>\n\n   </head>\n   <body>\n\n   </body>\n</html>");
		javascript.setText ("alert('This is an alert from UBrowser');");
	  }

	public class webclient extends WebViewClient
	  {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		  {
			// TODO: Implement this method
			view.loadUrl (url);

			if (url.contains (".mp3")) {
				Intent intent = new Intent (Intent.ACTION_VIEW); 
				intent.setDataAndType (Uri.parse (url), "audio/*"); 
				startActivity (intent); 
				return true; } else if (url.contains (".mp4") || url.contains (".3gp")) { 
				Intent intent = new Intent (Intent.ACTION_VIEW); 
				intent.setDataAndType (Uri.parse (url), "video/*"); 
				startActivity (intent); 
				return true; } else if (url.contains ("youtube.com")) {
				startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse (url)));
				return true;
			  }

			return true;
		  }
	  }

	public class MyWebChromeClient extends WebChromeClient
	  { 
		//Handle javascript alerts: 
		@Override 
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
		  {

			new AlertDialog.Builder (EditorActivity. this).
			  setTitle (view.getUrl () + " says:").
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

			return true; 
		  }

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage)
		  {
			// TODO: Implement this method
			String logmessage=consoleMessage.message ();
			return super.onConsoleMessage (consoleMessage);
		  }

	  }

	public boolean onKeyDown(int keyCode, KeyEvent event)
	  {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder (EditorActivity. this).
			  setTitle ("EXIT").
			  setMessage ("Are you sure You want to leave?").
			  setPositiveButton ("Yes", new DialogInterface.OnClickListener (){
				  @Override
				  public void onClick(DialogInterface p1, int p2)
					{
					  // TODO: Implement this method
					  finish ();
					}
				}).setNegativeButton ("No", null).
			  show ();
		  }
		return true;
	  }
  }
