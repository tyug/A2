package com.cs456.a2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class DeviceCommActivity extends Activity {
	
	// Member fields
    private Search search = Search.getInstance();
    private ArrayList<String> wlanList;
    
    private SocketServer serverSocket = null;
    private SocketClient clientSocket = null;
    
    private EditText statusText;
    private EditText myMACText;
    private EditText myIPText;
    private EditText lastBTScanText;
    private EditText btScanResultsText;
    
    private Button scanBTBtn;
    private Button fileListBtn;
    private Button startStopTranferButton;
    
    private Handler handle = new Handler();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        wlanList = new ArrayList<String>();
        
        statusText = (EditText) findViewById(R.id.statusText);
        myMACText = (EditText) findViewById(R.id.myMacText);
        myIPText = (EditText) findViewById(R.id.myIpText);
        lastBTScanText = (EditText) findViewById(R.id.lastBtScanText);
        btScanResultsText = (EditText) findViewById(R.id.btScanResultsText);
        
        scanBTBtn = (Button) findViewById(R.id.scanBtBtn);
        scanBTBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startSearch();
			}
		});
        
        fileListBtn = (Button) findViewById(R.id.fileListBtn);
        fileListBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startClient();
			}
		});
        
        startStopTranferButton = (Button) findViewById(R.id.startStopTrandferBtn);
        startStopTranferButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startServer();
			}
		});
        
     // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(search.getmReceiver(), filter);
        
     // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(search.getmReceiver(), filter);
        
        myMACText.setText(BluetoothAdapter.getDefaultAdapter().getAddress());
        
        clientSocket = new SocketClient(statusText);
        serverSocket = new SocketServer(statusText);
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
        
		Logger.getInstance().closeLogFile();
    }
    
    public void startSearch() {
    	statusText.setText("Starting Scan");
    	btScanResultsText.setText("");
    	
    	scanBTBtn.setEnabled(false);
    	Date date = new Date(System.currentTimeMillis());
    	SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	lastBTScanText.setText(dt.format(date));
    	search.startScan();
    	
    	Thread thr = new Thread(new Runnable() {
			@Override
			public void run() {
				Boolean searchTemp=true;
				while(searchTemp) {
					try {
						searchTemp = search.getIsRunning();
						Thread.currentThread();
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				StringBuffer var = new StringBuffer();
				ArrayList<String> mList = search.getBtMACList();
		    	for (String txt: mList) {
		    		var.append(txt+"\n");
		    	}	    	
		    	
		    	//Logger stuff here
		    	for (int i = 0; i < mList.size();i++) {
		    		String[] content = BLSQuery.query(mList.get(i));
		    		//btScanResultsText.append("\n-------\n");
		    		if (content == null) {
		    			//ERROR::
		    			return;
		    		}
		    		
		    		for (int j = 0; j<content.length; j++) {
		    			if (content[j]!=null) {
		    				//btScanResultsText.append(content[j]);
		    				if (j==1)
		    					wlanList.add(content[j]);
		    			}
		    		}
		    	}
		    	
		    	final String MACList = var.toString();
		    	handle.post(new Runnable() {
					@Override
					public void run() {
						btScanResultsText.setText(MACList);
						scanBTBtn.setEnabled(true);
						statusText.setText("Finished Scan");
					}
				});
			}
		});
    	thr.start();
    }
    
    public void startServer() {
    	if(!serverSocket.isConnected()) {
    		serverSocket.execute();
    	}
    }
    
    public void startClient() {
    	if(!clientSocket.isConnected()) {
    		clientSocket.execute();
    	}
    }
 
}