package com.mediarevolution_remote.android;

import com.mediarevolution_remote.android.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class MusicSettingsActivity extends PreferenceActivity {

	static final String KEY_LOGGING_ACTIVE = "logging_active";
	static final String KEY_TITLE_ACTIVE = "title_active";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        addPreferencesFromResource(R.xml.settings);
      
	}
   

}
