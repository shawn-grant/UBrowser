package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.webkit.*;
import android.widget.*;

public class LockScreenActivity extends Activity
  {
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	  {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		setContentView (R.layout.lockscreen);

		webview = (WebView)findViewById (R.id.lockscreenWebView);
		webview.getSettings ().setJavaScriptEnabled (true);
		webview.getSettings ().setUseWideViewPort (true);
		webview.getSettings ().setJavaScriptCanOpenWindowsAutomatically (true);
		webview.getSettings ().setBuiltInZoomControls (true);
		webview.getSettings ().setDisplayZoomControls (false);
		webview.setWebViewClient (new mywebclient ());

		webview.loadUrl ("http://google.com");
	  }

	public class mywebclient extends WebViewClient
	  {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		  {
			// TODO: Implement this method
			view.loadUrl (url);

			if (url.contains (".mp3") || url.contains (".mp4") || url.contains (".3gp")) {
				Toast.makeText (LockScreenActivity.this, "Cannot open while locked ", 2000).show ();
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
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		  {
			// TODO: Implement this method
			new AlertDialog.Builder (LockScreenActivity.this).setTitle ("Error: " + errorCode).
			  setMessage (description).show ();

			super.onReceivedError (view, errorCode, description, failingUrl);
		  }

	  }
  }
