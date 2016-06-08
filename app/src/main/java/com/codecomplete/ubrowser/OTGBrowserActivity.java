package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.webkit.*;
import android.widget.*;
import com.oguzdev.circularfloatingactionmenu.library.*;
import java.util.concurrent.*;

public class OTGBrowserActivity extends Activity implements OnClickListener
{
	ProgressBar pb;
	WebView webview;
	TextView title;
	String lastUrl;
	
	SharedPreferences savewebdata;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otgactivity);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		
	    savewebdata = getSharedPreferences("savewebdata", Context.MODE_PRIVATE);
	    editor = savewebdata.edit();
		
		lastUrl=savewebdata.getString("otglast","http://www.google.com");
	  
		webview = (WebView)findViewById(R.id.otgactivityWebView);
		pb = (ProgressBar) findViewById(R.id.otgactivityProgressBar);

		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setPluginState(WebSettings.PluginState.ON);
		webview.setWebViewClient(new mywebclient());
		webview.setWebChromeClient(new MyWebChromeClient());
		
		AddNavButtons();

		Uri url = getIntent().getData();

		if (url != null)
		{
			webview.loadUrl(url.toString());
		}else{
			Toast.makeText(this,"no new URL specified",2000).show();
			webview.loadUrl(lastUrl);
		}
	}

	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		switch (p1.getId())
		{
			
		}
	}
	
	public void AddNavButtons(){
		ImageView menub=new ImageView(this);
		ImageView backIcon = new ImageView(this); 
		ImageView forwardIcon = new ImageView(this);
		forwardIcon.setRotation(180);
		ImageView refreshIcon = new ImageView(this);
		
		menub.setImageResource(R.drawable.button_action);
		backIcon.setImageResource(R.drawable.back);
		forwardIcon.setImageResource(R.drawable.back);
		refreshIcon.setImageResource(R.drawable.reload);

		FloatingActionButton actionButton = new FloatingActionButton.Builder(this) .
		  setContentView(menub) .build();

		SubActionButton backButton = new SubActionButton.Builder(this).setContentView(backIcon).build();
		SubActionButton forwardButton = new SubActionButton.Builder(this).setContentView(forwardIcon).build();
		SubActionButton refreshButton = new SubActionButton.Builder(this).setContentView(refreshIcon).build();
		
		backButton.setOnClickListener (new SubActionButton.OnClickListener (){

			  @Override
			  public void onClick(View p1)
				{
				  // TODO: Implement this method
				  webview.goBack();
				  Toast.makeText(getApplicationContext(), "...going back...", 
								 Toast.LENGTH_LONG).show();
				}
			});
		forwardButton.setOnClickListener(new SubActionButton.OnClickListener (){

			  @Override
			  public void onClick(View p1)
				{
				  // TODO: Implement this method
				  webview.goForward();
				  Toast.makeText(getApplicationContext(), "...going forward...", 
								 Toast.LENGTH_LONG).show();
				}
			});
		refreshButton.setOnClickListener(new SubActionButton.OnClickListener (){

			  @Override
			  public void onClick(View p1)
				{
				  // TODO: Implement this method
				  webview.reload();
				  Toast.makeText(getApplicationContext(), "...reloading...", 
								 Toast.LENGTH_LONG).show();
				}
			});
		
		new FloatingActionMenu.Builder(this) .
		  addSubActionView(backButton, 50, 50) .
		  addSubActionView(forwardButton, 50, 50).
		  addSubActionView(refreshButton, 50, 50).
		  attachTo(actionButton) .build();
	}

	public class mywebclient extends WebViewClient
	{	
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			// TODO: Implement this method
			pb.setVisibility(View.VISIBLE);
			Dialog nonet=new Dialog(OTGBrowserActivity.this);
			nonet.setContentView(R.layout.nonetwork);
			nonet.setTitle("NETWORK ERROR");
			nonet.setCanceledOnTouchOutside(true);
			
			super.onPageStarted(view, url, favicon);	
		}			

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			// TODO: Implement this method
			view.loadUrl(url);	

			if (url.contains(".mp3"))
			{
				Intent intent = new Intent(Intent.ACTION_VIEW); 
				intent.setDataAndType(Uri.parse(url), "audio/*"); 
				view.getContext().startActivity(intent); 
				return true; } 

			else if (url.contains(".mp4") || url.contains(".3gp"))
			{ 
				Intent intent = new Intent(Intent.ACTION_VIEW); 
				intent.setDataAndType(Uri.parse(url), "video/*"); 
				view.getContext().startActivity(intent); 
				return true; }

			else if (url.contains("youtube.com"))
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				webview.goBack();
				return true;
			}

			if ("about:blank".equals(url) && view.getTag() != null)
			{ 
				view.loadUrl(view.getTag().toString()); }
			else
			{ view.setTag(url); }
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			// TODO: Implement this method
			pb.setVisibility(View.GONE);
			super.onPageFinished(view, url);
		}

	}

	public class MyWebChromeClient extends WebChromeClient
	{ 
		//Handle javascript alerts: 
		@Override 
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
		{ 
			Log.d("alert", message); 
			new AlertDialog.Builder(OTGBrowserActivity.this).
				setTitle(url + " says:").
				setMessage(message).
				setPositiveButton("OK", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
						result.confirm(); 
					}
				}).
				show();
			return true; 
		}

		@Override
		public void onReceivedTitle(WebView view, String ptitle)
		{
			// TODO: Implement this method
			getActionBar().setTitle(ptitle);
			super.onReceivedTitle(view, ptitle);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress)
		{
			// TODO: Implement this method
			pb.setProgress(newProgress);
			super.onProgressChanged(view, newProgress);
		}
	  }

	@Override
	protected void onStop()
	  {
		// TODO: Implement this method
		editor.putString("otglast",webview.getUrl());
		super.onStop ();
	  }
	
}
