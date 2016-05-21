package com.codecomplete.ubrowser;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.codecomplete.ubrowser.*;
import android.view.View.*;

public class FloatingBubbleService extends Service
  {
	WindowManager windowmanager;
	ImageView icon;
	
	@Override
	public IBinder onBind(Intent p1)
	  {
		// TODO: Implement this method
		return null;
	  }

	@Override
	public void onCreate()
	  {
		// TODO: Implement this method
		super.onCreate ();
		
		final WindowManager.LayoutParams lp=new WindowManager.LayoutParams(
		  50, 50,
		  WindowManager.LayoutParams.TYPE_PHONE,
		  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
		  PixelFormat.TRANSLUCENT);
		lp.gravity = Gravity.CENTER | Gravity.LEFT;

		icon = new ImageView(this);
		icon.setImageResource(R.drawable.icon);
		icon.setOnClickListener (new OnClickListener (){

			  @Override
			  public void onClick(View p1)
				{
				  // TODO: Implement this method
				  startActivity(new Intent("android.intent.action.OTG"));
				}
			});
		
		windowmanager.addView(icon,lp);
	  }
	  
}
