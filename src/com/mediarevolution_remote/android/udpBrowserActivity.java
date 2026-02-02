package com.mediarevolution_remote.android;
        
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import com.mediarevolution_remote.android.MRUtils;
        import com.mediarevolution_remote.android.R;
        import android.app.ListActivity;
        import android.content.Context;
        import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
        import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
        import android.view.View;
        import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

        public class udpBrowserActivity extends ListActivity 
            implements View.OnCreateContextMenuListener, MRUtils.Defs   {        	
      	
        	Context mContext = null;
        	public EditText edtField;
        	public Button btnStart;
        	public Boolean SpeechInitialized = false;
        	//private TextToSpeech tts;
        	private ArrayList<String> array_sort= new ArrayList<String>();
        	int textlength=0;
        	
            public udpBrowserActivity()
            {
            }                	
      	
        	
        	@Override              
        	public void onCreate(Bundle icicle) {  
        		super.onCreate(icicle);
        		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
                requestWindowFeature(Window.FEATURE_NO_TITLE);  
                setContentView(R.layout.udpmedia_picker_activity); 
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
                mContext = udpBrowserActivity.this;   
      		    getListView().setOnItemClickListener(mItemClickListener); 
      		    getListView().setTextFilterEnabled(true);
      		    final ListActivity activity = (ListActivity) mContext;   
      		    array_sort.addAll(MRUtils.ReceivedList);
				activity.setListAdapter(new udpFolderArrayAdapter(mContext ,R.layout.udpselect_folder_list_item, array_sort));   
      		    getListView().setOnCreateContextMenuListener(this); 
    		    edtField = (EditText)findViewById(R.id.filtertext);
    		    btnStart = (Button)findViewById(R.id.btnStart);
    		    btnStart.setOnClickListener(btnStartListener);
    		    //tts = new TextToSpeech(this, new OnInitListener() {
               //     public void onInit(int arg0) {
               //     	SpeechInitialized = true;
               //     }
               // });
        	}
        	
            private OnClickListener btnStartListener = new OnClickListener()
            {
        		public void onClick(View v)
            	{
        			textlength = edtField.getText().length();
        			if (textlength <= 0) {
        				array_sort.clear();
        				array_sort.addAll(MRUtils.ReceivedList);
        			} else {
    				array_sort.clear();
    				int x = -1;
    				for (int i = 0; i < MRUtils.ReceivedList.size(); i++)
    				{
    				if (textlength <= MRUtils.ReceivedList.get(i).length())
    				{	
    				String str = MRUtils.ReceivedList.get(i).toLowerCase();	
    				x = str.indexOf(edtField.getText().toString().toLowerCase());
                        if (x > -1) {
                        	array_sort.add(MRUtils.ReceivedList.get(i).toString());                  
                        }
    				  } 
    				}
        			}
    				getListView().setAdapter(new udpFolderArrayAdapter(mContext ,R.layout.udpselect_folder_list_item, array_sort));  
            	}
            };
        	
            public void sendRemoteCommand(String MessageToSend){
                try
                {		
                	WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiinfo= wifiManager.getConnectionInfo();
                    int ip_adress=wifiinfo.getIpAddress();
                    String newip= String.format("%d.%d.%d.%d", (ip_adress & 0xff),
                          (ip_adress>>8 & 0xff),
                          (ip_adress>>16 & 0xff),
                          (255));
                   InetAddress serv_addr= InetAddress.getByName(newip);//REMOTE_IPADRESS.toString().trim());       
                   int port = 1777; 
                   DatagramSocket sock = new DatagramSocket();
                   sock.setReuseAddress(true);
                   sock.setBroadcast(true);
                   byte [] buf = (MessageToSend).getBytes();
                   DatagramPacket pack= new DatagramPacket(buf, buf.length, serv_addr, port);
                   sock.send(pack);       
                   for (int i=0; i<buf.length;i++) buf[i]=0;  
                   sock.close();
                   showToast("command transmitted");
                }
                catch (Exception e){
                   Log.d("UDP", "Error: "+e);
                }
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
        	
        	private final AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        		@Override
        		public void onItemClick(final AdapterView<?> arg0, final View arg1, final int arg2, final long arg3) {  			
        			sendRemoteCommand("MRSTARTTITLE" + array_sort.get(arg2));  
        		}
        	};
        	
        	
            @Override
        	protected void onPause() {  
        	    super.onPause();
        	}
                         
  
			@Override
            public void onResume() {
                super.onResume();
            }  
			

		    @Override 
		    public void onSaveInstanceState(Bundle outState)   {		
		    	super.onSaveInstanceState(outState); 		    	
		    }			
		
        	
            @Override
            public void onDestroy() {			
                super.onDestroy();    

            }  
                        
            
            public void init(Cursor c) {    

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
                }                
                return super.onOptionsItemSelected(item);                
            } 
            
            
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                super.onCreateOptionsMenu(menu);  
                menu.add(0, SETTINGS, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);    
                return true;
            }
            
            @Override
            public boolean onPrepareOptionsMenu(Menu menu) {
                return super.onPrepareOptionsMenu(menu);
            }
            
            

            @Override
      	    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
      	        menu.add(0, STARTREMOTE, 0, "Start title");   
      	        menu.add(0, STARTEREMOTENEXT, 0, "Start title after actual running");   
      	    }


 	    @Override
 	    public boolean onContextItemSelected(MenuItem item) {
 	    	AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
 	        switch (item.getItemId()) {
 	            case STARTREMOTE: {
 	            	 sendRemoteCommand("MRSTARTTITLE" + array_sort.get(mi.position));  
 	                 return true;
 	            } 
 	            case STARTEREMOTENEXT: {
 	            	 sendRemoteCommand("MRNEXTTITLE" + array_sort.get(mi.position));  
	                 return true;
	            }    
 	        }
 	        return super.onContextItemSelected(item);
 	    }
            

        }   //ENDE

        
