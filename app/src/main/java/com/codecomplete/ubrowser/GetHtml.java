package com.codecomplete.ubrowser;

import android.os.*;
import android.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GetHtml
  {
	public void GetHtml(){
	  
	}
	
	public String getHtml(String urlToRead)
	  {
		String html = null;
		
		try {
			html = new MyTask ().execute (urlToRead).get ();
		  }
		catch (ExecutionException e) {
		  e.printStackTrace();
		}
		catch (InterruptedException e) {
		  e.printStackTrace();
		}
		
		return html;
	  }
	  
	public class MyTask extends AsyncTask<String, Void, String>
	  {

		@Override
		protected String doInBackground(String[] p1)
		  {
			// TODO: Implement this method
			URL url; // The URL to read
			HttpURLConnection conn; // The actual connection to the web page
			BufferedReader rd; // Used to read results from the web page
			String line; // An individual line of the web page HTML
			String result = ""; // A long string containing all the HTML

			try {
				url = new URL (p1[0]);
				conn = (HttpURLConnection) url.openConnection ();
				conn.setRequestMethod ("GET");
				rd = new BufferedReader (new InputStreamReader (conn.getInputStream ()));

				while ((line = rd.readLine ()) != null) {
					result += line;
				  }
				rd.close ();
				Log.d("GetHtml",result);
			  }
			catch (Exception e) {
				e.printStackTrace ();
				Log.d("GetHtml","problem");
			  }
			return result;
		  }
	  
	}

	
  }


