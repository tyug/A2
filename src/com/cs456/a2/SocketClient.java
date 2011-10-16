package com.cs456.a2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.widget.EditText;
import android.widget.TextView;

/**
 * A socket used to connect to a server socket on another device
 * @author Janson
 *
 */
public class SocketClient extends SocketBase {
	
	private EditText statusText;

	public SocketClient(EditText clientView) {
		this.statusText = clientView;
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		BufferedReader in = null;
		BufferedWriter out = null;
		Socket socket = null;
		PrintWriter pw = null;
		try {
			isConnected = true; // We are connected
			
			// Create a new socket
			socket = new Socket((String)arg0[0], SOCKET_PORT);
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					statusText.setText("Socket connected");
				}
			});
			
			// Create the input and output stream readers
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			// Create a PrintWriter to use for the output stream
			pw = new PrintWriter(out);
			
			String line = null;
			
			// Send the start connection message, and wait for the start message response
			while (true) {
				pw.println(START_MESSAGE);
				pw.flush();
				
				line = in.readLine();
				
				if(line == null) {
					break;
				}
				
				if(START_MESSAGE.equals(line)) {
					break;
				}
			}
			
			String fileList = "XXX";
			
			// Ensure we didn't get here because the socket was closed
			if(line != null) {
				//Send a request for the file list message
				pw.println(SEND_FILE_LIST_MESSAGE);
				pw.flush();
				
				// Continue to read in the file list until the end of file list message is received
				while (true) {
					
					line = in.readLine();
					
					// When we get the end of file list message, send the connection exit message
					if(END_FILE_LIST_MESSAGE.equals(line)) {
						pw.println(EXIT_MESSAGE);
						pw.flush();
						break;
					}
					// The socket closed, break out of this
					else if(line == null) break;
					// Append the new line of the file list data to a string
					else {
						fileList += line + "\n";
					}
				}
				
				// Wait for the exit successful message to be received.  We don't really care what this is as long as we get something
				line = in.readLine();
			}
			
			final String test = fileList;
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					statusText.setText(test);
				}
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		// This is always run
		finally {
			// We are no longer connected
			isConnected = false;
			try {
				// Close everything that was opened
				if(in != null) in.close();
				if(out != null) out.close();
				if(pw != null) pw.close();
				if(socket != null) socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}
		return null;
	}
	
}
