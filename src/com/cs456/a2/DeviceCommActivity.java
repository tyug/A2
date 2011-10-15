package com.cs456.a2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class DeviceCommActivity extends Activity {
	
	// Member fields
    private Search search;
    private ArrayList<String> wlanList;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        search = new Search();
        search.startScan();
        wlanList = new ArrayList<String>();
        
     // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(search.getmReceiver(), filter);
        
     // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(search.getmReceiver(), filter);
        
        TextView output = (TextView) findViewById(R.id.output);
        output.setText("Nothing here yet");
    }
    
    /***
     * Testing code
     */
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
            //Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (search.getBtAdapter() != null) {
            search.stopScan();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(search.getmReceiver());
    }
    
    public void startSearch(View view) {
    	TextView tv = (TextView) findViewById(R.id.output2);
    	tv.setText("");
    	TextView tv2 = (TextView) findViewById(R.id.output);
    	tv2.setText("Starting Scan");
    	search.startScan();
    	
    	ArrayList<String> mList = search.getBtMACList();
    	for (String txt: mList) {
    		tv.append(txt);
    	}
    	tv2.setText("Finished Scan");
    	
    	TextView tv3 = (TextView) findViewById(R.id.output3);
    	tv3.setText("Starting query");
    	for (int i = 0; i < mList.size();i++) {
    		String[] content = BLSQuery.query(mList.get(i));
    		tv.append("\n-------\n");
    		if (content == null) {
    			//ERROR::
    			return;
    		}
    		
    		for (int j = 0; j<content.length; j++) {
    			if (content[j]!=null) {
    				tv.append(content[j]);
    				if (j==1)
    					wlanList.add(content[j]);
    			}
    		}
    	}
    	
    	tv3.setText("done query");
    }
    
    public void startServer(View view) {
    	TextView tv = (TextView) findViewById(R.id.startServer);
    	tv.setText("starting");
    	TextView tv2 = (TextView) findViewById(R.id.server);
    	new MyServer().execute(tv,tv2);
    }
 
    //Singleton
    private class MyServer extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {
			ServerSocket server;
			try {
				server = new ServerSocket();
			
				server.setReuseAddress(true);
				//server.setTimeout(0);
				String ip = getLocalIpAddress();
				TextView tv = (TextView) arg0[0];
				tv.setText("Listening: "+ip);
				
				server.bind(new InetSocketAddress(62009));
	
				Socket client = null;
				
				//this command blocks
				client = server.accept();
				tv.setText("ACCEPTED WIN");
				
				/*BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String line = null;
				while ((line=in.readLine())!=null) {
					
				}
					*/	
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
    	
    }
    
    public void startConnect(View view) {
    	Socket socket = null;    	
    	
		try {
			if (wlanList.size()>0) {
				//Not sure how this is gonna work
				socket = new Socket(wlanList.get(0), 62009);
			  	OutputStream out = socket.getOutputStream();       
		    	PrintWriter output = new PrintWriter(out);         
		    	
		    	TextView tv = (TextView) findViewById(R.id.connection);
		    	tv.setText("Starting sending of data");
		    	output.println("Hello from Android");
		    	out.flush();
		    	out.close();
		    	tv.setText("Data sent to PC");            
		
		    	socket.close();                                    
		    	tv.setText("Socket closed");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
    }
 
}