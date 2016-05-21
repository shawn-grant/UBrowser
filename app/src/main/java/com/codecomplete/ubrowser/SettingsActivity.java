package com.codecomplete.ubrowser;

import android.app.*;
import android.os.*;
import android.content.*;

public class SettingsActivity extends Activity
{

	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		SharedPreferences prefs=getSharedPreferences("savewebdata",MODE_PRIVATE);
		editor=prefs.edit();
		
		
	}

	@Override
	protected void onStop()
	{
		// TODO: Implement this method
		super.onStop();
	}
	
}
