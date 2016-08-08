package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.webkit.*;
import android.widget.*;
import android.text.*;

public class SourceViewer extends Activity
  {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	  {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		SharedPreferences savewebdata = getSharedPreferences ("savewebdata", Context.MODE_PRIVATE);

		if (savewebdata.getBoolean ("DarkTheme", false)) {
			setTheme (R.style.DarkTheme);
		  }
		setContentView (R.layout.webview_solo);

		final Bundle bundle=getIntent ().getExtras ();

		WebView view= (WebView) findViewById (R.id.solo_WebView);
		view.getSettings ().setJavaScriptEnabled (true);
		view.getSettings ().setLoadWithOverviewMode (true);

		view.setWebViewClient (new WebViewClient (){
			  @Override
			  public boolean shouldOverrideUrlLoading(WebView view, String url)
				{
				  // TODO: Implement this method
				  view.loadUrl (url);
				  return true;
				}
		});
		
		if (bundle != null) {
			String htmlSource = (String) bundle.get ("html");
			String htmlData ="<!doctype html><html><body><pre><code>" + htmlSource +"</code></pre></body></html>";
			view.loadData(htmlData, "text/html", "utf-8");
		  }
		
	  }

  }
