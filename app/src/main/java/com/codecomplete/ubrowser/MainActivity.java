package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.print.*;
import android.speech.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.webkit.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.widget.TextView.*;
import com.oguzdev.circularfloatingactionmenu.library.*;
import java.io.*;
import java.net.*;
import java.util.*;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import java.util.concurrent.*;
import java.lang.annotation.*;

public class MainActivity extends Activity implements OnClickListener
{
	WebView webview;
	ProgressBar pb;
	EditText urlbar;
	String goingBack;
	
	String failedUrl,errorDescription;
	int error;	
	

	SlidingDrawer slider;
	SharedPreferences savewebdata;
	SharedPreferences.Editor editor;

	Dialog log,history;
	TextView logtext;

	String historytxt,bookmarks,savedpages;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		savewebdata = getSharedPreferences("savewebdata", Context.MODE_PRIVATE);
		editor = savewebdata.edit();

		slider = (SlidingDrawer)findViewById(R.id.mainSlidingDrawer);
		slider.bringToFront();

		webview = (WebView)findViewById(R.id.mainWebView);
		pb = (ProgressBar)findViewById(R.id.mainProgressBar);
		urlbar = (EditText)findViewById(R.id.urlbar);

		historytxt = savewebdata.getString("history", "");
		bookmarks = savewebdata.getString("bookmarks", "");
		savedpages = savewebdata.getString("savedpages", "");
		goingBack = "";

		history = new Dialog(this);
		history.setContentView(R.layout.history);
		log = new Dialog(this);
		log.setTitle("CONSOLE LOG");
		log.setContentView(R.layout.log);

		logtext = (TextView)log.findViewById(R.id.logTextView);

