package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.util.*;
import android.widget.AdapterView.*;

public class DOMEditor extends Activity
  {

	ArrayList<String> IDs;
	WebView webview;
	ListView IDlist;
	ViewFlipper flipper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	  {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		setContentView(R.layout.domeditor);
		
		IDs= new ArrayList<String> ();
		webview = (WebView) findViewById(R.id.domeditorWebView);
		IDlist= (ListView) findViewById(R.id.domeditorListView);
		flipper = (ViewFlipper) findViewById(R.id.content);
		findViewById(R.id.domeditorSlidingDrawer).bringToFront();
		
		webview.getSettings ().setJavaScriptEnabled (true);
		webview.getSettings ().setLoadWithOverviewMode (true);
		webview.getSettings ().setUseWideViewPort (true);
		webview.getSettings ().setJavaScriptCanOpenWindowsAutomatically (true);
		webview.getSettings ().setBuiltInZoomControls (true);
		webview.getSettings ().setDisplayZoomControls (false);
		webview.addJavascriptInterface(new WebInterface(this), "Android");
		webview.setWebViewClient (new mywebclient ());
		
		String url = getIntent().getExtras().get("url").toString();
		webview.loadUrl(url);
	  }
  
	public class WebInterface
	  {
		Context c;
		
	    WebInterface(Context context){
		  c = context;
		}
		
		@JavascriptInterface
		public void getElementIDs(String Ids){
		  String[] list=Ids.split("+");
		  
		  for(int i=0; i<list.length;i++){
			IDs.add(list[i]);
		  }
		  ArrayAdapter<String> aa = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, list);
		  IDlist.setAdapter(aa);
		  
			IDlist.setOnItemClickListener (new OnItemClickListener (){
				  @Override
				  public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
					  // TODO: Implement this method
					  String id=(String) p1.getAdapter().getItem(p3);
					  flipper.setDisplayedChild(1);
					}
				});
		}
      }
	  
	  public void GetIDs(View v){
		  webview.loadUrl("javascript:var allElements = document.getElementsByTagName(\"*\");var Ids=\"\";for (var i = 0, n = allElements.length; i < n; ++i){var el = allElements[i]; if (el.id) { Ids+=el.id+\"+\";} } Android.getElementIDs(Ids);");
	  }
	  
	public class mywebclient extends WebViewClient
	  {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		  {
			// TODO: Implement this method
			view.loadUrl (url);
			return true;
		  }

        @Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		  {
			// TODO: Implement this method
			new AlertDialog.Builder (DOMEditor.this).setTitle ("Error: " + errorCode).
			  setMessage (description).show ();

			super.onReceivedError (view, errorCode, description, failingUrl);
		  }

	  }
}
