package com.codecomplete.ubrowser;
import android.os.*;
import android.util.*;
import java.io.*;
import java.net.*;

class InternetConnectionAvail extends AsyncTask<URL, Integer, Boolean>
{
	@Override
	protected Boolean doInBackground(URL[] p1)
	{
		boolean connected;
		try
		{ 
			URL url = new URL("http://www.google.com"); 
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection(); 
			urlConnect.setConnectTimeout(1000); 
			urlConnect.getContent(); 
			Log.d("InternetStat", "Connection established."); 
			connected=true;
		} 
		catch (NullPointerException np)
		{
			Log.d("InternetStat", "Connection not established.");
			np.printStackTrace(); 
			connected=false;
		}
		catch (IOException io)
		{
			Log.d("InternetStat", "Connection not established."); 
			io.printStackTrace(); 
			connected=false;
		}
		
		return connected;
	}
}
