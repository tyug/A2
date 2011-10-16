package com.cs456.a2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FileListActivity extends ListActivity {
	private SocketClient clientSocket = null;
	private Map<String,String> MACIPMap = null;
	
	public void onCreate(Bundle b) {
		super.onCreate(b);
		b = this.getIntent().getExtras(); 
		// Create an ArrayAdapter, that will actually make the Strings
		// appear in the ListView
		ArrayList<String> val = b.getStringArrayList("values");
		ArrayList<String> key = b.getStringArrayList("keys");
		MACIPMap = new HashMap<String,String>();
		//creating the mapping for future to remember where the connections are connected
		for (int i =0; i < val.size();i++) {
			MACIPMap.put(key.get(i), val.get(i));
		}
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, key));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position);
		if(!clientSocket.isConnected()) {
    			clientSocket.execute(MACIPMap.get(o.toString()));
    	}
	}
}
