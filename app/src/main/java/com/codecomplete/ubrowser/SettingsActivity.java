package com.codecomplete.ubrowser;

import android.app.*;
import android.os.*;
import android.content.*;
import android.widget.*;
import android.view.*;
import android.widget.CompoundButton.*;

public class SettingsActivity extends Activity implements OnCheckedChangeListener
  {

	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	Switch theme, overview, javascript, plugins, incog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	  {
		// TODO: Implement this method
		super.onCreate (savedInstanceState);
		prefs = getSharedPreferences ("savewebdata", MODE_PRIVATE);

		if (prefs.getBoolean ("DarkTheme", false)) {
			setTheme (R.style.DarkTheme);
		  }
		setContentView (R.layout.settings);
		
		editor = prefs.edit ();

		theme = (Switch) findViewById (R.id.themeSwitch);
		overview = (Switch) findViewById (R.id.overviewSwitch);
		javascript = (Switch) findViewById (R.id.javascriptSwitch);
		plugins = (Switch) findViewById (R.id.pluginsSwitch);
		incog= (Switch) findViewById(R.id.incoghistorySwitch);
		
		setState ();

		//switch1
		theme.setOnCheckedChangeListener (this);
		//switch2
	    overview.setOnCheckedChangeListener (this);
		//switch3
		javascript.setOnCheckedChangeListener (this);
		//switch4
		plugins.setOnCheckedChangeListener (this);
		//switch5
		incog.setOnCheckedChangeListener (this);
	  }

	public void setState()
	  {
		theme.setChecked (prefs.getBoolean ("DarkTheme", false));
		overview.setChecked (prefs.getBoolean ("Overview", true));
		javascript.setChecked (prefs.getBoolean ("Javascript", true));
		plugins.setChecked (prefs.getBoolean ("Plugins", true));
		incog.setChecked (prefs.getBoolean ("incogHistory", false));
	  }

	@Override
	public void onCheckedChanged(CompoundButton p1, boolean p2)
	  {
		// TODO: Implement this method
		switch (p1.getId ()) {

			case R.id.themeSwitch:
			  editor.putBoolean ("DarkTheme", p2);
			  new AlertDialog.Builder (this).setTitle ("Theme Changed").
				setMessage ("The app must be restarted for this change to apply.").
				show ();
			  break;

			case R.id.overviewSwitch:
			  editor.putBoolean ("Overview", p2);
			  break;

			case R.id.javascriptSwitch:
			  editor.putBoolean ("Javascript", p2);
			  new AlertDialog.Builder (this).setTitle ("Disabling Javascript").
				setMessage ("If javascript is disabled ,websites that use javascript wont function properly if at all. Some app functions will not work!")
				.show ();
			  break;

			case R.id.pluginsSwitch:
			  editor.putBoolean ("Plugins", p2);
			  break;
			  
			case R.id.incoghistorySwitch:
			  editor.putBoolean ("incogHistory", p2);
			  break;
		  }
	  }

	@Override
	protected void onStop()
	  {
		// TODO: Implement this method
		super.onStop ();
		editor.putBoolean ("DarkTheme", theme.isChecked());
		editor.putBoolean ("Overview", overview.isChecked());
		editor.putBoolean ("Javascript", javascript.isChecked());
		editor.putBoolean ("Plugins", plugins.isChecked());
		editor.putBoolean ("incogHistory", incog.isChecked());
		editor.commit ();
	  }

  }
