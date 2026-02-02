package com.mediarevolution_remote.android;

import java.io.IOException;

import com.mediarevolution_remote.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class WOLSettingsActivity extends Activity {

	public EditText macField;
	public EditText ipField;
	public TextView btnStart, btnClose;
	private Context con;
	private SharedPreferences mPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.wakeonlan);   
        mPreferences = getSharedPreferences("com.mediarevolution.android_states", Context.MODE_PRIVATE);
        macField = (EditText)findViewById(R.id.macedit);
        ipField = (EditText)findViewById(R.id.ipedit);
        btnClose = (TextView)findViewById(R.id.ButtonCloseWOL);
        btnClose.setOnClickListener(btnCloseListener);
        btnStart = (TextView)findViewById(R.id.ButtonWake);  
        btnStart.setOnClickListener(btnStartListener);
        String macStr = "";
		String ipStr = "";  
		con = getApplicationContext();
		if (mPreferences.contains("WAKE_MACADRESS")) {
			macStr = mPreferences.getString("VIDEO_ASPECT", "");
			macField.setText(macStr);
		}		
		if (mPreferences.contains("WAKE_IPADRESS")) {
			ipStr = mPreferences.getString("VIDEO_ASPECT", "");
			ipField.setText(ipStr);
		}		
	}
	
	
	private void showToast(String Caption) {
		Toast.makeText(this, Caption, Toast.LENGTH_SHORT).show();
    }   
	
	
    private OnClickListener btnCloseListener = new OnClickListener()
    {
		public void onClick(View v)
    	{
			finish();
    	}
    };	
   
    private OnClickListener btnStartListener = new OnClickListener()
    {
		public void onClick(View v)
    	{
			String macStr = macField.getText().toString();
			String ipStr = ipField.getText().toString();  
			try {
				WOLPowerManager.sendWOL(macStr, con);    					
			} catch (IOException e) { 
				showToast("Error: WakeONLan Message not sent");
			}
			try {
				WOLPowerManager.sendWOL(ipStr, macStr, 2);  
			} catch (IOException e) { 
				showToast("Error: WakeONLan Message not sent");
			}				
			Editor ed = mPreferences.edit();
        	ed.putString("WAKE_MACADRESS", macStr);
        	ed.putString("WAKE_IPADRESS", ipStr);
        	ed.commit();   
    	}
    };
    
}
