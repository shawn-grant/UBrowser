package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.webkit.*;
import android.widget.*;
import com.oguzdev.circularfloatingactionmenu.library.*;
import java.net.*;

public class OTGBrowserActivity extends Activity implements OnClickListener
{
	WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otgactivity);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		
		webview = (WebView)findViewById(R.id.otgactivityWebView);
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
	  webview.getSettings().setBuiltInZoomControls(true);
	  webview.getSettings().setDisplayZoomControls(false);
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
			new AlertDialog.Builder(this).setTitle("Error").setMessage("No new URL specified").show();
			
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
		  addSubActionView(backButton, 60, 60) .
		  addSubActionView(forwardButton, 60, 60).
		  addSubActionView(refreshButton, 60, 60).
		  attachTo(actionButton) .build();
	}

	public class mywebclient extends WebViewClient
	{	
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			// TODO: Implement this method
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
			super.onPageFinished(view, url);
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		  {
			// TODO: Implement this method
			new AlertDialog.Builder(OTGBrowserActivity.this).setTitle("Error: "+errorCode).
			  setMessage(description).show();

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
			  new AlertDialog.Builder (OTGBrowserActivity.this).
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
		public void onReceivedTitle(WebView view, String title)
		{
			// TODO: Implement this method
			getActionBar().setTitle(title);
			super.onReceivedTitle(view, title);
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
		public void onProgressChanged(WebView view, int newProgress)
		{
			// TODO: Implement this method
		  if(newProgress!=100){
			  getActionBar().setSubtitle("Loading: "+newProgress+"%");
			}else{
			  getActionBar().setSubtitle(null);
			}
			super.onProgressChanged(view, newProgress);
		}
	  }

}
