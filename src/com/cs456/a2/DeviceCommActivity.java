package com.cs456.a2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class DeviceCommActivity extends Activity {
	
	// Member fields
    private Search search;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        search = new Search();
        search.startScan();
        
     // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(search.getmReceiver(), filter);
        
     // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(search.getmReceiver(), filter);
        
        TextView output = (TextView) findViewById(R.id.output);
        output.setText("Nothing here yet");
        
        
        //////////////////////////
        ////// This is TEMP //////
        //////////////////////////
        TextView janson = (TextView)findViewById(R.id.jansonTest);
        File sdCardRoot = Environment.getExternalStorageDirectory(); // Get the directory path to the SD card
        janson.setText(FileListing.getSortedFileListString(sdCardRoot));
        
        Button jansonbtn = (Button)findViewById(R.id.jansonbtn);
        jansonbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logger.getInstance().log("Pressed the Button");				
			}
		});
        
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
    
   
    // Creates a dialog box stating there was an error, and prints the text which the calling code provides it
    private void handleError(String error) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(error);
		alertBuilder.setCancelable(false);
		
		alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		AlertDialog alert = alertBuilder.create();
		alert.setTitle("Error!");
		alert.show();
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
    		tv.append("-------\n");
    		for (int j = 0; j<content.length; j++) {
    			if (content[j]!=null)
    				tv.append(content[j]);
    		}
    	}
    	
    	tv3.setText("done query");
    }
 
}