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

public class EditorActivity extends Activity
{
	WebView webview;
	ViewFlipper flipper;
	TextView logtext;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.htmlcoder);

		webview = (WebView) findViewById(R.id.coderwebView);
		flipper = (ViewFlipper) findViewById(R.id.htmlcoderViewFlipper);
		logtext = (TextView) findViewById(R.id.coderlogTextView);

		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.setWebViewClient(new webclient());
		webview.setWebChromeClient(new MyWebChromeClient());
		
		ArrayList<String> a=new ArrayList<String>();
		a.add("<html>\n</html>");
		a.add("<div>\n</div>");
		a.add("<head>\n</head>");
		a.add("<body>\n</body>");
		a.add("<script>\n</script>");
		a.add("<style>\n</style>");
		a.add("<h1></h1>");
		a.add("<h2></h2>");
		a.add("<p></p>");
		a.add("<a></a>");
		a.add("<img>");
		a.add("<ul></ul>");
		a.add("<ol></ol>");
		
		final ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, a);
		final AutoCompleteTextView editor=(AutoCompleteTextView)findViewById(R.id.coderTextView);
		
		editor.setOnEditorActionListener(new OnEditorActionListener(){
				@Override
				public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
				{
					editor.setAdapter(aa);
					return true;
				}
			});

		findViewById(R.id.coderrun).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					Switch lang=(Switch)findViewById(R.id.codeswitch);

					if (lang.isChecked())
					{
						flipper.showNext();
						webview.loadData(editor.getText().toString(), "text/html", "UTF-8");
					}
					else
					{
						flipper.showNext();
						webview.loadUrl("javascript:" + editor.getText().toString());
					}
				}
			});

	}


	public class webclient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			// TODO: Implement this method
			view.loadUrl(url);

			if (url.contains(".mp3"))
			{
				Intent intent = new Intent(Intent.ACTION_VIEW); 
				intent.setDataAndType(Uri.parse(url), "audio/*"); 
				startActivity(intent); 
				return true; } 

			else if (url.contains(".mp4") || url.contains(".3gp"))
			{ 
				Intent intent = new Intent(Intent.ACTION_VIEW); 
				intent.setDataAndType(Uri.parse(url), "video/*"); 
				startActivity(intent); 
				return true; }

			else if (url.contains("youtube.com"))
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
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
			
				new AlertDialog.Builder(EditorActivity. this).
					setTitle(view.getUrl()+ " says:").
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
		public boolean onConsoleMessage(ConsoleMessage consoleMessage)
		{
			// TODO: Implement this method
			String logmessage=consoleMessage.message();
			logtext.setText(logtext.getText() + logmessage + "\n");
			return super.onConsoleMessage(consoleMessage);
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (flipper.getDisplayedChild() != 0)
				flipper.setDisplayedChild(0);
			else{finish();}
		}
		return true;
	}
}
