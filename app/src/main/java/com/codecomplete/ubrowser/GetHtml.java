package com.codecomplete.ubrowser;

import android.os.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import android.util.*;

public class GetHtml
  {
	public void GetHtml()
	  {

	  }

	public String getHtml(String urlToRead)
	  {
		String html = null;

		try {
			html = new MyTask ().execute (urlToRead).get ();
		  }
		catch (ExecutionException e) {
			e.printStackTrace ();
		  }
		catch (InterruptedException e) {
			e.printStackTrace ();
		  }

		return html;
	  }

	public class MyTask extends AsyncTask<String, Void, String>
	  {

		@Override
		protected String doInBackground(String[] p1)
		  {
			// TODO: Implement this method
			String content = "";
			URL url; 
			InputStream is = null; 
			BufferedReader br; 
			String line; 
			try { 
				url = new URL (p1[0]); 
				is = url.openStream (); // throws an IOException 
				br = new BufferedReader (new InputStreamReader (is)); 

				while ((line = br.readLine ()) != null) { 
					System.out.println (line);
					content+=line+"\n";
				  } 
			  } 
			catch (MalformedURLException e) {
				e.printStackTrace (); 
			  }
			catch (IOException e) {
				e.printStackTrace (); 
			  } 
			finally { 
				try { 
					if (is != null) is.close (); 
				  }
				catch (IOException e) {
				    e.printStackTrace();
				  } 
			  } 

			Log.d("HTMLContent", content);
			return content;
		  }

	  }


  }


