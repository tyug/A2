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
	private BluetoothAdapter btAdapter;
	private ArrayList<String> btMACList;
	
	private static Search singleton = null;
	
	public static Search getInstance() {
		if(singleton == null) {
			singleton = new Search();
		}
		
		return singleton;
	}

	public ArrayList<String> getBtMACList() {
		return btMACList;
	}

	private Boolean isRunning;
	
	private Search() {
		btAdapter=getBtAdapter();
		btMACList= new ArrayList<String>();
		setIsRunning(false);
	}
	
	public BluetoothAdapter getBtAdapter() {
		return BluetoothAdapter.getDefaultAdapter();
	}
	
	public void startScan() {
		btMACList.clear();
		if (btAdapter.isDiscovering())
			btAdapter.cancelDiscovery();
		setIsRunning(true);
		btAdapter.startDiscovery();
	}
	
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
                
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setIsRunning(false);
            }
        }
    };
}