		log.findViewById(R.id.clearlog).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					logtext.setText("");
				}
			});

		//Allowing access to web icons
		WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());

		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setPluginState(WebSettings.PluginState.ON);
		webview.setWebViewClient(new mywebclient());
		webview.setWebChromeClient(new MyWebChromeClient());

		Uri url = getIntent().getData();

		if (url == null)
		{
			webview.loadUrl(savewebdata.getString("lasturl", "http://www.google.com/?gws_rd=cr&ei=c-qhVJJsy5XIBN7UgagD"));
		}
		else
		{
			webview.loadUrl(url.toString());
		}

		urlbar.setOnEditorActionListener(new OnEditorActionListener(){
				@Override
				public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
				{
					if (p2 == EditorInfo.IME_ACTION_DONE || p2 == EditorInfo.IME_ACTION_NEXT)
					{ 
						String webpage=	urlbar.getText().toString();
						Search(webpage);
					}
					return true;
				}
			});

		addFloatingMenu();
		//startService(new Intent( "FloatingBubbleService"));
    }

	public void addFloatingMenu()
	{
		ImageView menub=new ImageView(this);
		ImageView backIcon = new ImageView(this); 
		ImageView forwardIcon = new ImageView(this);
		forwardIcon.setRotation(180);
		ImageView refreshIcon = new ImageView(this);
		ImageView searchIcon = new ImageView(this);
		ImageView voiceIcon = new ImageView(this);

		menub.setImageResource(R.drawable.button_action);
		backIcon.setImageResource(R.drawable.back);
		forwardIcon.setImageResource(R.drawable.back);
		refreshIcon.setImageResource(R.drawable.reload);
		searchIcon.setImageResource(R.drawable.search);
		voiceIcon.setImageResource(R.drawable.voice);

		FloatingActionButton actionButton = new FloatingActionButton.Builder(this) .
			setContentView(menub) .build();

		SubActionButton backButton = new SubActionButton.Builder(this).setContentView(backIcon).build();
		SubActionButton forwardButton = new SubActionButton.Builder(this).setContentView(forwardIcon).build();
		SubActionButton refreshButton = new SubActionButton.Builder(this).setContentView(refreshIcon).build();
		SubActionButton searchButton = new SubActionButton.Builder(this).setContentView(searchIcon).build();
		SubActionButton voiceButton = new SubActionButton.Builder(this).setContentView(voiceIcon).build();

		backButton.setId(1);
		forwardButton.setId(2);
		refreshButton.setId(3);
		searchButton.setId(4);
		voiceButton.setId(5);

		backButton.setOnClickListener(this);
		forwardButton.setOnClickListener(this);
		refreshButton.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		voiceButton.setOnClickListener(this);

		new FloatingActionMenu.Builder(this) .
			addSubActionView(backButton, 50, 50) .
			addSubActionView(forwardButton, 50, 50).
			addSubActionView(refreshButton, 50, 50) .
			addSubActionView(searchButton, 50, 50).
			addSubActionView(voiceButton, 50, 50).
			attachTo(actionButton) .build();
	}

	public void Search(String webpage)
	{

		if (URLUtil.isValidUrl(webpage))
		{
			webview.loadUrl(webpage);
		}
		else
		{
			webview.loadUrl("http://www.google.com/search?sclient=tablet-gws&safe=active&site=&source=hp&q=" + webpage + "&oq=" + webpage + "&gs_l=tablet-gws.3..0i131j0l2.18370.26152.0.27669.7.7.0.0.0.0.473.1455.2-1j1j2.4.0..3..0...1c.1.64.tablet-gws..3.4.1453.5pQH1AWhaxo");
		}

		slider.animateClose();
		Toast.makeText(getApplicationContext(), "...loading...", 
					   Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		// TODO: Implement this method
		switch (item.getItemId())
		{
			case R.id.bookmark:
				Bookmark();
				break;

			case R.id.code:
				startActivity(new Intent("android.intent.action.EDITOR"));
				break;

			case R.id.incognito:
			  //go incognito
			  startActivity(new Intent("android.intent.action.INCOGNITO"));
			    break;
			  
			case R.id.history:			
				LoadHistory();
				break;

			case R.id.viewbookarks:
				LoadBookmarks();
				break;

			case R.id.quickload:			
				SpeedDial();
				break;

			case R.id.print:			
				printWebDoc();
				break;

			case R.id.viewsource:	
				webview.loadUrl("javascript:alert(document.getElementsByTagName('html')[0].innerHTML);");
				break;

			case R.id.log:		
				log.show();
				break;

			case R.id.settings:		
				startActivity(new Intent("android.intent.action.SETTINGS"));
				break;

			case R.id.about:		
				new AlertDialog.Builder(this).
					setTitle("ABOUT").
					setMessage("Developer: Shawn Grant(shawn-grant.github.io)" +
							   "\nCortechx,inc" +
							   "\nDeveloped: November 28,2015" +
							   "\nEmail: shawngrant333@gmail.com").
					show();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void Bookmark()
	{
		final String url=webview.getUrl();
		final EditText bookmarkname=new EditText(this);
		bookmarkname.setText(webview.getTitle());

		new AlertDialog.Builder(this).
			setTitle("Add Bookmark").
			setMessage("name this bookmark:").
			setView(bookmarkname).
			setPositiveButton("Add", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					if (bookmarkname.getText().toString().isEmpty() == false)
						bookmarks += bookmarkname.getText().toString() + " 》 " + url + "\n";
					else
						bookmarks += webview.getTitle() + " 》 " + url + "\n";

					editor.putString("bookmarks", bookmarks);
					editor.commit();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					p1.dismiss();
				}
			}).
			show();
	}

	public void LoadBookmarks()
	{
		String[] list=bookmarks.split("\n");
		final Dialog bookmark=new Dialog(this);
		bookmark.setTitle("Bookmarks");
		bookmark.setContentView(R.layout.bookmarks);
		LinearLayout bookmarkll=(LinearLayout) bookmark.findViewById(R.id.bookmarksLayout);

		for (int i=0;list.length > i;i++)
		{
			final String item=list[i];
			if (item.equals(""))
			{}
			else
			{
				String [] parts=item.split(" 》 ");
				String title=parts[0];
				final String url=parts[1];

				ImageView iv=new ImageView(this);
				TextView tv=new TextView(this);

				iv.setImageResource(R.drawable.icon);
				tv.setTextColor(Color.BLUE);
				tv.setTextSize(30);
				tv.setText(title);

				LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
																		   LinearLayout.LayoutParams.FILL_PARENT);
				lp.setMargins(0, 0, 0, 10);
				LinearLayout ll=new LinearLayout(this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setBackgroundColor(Color.BLACK);
				ll.setLayoutParams(lp);
				ll.addView(iv, 100, 100);
				ll.addView(tv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				bookmarkll.addView(ll);

				ll.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							// TODO: Implement this method
							webview.loadUrl(url);
							bookmark.dismiss();
						}
					});

				ll.setOnLongClickListener(new OnLongClickListener(){

						@Override
						public boolean onLongClick(View p1)
						{
							// TODO: Implement this method
							new AlertDialog.Builder(MainActivity.this).
								setTitle("Remove From Bookmarks").
								setMessage("Do you want to remove this item?").
								setPositiveButton("Remove", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface p1, int p2)
									{
										// TODO: Implement this method
										bookmarks = bookmarks.replace(item, "");
										editor.putString("bookmarks", bookmarks);
										editor.commit();
										bookmark.dismiss();
										LoadBookmarks();
									}
								}).
								setNegativeButton("Remove All", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface p1, int p2)
									{
										// TODO: Implement this method
										bookmarks = "";
										editor.putString("bookmarks", bookmarks);
										editor.commit();
										bookmark.dismiss();
										LoadBookmarks();
									}
								}).show();
							return true;
						}
					});
			}
		}

		bookmark.show();
	}

	private void SpeedDial()
	{
		// TODO: Implement this method
		ArrayList<String> a=new ArrayList<String>();
		a.add("http://google.com");
		a.add("http://facebook.com");
		a.add("http://youtube.com");
		a.add("http://yahoo.com");
		a.add("http://gmail.com");
		a.add("http://jutc.com");
		a.add("http://twitter.com");
		a.add("http://microsoft.com");
		a.add("http://translate.google.com");
		a.add("http://developer.android.com");

		ListView l=new ListView(this);
		final ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, a);
		l.setAdapter(aa);
		l.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					webview.loadUrl(aa.getItem(p3).toString());
				}
			});
		new AlertDialog.Builder(this).
			setTitle("SPEED DIAL").
			setMessage("Choose a webpage to go to:").
			setView(l).show();
	}

	private void LoadHistory()
	{
		// TODO: Implement this method
		history.setTitle("HISTORY");
	    ListView lv=(ListView) history.findViewById(R.id.historyListView);
		final String[] list=historytxt.split("\n");
		final ArrayList<String> array=new ArrayList<String>();

		for (int i=0;i < list.length;i++)
		{
			String item=list[i];
			if (item.equals(""))
			{}
			else
			{array.add(item);}
		}
		final ArrayAdapter<String> aa=new ArrayAdapter<String>(this, R.layout.list_item, array);
		lv.setAdapter(aa);
		lv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					String i=array.get(p3);
					webview.loadUrl(i.split(" 》 ")[1]);
					history.dismiss();
				}
			});
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(final AdapterView<?> p1a, View p2, final int p3, long p4)
				{
					// TODO: Implement this method
					final String i=array.get(p3);

					new AlertDialog.Builder(MainActivity.this).
						setTitle("Remove From History").
						setMessage("Do you want to remove this item?").
						setPositiveButton("Remove", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								historytxt = historytxt.replace(i, "");
								editor.putString("history", historytxt);
								editor.commit();
								LoadHistory();
							}
						}).
						setNegativeButton("Remove All", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								historytxt = "";
								editor.putString("history", historytxt);
								editor.commit();
								LoadHistory();
							}
						}).show();
					return true;
				}
			});

		history.show();
	}

	public void printWebDoc()
	{
		android.print.PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

		// Get a print adapter instance
		android.print.PrintDocumentAdapter printAdapter = webview.createPrintDocumentAdapter();

		// Create a print job with name and adapter instance
		String jobName = getString(R.string.app_name) + " Web Document";
		android.print.PrintJob printJob = printManager.print(jobName, printAdapter,
															 new PrintAttributes.Builder().build());
	}

	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		switch (p1.getId())
		{
			case 1:
				webview.goBack();
				slider.animateClose();
				Toast.makeText(getApplicationContext(), "...going back...", 
							   Toast.LENGTH_LONG).show();
				goingBack = "true";
				break;

			case 2:
				webview.goForward();
				slider.animateClose();
				Toast.makeText(getApplicationContext(), "...going forward...", 
							   Toast.LENGTH_LONG).show();
				goingBack = "false";
				break;

			case 3:
				webview.reload();
				slider.animateClose();
				Toast.makeText(getApplicationContext(), "...reloading...", 
							   Toast.LENGTH_LONG).show();
				break;

			case 4:
				slider.animateOpen();
				break;

			case 5:
				Intent i= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				i.putExtra(RecognizerIntent.EXTRA_PROMPT, "what are you searching for?");
				startActivityForResult(i, 1000);
				break;

			case R.id.stop:
				webview.stopLoading();
				slider.animateClose();
				Toast.makeText(getApplicationContext(), "...stopping...", 
							   Toast.LENGTH_LONG).show();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{ 
		if (resultCode == RESULT_OK)
		{
			if (requestCode == 1000)
			{ 
				ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String words=matches.get(0);
				Search(words);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class mywebclient extends WebViewClient
	  {
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			// TODO: Implement this method
			webview.setVisibility(View.VISIBLE);
			pb.setVisibility(View.VISIBLE);
			findViewById(R.id.stop).setVisibility(View.VISIBLE);
			
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
				view.loadUrl(view.getTag().toString()); 
			}
			else
			{ 
				view.setTag(url); 
			}

			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			// TODO: Implement this method
			pb.setVisibility(View.GONE);
			findViewById(R.id.stop).setVisibility(View.GONE);
			urlbar.setText(url);

			if ("about:blank".equals(url) && goingBack.equals("true"))
			{
				view.goBack();
				goingBack = "";
			}
			else if ("about:blank".equals(url) && goingBack.equals("false"))
			{
				view.goForward();
				goingBack = "";
			}

			if (view.getTitle() != null)
			{
				historytxt += view.getTitle().toUpperCase() + " 》 " + url + "\n";
			}
			
			editor.putString("history", historytxt);
			editor.commit();
			super.onPageFinished(view, url);
		}

	  @Override
	  public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
		  // TODO: Implement this method
		  failedUrl=failingUrl;
		  error=errorCode;
		  errorDescription=description;

		  Log.d ("OnError", description);
		  Log.d("failedUrl",failingUrl);
		  Log.d("errorCode",""+errorCode);
		  
		  webview.addJavascriptInterface (new JsInterface(), "JSInterface");
		  view.loadUrl("file:///android_asset/errorpage.html");
		  
		  super.onReceivedError (view, errorCode, description, failingUrl);
		}

	}

	public class MyWebChromeClient extends WebChromeClient
	{ 
		//Handle javascript alerts: 
		@Override 
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
		{
			try
			{
				new AlertDialog.Builder(MainActivity.this).
					setTitle(new URL(view.getUrl()).getHost() + " says:").
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
			}
			catch (MalformedURLException e)
			{e.printStackTrace();}
			return true; 
		}
		
		@Override
		public void onReceivedIcon(WebView view, Bitmap picon)
		{
			// TODO: Implement this method
			Drawable ic=new BitmapDrawable(getResources(), picon);
			getActionBar().setIcon(ic);
			super.onReceivedIcon(view, picon);
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

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage)
		{
			// TODO: Implement this method
			String logmessage=consoleMessage.message();
			logtext.setText(logtext.getText() + logmessage + "\n");
			return super.onConsoleMessage(consoleMessage);
		}

	}
	
	public class JsInterface{
	  Context context;
	  
	  public void JsInterface(Context c){
		context=c;
	  }
	  
		@JavascriptInterface
	  public void reloadPage(){
		webview.loadUrl(failedUrl);
		  Log.d("reloadPage()",failedUrl);
	  }
	  
		@JavascriptInterface
	  public void setErrorCode(){
		  webview.loadUrl("javascript:document.getElementById('errorcode').innerHTML=\""+error+"\";");
		  Log.d("setErrorCode()",""+error);
	  }
	  
		@JavascriptInterface
	  public void setErrorDescription(){
		  webview.loadUrl("javascript:document.getElementById('description').innerHTML=\""+errorDescription+"\";");
		  Log.d("setErrorDescription()",errorDescription);
	  }
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (webview.canGoBack())
			{
				webview.goBack();
				goingBack = "true";
			}
			else
			{finish();}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop()
	{
		// TODO: Implement this method
	  if(webview.getUrl().equals("file:///android_asset/errorpage.html")!=true){
		  editor.putString("lasturl", webview.getUrl().toString());
		}
		editor.commit();
		super.onStop();
	}
}
