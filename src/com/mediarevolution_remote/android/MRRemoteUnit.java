    package com.mediarevolution_remote.android;

    import android.app.Activity;
import android.content.Context;
    import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
    import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.net.wifi.WifiInfo;
    import android.net.wifi.WifiManager;
    import com.mediarevolution_remote.android.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;

    public class MRRemoteUnit extends Activity 
    implements View.OnCreateContextMenuListener, MRUtils.Defs  {
    	
		public MRRemoteUnit()
        {
        }    

	   private SharedPreferences mPreferences;

	   private WifiManager wifiManager;	
	   //private TextToSpeech tts;
	   private Boolean wifiWasOFF, NoWifi;
	   private Thread tReceiver = null; 
	   private String server_ip;
	   public Bitmap GlobalPicture;
	   public String GlobalReceivedMessage;	      
	   public Boolean mFullscreen = true;	 
	   public Integer TimeOut = 0;
	   public Boolean ReceivingPlaylist = false;
	   public Boolean Playlistloaded = false;
	   public Boolean notTerminated = true;
	   public Boolean possibleCover = false;
	   public Boolean SpeechInitialized = false;
       public TextView mLabelTitle;     
       public ImageView RemoteCover; 
       public TextView btnPlay, btnScan, btnForward, btnFastforward, btnStop, btnBackward, btnFastbackward, bntAspect, btnRepeat, btnFolderForward, btnFolderBackward;
       
        @Override
        protected void onCreate(Bundle savedInstanceState)    {
            super.onCreate(savedInstanceState);  
            requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
            requestWindowFeature(Window.FEATURE_NO_TITLE); 
            setContentView(R.layout.mediacontrol);   
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //mFullscreen = (MRUtils.getBooleanPref(this, MusicSettingsActivity.KEY_TITLE_ACTIVE, true));
            //if (mFullscreen == true) {
            	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
            //} else {
            //	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //}            
            mLabelTitle = (TextView)findViewById(R.id.remoteartist);
            RemoteCover = (ImageView)findViewById(R.id.remotecover);
            //RemoteCover.setVisibility(View.INVISIBLE);  
            	
            btnPlay = (TextView)findViewById(R.id.ButtonPlay);            
            btnPlay.setOnClickListener(mPlayClick);    
            btnStop = (TextView)findViewById(R.id.ButtonStop); 
            btnStop.setOnClickListener(mStopClick);
            btnScan = (TextView)findViewById(R.id.ButtonScan);
            btnScan.setOnClickListener(mbtnScanClick);
            btnForward = (TextView)findViewById(R.id.ButtonSkipForward); 
            btnForward.setOnClickListener(mForwardClick);
            btnBackward = (TextView)findViewById(R.id.ButtonSkipBack); 
            btnBackward.setOnClickListener(mBackwardClick);
            btnFastforward = (TextView)findViewById(R.id.ButtonFF); 
            btnFastforward.setOnClickListener(mFastforwardClick);
            btnFastbackward = (TextView)findViewById(R.id.ButtonRew);
            btnFastbackward.setOnClickListener(mFastbackwardClick);
            bntAspect = (TextView)findViewById(R.id.ButtonAspect); 
            bntAspect.setOnClickListener(mAspectClick);
            btnRepeat = (TextView)findViewById(R.id.ButtonRepeat); 
            btnRepeat.setOnClickListener(mRepeatClick);
            btnFolderForward = (TextView)findViewById(R.id.ButtonFolderplus); 
            btnFolderForward.setOnClickListener(mFolderforwardClick);
            btnFolderBackward = (TextView)findViewById(R.id.ButtonFolderminus);
            btnFolderBackward.setOnClickListener(mFolderbackwardClick);   
            mPreferences = getSharedPreferences("com.mediarevolution.android_states", Context.MODE_PRIVATE);
            try {
              wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
              WifiInfo wifiinfo= wifiManager.getConnectionInfo();
              int ip_adress=wifiinfo.getIpAddress();
              String newip= String.format("%d.%d.%d.%d", (ip_adress & 0xff),
                    (ip_adress>>8 & 0xff),
                    (ip_adress>>16 & 0xff),
                    (255));	
              server_ip = newip;
              if(wifiManager.isWifiEnabled()){ 
                 wifiWasOFF = false;
              }else{
            	 showToast("Please Activate Wifi");  
                 wifiWasOFF = true;
                // wifiManager.setWifiEnabled(true);
              }
              NoWifi = false;
            }
            catch (Exception e){
            	NoWifi = true;
            	showToast("No Wifi possible");
            }
            if (wifiWasOFF == false) {
            	StartReceiver("MRGETTITLE");
            	//StartReceiver("MRGETCOVER");            	
            }            
        }  
        
        



        public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
        }
        
    	private void showToast(String Caption) {
    			Toast.makeText(this, Caption, Toast.LENGTH_SHORT).show();
    			//if (SpeechInitialized) tts.speak(Caption, 0, null);
    			//if (mToast == null) {
    			//	Toast.makeText(this, "", Toast.LENGTH_SHORT);
    			//} //else mToast.cancel();
    			//mToast.setText(Caption);
    			//mToast.show();
    	}      
    	
    	
        public static String extractIPAdr(String TEXT){
            return TEXT.substring(TEXT.lastIndexOf("|")+1, TEXT.length() );
        }
          
        public static String extractPCName(String TEXT){
            return TEXT.substring(0, TEXT.lastIndexOf("|") );
        }
        
        
    	public static void appendLog(String text) 
    	{       
    	   File logFile = new File("/mnt/sdcard/MRWPlaylist.txt");
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
    	      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
    	      buf.append(text);
    	      buf.newLine();
    	      buf.close();
    	   }
    	   catch (IOException e)
    	   {
    	      e.printStackTrace();
    	   }
    	}
    	
 
        
        public void StartReceiver(final String Message) {
        	tReceiver = new Thread(new Runnable() {
          	public void run() {
	                    try{
	                        int port = 1778;           
	                        Boolean GotMessage = false;
	                        //InetAddress serv_address = InetAddress.getByName(newip); 
	                        DatagramSocket serversocket = new DatagramSocket(port);//, serv_address); 
	                        serversocket.setReuseAddress(true);
	                        serversocket.setBroadcast(true);
	                        Looper.prepare();
	                        send_btn(Message); 
	                        if (Message.indexOf("MRGETCOVER") > -1) {
	                        	possibleCover = true;
	                       	}      
	                        while (GotMessage == false) { 
	                          try{	
	  	                        byte [] buf = new byte[32768];   
		                        DatagramPacket packet= new DatagramPacket(buf, buf.length);
	                       	    packet.setLength(buf.length);	
	                       	    serversocket.receive(packet);                     	    
	                            //actual Cover	                            
	                            if (possibleCover == true) {	
	                            	possibleCover = false;
	                            	try {
	                            		Bitmap img = null;	                       
		                            	img = BitmapFactory.decodeByteArray(packet.getData(), 0, packet.getLength());
		                            	GlobalPicture = img;
		                            	Thread t = new Thread(){
		                            	    public void run() {
		                            	    	runOnUiThread(new Runnable() {
		                            	            @Override
		                            	            public void run() {
		                            	            	RemoteCover.setImageBitmap(GlobalPicture);  
		                            	            }
		                            	        });
		                            	    }};
		                            	    t.start();  
	    		                   	    GotMessage = true;	
	                            	}
	                            	catch (Exception e){			                    	  
	    		                   	    possibleCover = false;
	    		                   	    GotMessage = true;	
	    		                      }	                            	
	                            }	
	                       	    String ReceivedMessage = new String(packet.getData(), 0, packet.getLength());		                       	    
	        //-------------------------------------------------------------------------------------
	                            //running receivers
	                            //int i = ReceivedMessage.indexOf("MRXVRIPANDID");
	                            //if (i > 0 &&  i < ReceivedMessage.length() - 1) {
	                              //adapter.add(" " + extractPCName(ReceivedMessage.substring(i+1)));
	                              //adapter.notifyDataSetChanged(); 
	                            //}	 	  
	                            //aktueller titel      
	                       	int i = ReceivedMessage.indexOf("MRXVRTITEL");
	                            if (i > -1) {
	                            	GotMessage = true;	
	                            	GlobalReceivedMessage = ReceivedMessage.substring(i+10);
	                            	Thread t = new Thread(){
	                            	    public void run() {
	                            	    	runOnUiThread(new Runnable() {
	                            	            @Override
	                            	            public void run() {	
	                            	              mLabelTitle.setText(GlobalReceivedMessage);
	                            	            if (GlobalReceivedMessage.length() > 5) {
	                            	                 //if (SpeechInitialized) tts.speak("Actual Playing Title: " + GlobalReceivedMessage, 0, null);
	                            	            }
	                            	            }
	                            	        });
	                            	    }};
	                            	    t.start();                               	    
	                            }
	         //-------------------------------------------------------------------------------------  
		                      }              
		                      catch (Exception e){			                    	  
		                   	    possibleCover = false;
		                      }
	                       } //Schleifenende 
	                       serversocket.close();
	                     }              
	                     catch (Exception e){
	                   	   possibleCover = false;
	                     }	  
                   } 
          	});   
  	        tReceiver.setPriority(Thread.MIN_PRIORITY);
  	        tReceiver.start();
        }        
        
 

    public void ReadFile() {
    		try {  
    		  Playlistloaded = false;
    		  MRUtils.ReceivedList.clear();    		    
    		  File dir = Environment.getExternalStorageDirectory();
  	    	  File file = new File(dir, "MRWPlaylist.txt");   	    	  
    		  File xfile = new File(file.toString());
    		  InputStream instream = new FileInputStream(xfile); 
    		  BufferedReader buffreader = new BufferedReader(new InputStreamReader(instream,"ISO-8859-1")); 
    		  String line;
    		  while((line = buffreader.readLine())!= null){
    		     MRUtils.ReceivedList.add(line);  
    		  }    	
    		  instream.close();   
    		  Playlistloaded = true;
    		} catch (java.io.FileNotFoundException e) {
    		} catch (IOException e) {
				e.printStackTrace();
			}
    }

        private OnClickListener mbtnScanClick = new OnClickListener()
        {  		public void onClick(View v)        	{ 
  				    ReadFile();
                    if (Playlistloaded) {
					   Intent intent = new Intent(MRRemoteUnit.this, udpBrowserActivity.class);
					   startActivity(intent); 
                    } else {
                    	showToast("No Playlist found, place the txt file generated from MEDIA Revolution for Windows directly on your SDCARD");
                    	//new DoInBackground().execute();  //Playlist empfangen über wifi
                    }
            }
        };  		    
		
        private OnClickListener mPlayClick = new OnClickListener()
        {  		public void onClick(View v)        	{
    			send_btn("MRPLAY");      	}
        };
        
        private OnClickListener mFolderbackwardClick = new OnClickListener()
        { 		public void onClick(View v)  	{
    			send_btn("MRFOLDERBACKWARD");   	}
        }; 
        
        private OnClickListener mFolderforwardClick = new OnClickListener()
        {  		public void onClick(View v)       	{
    			send_btn("MRFOLDERFORWARD");      	}
        }; 
        
        private OnClickListener mRepeatClick = new OnClickListener()
        {  		public void onClick(View v)        	{
    			send_btn("MRREPEAT");       	}
        };        
        
        private OnClickListener mAspectClick = new OnClickListener()
        {  		public void onClick(View v)        	{
    			send_btn("MRASPECT");       	}
        }; 
        
        private OnClickListener mFastbackwardClick = new OnClickListener()
        {  		public void onClick(View v)        	{
    			send_btn("MRFASTBACKWARD");       	}
        };         
        
        private OnClickListener mFastforwardClick = new OnClickListener()
        {  		public void onClick(View v)        	{
    			send_btn("MRFASTFORWARD");       	}
        };         
        
        private OnClickListener mBackwardClick = new OnClickListener()
        {  		public void onClick(View v)        	{
    			send_btn("MRBACKWARD");       	}
        }; 
        
        private OnClickListener mForwardClick = new OnClickListener()
        {  		public void onClick(View v)        	{
    			send_btn("MRFORWARD");       	}
        };  
        
        private OnClickListener mStopClick = new OnClickListener()
        {  		public void onClick(View v)        	{
    			send_btn("MRSTOP");      	}
        };          
        
      
        public void send_btn(String MessageToSend){
        try
        {     
           InetAddress serv_addr= InetAddress.getByName(server_ip);//server_ip.getText().toString().trim());
           int port = 1777; 
           DatagramSocket sock = new DatagramSocket();
           sock.setReuseAddress(true);
           sock.setBroadcast(true);
           byte [] buf = (MessageToSend).getBytes();
           DatagramPacket pack= new DatagramPacket(buf, buf.length,serv_addr,port);
           sock.send(pack);
           sock.close();
           for (int i=0; i<buf.length;i++) buf[i]=0;          
           showToast("command transmitted");
        }
        catch (Exception e){
           showToast("Can not Send - No Connection to Wifi");	
           Log.d("UDP", "Error: "+e);
        }
        }        

             
        
        @Override
        public void onDestroy() {
        	super.onDestroy();    
        	//if (tts.isSpeaking()) tts.stop();
        	//tts.shutdown();
        	notTerminated = false;
        	if (NoWifi == false) {
        	  if (wifiWasOFF == true) {
                 // wifiManager.setWifiEnabled(false);
                 // showToast("Wifi disabled");
        	  }
        	}       
        	if (tReceiver != null) {
        		tReceiver.interrupt();	
        	}     
        	Editor ed = mPreferences.edit();
        	ed.putString("REMOTE_IPADRESS", server_ip);
        	ed.commit();   
        }        
        
        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            super.onSaveInstanceState(savedInstanceState);
        }   

		@Override
        public void onResume() {
            super.onResume();
          //  mFullscreen = (MRUtils.getBooleanPref(this, MusicSettingsActivity.KEY_TITLE_ACTIVE, false));
          //  if (mFullscreen == true) {
          //  	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
          //  } else {
          //  	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
          //  }
        }  
        
        
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                 case SETTINGS:
                    intent = new Intent();
                    intent.setClass(this, MusicSettingsActivity.class);
                    startActivityForResult(intent, SETTINGS);
                    return true; 
                 case GETURL:
                	 String url = "http://www.in-mediakg.de/software/mediarevolution/mediarevolution.shtml";  
                	 Intent i = new Intent(Intent.ACTION_VIEW);  
                	 i.setData(Uri.parse(url));  
                	 startActivity(i); 
                     return true;
     			case SENDWOL:    				
 					 intent = new Intent();
    	             intent.setClass(this, WOLSettingsActivity.class);
    	             startActivityForResult(intent, SETTINGS);
    	             return true; 
                 case CLOSEACTIVITY:
                     finish();
                     return true;       
            } 
            return super.onOptionsItemSelected(item);
        } 
        
        
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            menu.add(0, SETTINGS, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);
            menu.add(0, CLOSEACTIVITY, 0,  R.string.close_string).setIcon(android.R.drawable.ic_menu_close_clear_cancel); 
            menu.add(0, SENDWOL, 0, "Send WakeOnLAN Message").setIcon(android.R.drawable.ic_menu_preferences);
            menu.add(0, GETURL, 0, "Get MEDIA Revolution for Windows").setIcon(android.R.drawable.ic_menu_preferences);
            return true;
        }
        
        @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
            return super.onPrepareOptionsMenu(menu);
        } 
        
        
        
    }   //ENDE