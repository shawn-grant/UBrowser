package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.codecomplete.ubrowser.*;
import com.squareup.picasso.*;
import java.io.*;
import java.util.*;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class ResultsActivity extends Activity
  {

	ViewFlipper flipper;
	ListView historyList;
	LinearLayout bookmarkll, pages;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	  {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		SharedPreferences savewebdata = getSharedPreferences ("savewebdata", Context.MODE_PRIVATE);

		if (savewebdata.getBoolean ("DarkTheme", false)) {
			setTheme (R.style.DarkTheme);
		  }
		setContentView (R.layout.results);

		flipper = (ViewFlipper) findViewById (R.id.resultsViewFlipper);
		historyList = (ListView) findViewById (R.id.historyListView);
		bookmarkll = (LinearLayout) findViewById (R.id.bookmarksLinearLayout);
		pages = (LinearLayout) findViewById (R.id.pagesLinearLayout);

		AddTabs ();
		displayHistory ();
		LoadSavedPages ();
		LoadBookmarks ();
	  }

	public void AddTabs()
	  {
		String type = getIntent ().getExtras ().getString ("type");
		getActionBar ().setNavigationMode (ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab historyTab = getActionBar ().newTab ();
		ActionBar.Tab bookmarksTab = getActionBar ().newTab ();
		ActionBar.Tab pagesTab = getActionBar ().newTab ();

		historyTab.setText ("HISTORY");
		bookmarksTab.setText ("BOOKMARKS");
		pagesTab.setText ("SAVED PAGES");

		historyTab.setIcon (R.drawable.history);
		bookmarksTab.setIcon (R.drawable.bookmark);
		pagesTab.setIcon (R.drawable.save);

		historyTab.setTabListener (new ActionBar.TabListener (){

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

		bookmarksTab.setTabListener (new ActionBar.TabListener (){

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

		pagesTab.setTabListener (new ActionBar.TabListener (){

			  @Override
			  public void onTabSelected(ActionBar.Tab p1, FragmentTransaction p2)
				{
				  // TODO: Implement this method
				  flipper.setDisplayedChild (2);

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

		getActionBar ().addTab (historyTab);
		getActionBar ().addTab (bookmarksTab);
		getActionBar ().addTab (pagesTab);

		if (type.equals ("history")) {
			historyTab.select ();
		  }
		else if (type.equals ("bookmarks")) {
			bookmarksTab.select ();
		  }
	  }


	public void LoadHistory(String rawValue)
	  {
		// TODO: Implement this method
		final String[] list = rawValue.split ("\n");
		Log.i ("LoadHistory", "Recieved rawValue:" + rawValue);
		final ArrayList<String> titleArray = new ArrayList<String> ();
		final ArrayList<String> urlArray = new ArrayList<String> ();

		for (int i = 0; i < list.length; i++) {
			String item=list [i];
			if (!item.equals ("") && !list[i - 1].equals(item)) {
				String title=item.split (" 》 ") [0];
				String url=item.split (" 》 ") [1];
				titleArray.add (title);
				urlArray.add (url);
			  }
		  }
		ArrayAdapter<String> aa=new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, titleArray);
		historyList.setAdapter (aa);
		historyList.setOnItemClickListener (new OnItemClickListener (){

			  @Override
			  public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
				  // TODO: Implement this method
				  String i=urlArray.get (p3);
				  Intent returnIntent = new Intent (); 
				  returnIntent.putExtra ("result", i); 
				  setResult (Activity.RESULT_OK, returnIntent); 
				  finish ();
				}
			});
		historyList.setOnItemLongClickListener (new OnItemLongClickListener (){

			  @Override
			  public boolean onItemLongClick(final AdapterView<?> p1a, View p2, final int p3, long p4)
				{
				  // TODO: Implement this method
				  final String entry=titleArray.get (p3) + " 》 " + urlArray.get (p3);

				  new AlertDialog.Builder (ResultsActivity.this).
					setTitle ("Remove From History").
					setMessage ("Do you want to remove this item?").
					setPositiveButton ("Remove", new DialogInterface.OnClickListener (){

						@Override
						public void onClick(DialogInterface p1, int p2)
						  {
							// TODO: Implement this method

						  }
					  }).
					setNegativeButton ("Remove All", new DialogInterface.OnClickListener (){

						@Override
						public void onClick(DialogInterface p1, int p2)
						  {
							// TODO: Implement this method

						  }
					  }).show ();
				  return true;
				}
			});

	  }


	public void displayHistory()
	  {
		String rawValue = "";
		try {
			File file = new File (getFilesDir () + "/history.dat");
			BufferedReader bufferedReader = new BufferedReader (new FileReader (file));

			String line; 
			while ((line = bufferedReader.readLine ()) != null) { 
				Log.d ("Output_Line", line);
				rawValue += "\n" + line;
			  }
		    bufferedReader.close ();

			LoadHistory (rawValue);
		  }
		catch (FileNotFoundException e) {
			e.printStackTrace ();
		  }
		catch (IOException e) {
			e.printStackTrace ();
		  }

	  }

	public void LoadBookmarks()
	  {
		String rawValue = "";
		try {
			File file = new File (getFilesDir () + "/bookmarks.dat");
			BufferedReader bufferedReader = new BufferedReader (new FileReader (file));

			String line; 
			while ((line = bufferedReader.readLine ()) != null) { 
				Log.d ("Output_Line", line);
				rawValue += "\n" + line;
			  }
		    bufferedReader.close ();

			PopulateBookmarks (rawValue);
		  }
		catch (FileNotFoundException e) {
			e.printStackTrace ();
		  }
		catch (IOException e) {
			e.printStackTrace ();
		  }
	  }

	public void PopulateBookmarks(String rawValue)
	  {
		bookmarkll.removeAllViews();
		String[] list = rawValue.split ("\n");
		if (list.length == 0) {
			TextView tv = new TextView (this);
			tv.setText ("NO BOOKMARKS ADDED");
			bookmarkll.addView (tv);
		  }
		else {

			for (int i=0; list.length > i; i++) {
				final String item = list [i];

				if (!item.equals ("")) {
					String [] parts=item.split (" 》 ");
					String title = parts [0];
					final String url = parts [1];

					View fl = View.inflate(this, R.layout.bookmark, null);
					TextView tv = (TextView)fl.findViewById(R.id.bookmarkTextView);
					tv.setText(title);

					fl.setOnClickListener (new OnClickListener (){

						  @Override
						  public void onClick(View p1)
							{
							  // TODO: Implement this method
							  Intent returnIntent = new Intent (); 
							  returnIntent.putExtra ("result", url); 
							  setResult (Activity.RESULT_OK, returnIntent); 
							  finish ();
							}
						});

					fl.setOnLongClickListener (new OnLongClickListener (){

						  @Override
						  public boolean onLongClick(View p1)
							{
							  // TODO: Implement this method
							  new AlertDialog.Builder (ResultsActivity.this).
								setTitle ("Remove From Bookmarks").
								setMessage ("Do you want to remove this item?").
								setPositiveButton ("Remove", new DialogInterface.OnClickListener (){

									@Override
									public void onClick(DialogInterface p1, int p2)
									  {
										// TODO: Implement this method
										LoadBookmarks ();
									  }
								  }).
								setNegativeButton ("Remove All", new DialogInterface.OnClickListener (){

									@Override
									public void onClick(DialogInterface p1, int p2)
									  {
										// TODO: Implement this method
										LoadBookmarks ();
									  }
								  }).show ();
							  return true;
							}
						});
					bookmarkll.addView (fl);
				  }
			  }
		  }
	  }

	public void LoadSavedPages()
	  {
		pages.removeAllViews();
		final String path = Environment.getExternalStorageDirectory ().toString () + "/UBrowser/Pages/"; 
		Log.d ("Files", "Path: " + path); 
		File f = new File (path); 
		final File files[] = f.listFiles (); 

		if (f.listFiles () != null) {
			Log.d ("Files", "Size: " + files.length); 

			for (int i=0; i < files.length; i++) {
				final String fileName = files [i].getName ();
				Log.d ("Files", "FileName:" + fileName);
				final String fileUrl="file://" + path + fileName;

				ImageView iv= new ImageView (this);
				LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams (200, LinearLayout.LayoutParams.FILL_PARENT);
				lp.setMargins (5, 5, 0, 5);
				iv.setLayoutParams (lp);
				Picasso.with (this).load (fileUrl).into (iv);

				iv.setOnClickListener (new OnClickListener (){

					  @Override
					  public void onClick(View p1)
						{
						  // TODO: Implement this method
						  Intent returnIntent = new Intent (); 
						  returnIntent.putExtra ("result", fileUrl); 
						  setResult (Activity.RESULT_OK, returnIntent); 
						  finish ();
						}
					});

				iv.setOnLongClickListener (new OnLongClickListener (){

					  @Override
					  public boolean onLongClick(View p1)
						{
						  // TODO: Implement this method
						  new AlertDialog.Builder (ResultsActivity.this).
							setTitle ("Delete?").
							setMessage ("Delete this item").
							setPositiveButton ("Delete", new DialogInterface.OnClickListener (){
								@Override
								public void onClick(DialogInterface p1, int p2)
								  {
									// TODO: Implement this method
									File f= new File (path + fileName);
									Boolean b = f.delete ();
									LoadSavedPages ();
								  }
							  }).
							show ();
						  return true;
						}
					});
				pages.addView (iv);
			  }
		  }
		else {
			TextView tv = new TextView (this);
			tv.setText ("NO SAVED PAGES");
			pages.addView (tv);
		  }

	  }
  }
