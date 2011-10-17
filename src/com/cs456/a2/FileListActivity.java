package com.cs456.a2;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;

/***
 * Activity for going to a new page and displaying the files retrieved
 * from the chosen MAC address
 * @author Janson
 *
 */
public class FileListActivity extends ListActivity {	
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.filelist);
		
		//get the value passed from deviceComm
		b = this.getIntent().getExtras(); 
		
		// appear in the ListView as the keys
		ArrayList<String> files = b.getStringArrayList("files");
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files));
		
		EditText statusText = (EditText)findViewById(R.id.fileListStatus);
		
		if (files.isEmpty())
		{
			statusText.setText("There were no files found");
		} else {
			if(files.size() == 1) {
				statusText.setText("There was " + files.size() + " file found");
			}
			else {
				statusText.setText("There were " + files.size() + " files found");
			}
			
		}
		
		
	}
}

