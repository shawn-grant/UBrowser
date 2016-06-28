package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.webkit.*;

public class SourceViewer extends Activity
  {
	String source;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	  {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		setContentView(R.layout.view_source);
		
		Bundle bundle=getIntent().getExtras();
		
		WebView view= (WebView) findViewById(R.id.view_source_WebView);
		view.getSettings().setJavaScriptEnabled(true);
		view.getSettings().setLoadWithOverviewMode(true);
		view.addJavascriptInterface (new JsInterface (), "JSInterface");

		view.setWebViewClient(new WebViewClient(){
			  @Override
			  public boolean shouldOverrideUrlLoading(WebView view, String url)
				{
				  // TODO: Implement this method
				  view.loadUrl (url);
				  return true;
				}			 
			});
		
		if(bundle!=null){
		  source=bundle.getString("source");
		  String title="View Source: "+bundle.getString("url");
		  getActionBar().setTitle(title);
		  view.loadUrl("file:///android_asset/view-source.html");
		}
		else{
		  Log.d("source","Undefined");
		}
		
	  }
	  
	public class JsInterface
	  {
		Context context;

		public void JsInterface(Context c)
		  {
			context = c;
		  }

		@JavascriptInterface
		public String getHtml(){
		  
		  return source;
		}
		}
  
}
