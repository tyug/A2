package com.cs456.a2;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/***
 * Scan and searches for BT devices
 * 
 * @author Kuen
 *
 */
public class Search {
	//Members
	private BluetoothAdapter btAdapter;
	private ArrayList<String> btMACList;
	private Boolean isRunning;
	
	//singleton related
	private static Search singleton = null;
	
	public static Search getInstance() {
		if(singleton == null) {
			singleton = new Search();
		}
		
		return singleton;
	}

	//get the list
	public ArrayList<String> getBtMACList() {
		return btMACList;
	}

	private Search() {
		btAdapter=getBtAdapter();
		btMACList= new ArrayList<String>();
		setIsRunning(false);
	}

	//get the adapter
	public BluetoothAdapter getBtAdapter() {
		return BluetoothAdapter.getDefaultAdapter();
	}
	
	//Starts the scan
	public void startScan() {
		Logger.getInstance().log(" SCAN_BEGIN");
		btMACList.clear();
		if (btAdapter.isDiscovering())
			btAdapter.cancelDiscovery();
		setIsRunning(true);
		btAdapter.startDiscovery();
	}
	
	//Stops the scan
	public void stopScan() {
		setIsRunning(false);
		btAdapter.cancelDiscovery();
	}
	
	public Boolean getIsRunning() {
		return isRunning;
	}

	private void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}

	public BroadcastReceiver getmReceiver() {
		return mReceiver;
	}

	// The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevic	e object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btMACList.add(device.getAddress());
                Logger.getInstance().log(" SCAN_DETECT: "+device.getAddress());
                
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setIsRunning(false);
                Logger.getInstance().log(" SCAN_END");
            }
        }
    };
}
