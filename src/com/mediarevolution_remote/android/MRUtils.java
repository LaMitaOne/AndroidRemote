package com.mediarevolution_remote.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;


public class MRUtils {	
	
    public MRUtils()
    {
    }

    public static final int UNCONSTRAINED = -1;
    public interface Defs {
        public final static int OPEN_URL = 0;
        public final static int SETTINGS = 1;
        public final static int CLOSEACTIVITY = 2;
        public final static int GETURL = 3; 
        public final static int SENDWOL = 4;
        public final static int STARTREMOTE = 5;
        public final static int STARTEREMOTENEXT = 6;
        public final static int CHILD_MENU_BASE = 7; // this should be the last item
    }
    public static boolean mDebugMode = false;       //--------------------DEBUGMODE schreibt mrlog.txt direkt auf /mnt/sdcard
    public static ArrayList<String> ReceivedList = new ArrayList<String>();	
    static boolean getBooleanPref(Context context, String name, boolean def) {
    	SharedPreferences prefs =
    		context.getSharedPreferences("com.mediarevolution.android_preferences", Context.MODE_PRIVATE);
    	return prefs.getBoolean(name, def);
    }

    
    public static String extractFileNameFromString(String fullFileName){
        return fullFileName.substring(fullFileName.lastIndexOf("/")+1, fullFileName.length() );
    }
    
    public static String extractFileNameFromStringWithoutExtension(String fullFileName){
        return fullFileName.substring(0, fullFileName.lastIndexOf(".") );
    }

    public static String extractFileNameFromStringX(String fullFileName){
        return fullFileName.substring(fullFileName.lastIndexOf("\\")+1, fullFileName.length() );
    }
    
	public static void appendLog(String text) //if (MusicUtils.mDebugMode == true) { MRUtils.appendLog("bitmapWithReflection ok "); } 
	{       
	   File logFile = new File("/mnt/sdcard/MRRemote-log.txt");
	   if (!logFile.exists())
	   {
	      try
	      {
	         logFile.createNewFile();
	      } 
	      catch (IOException e)
	      {
	         e.printStackTrace();
	      }
	   }
	   try
	   {
	      //BufferedWriter for performance, true to set append to file flag
	      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));  //true alles speichern false nur letztes...
	      buf.append(text);
	      buf.newLine();
	      buf.close();
	   }
	   catch (IOException e)
	   {
	      e.printStackTrace();
	   }
	}
    
}