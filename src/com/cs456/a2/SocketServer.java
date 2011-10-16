package com.cs456.a2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Environment;
import android.widget.TextView;

/**
 * A socket used as a server to receive requests from other devices
 * @author Janson
 *
 */
public class SocketServer extends SocketBase {

	private File sdCardRoot = Environment.getExternalStorageDirectory(); // The SD card root path
	
	private TextView serverView;
	
	public SocketServer(TextView serverView) {
		this.serverView = serverView;
	}

	@Override
	protected Object doInBackground(Object... arg0) {
		ServerSocket server = null;
		Socket client = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		PrintWriter pw = null;
		
		try {
			isConnected = true; // We are connected
			
			// Create a new server socket
			server = new ServerSocket();

			// This address can be reused
			server.setReuseAddress(true);

			handler.post(new Runnable() {

				@Override
				public void run() {
					serverView.setText("Listening: " + getLocalIpAddress());
				}
			});

			// Bind the socket to the listening port
			server.bind(new InetSocketAddress(SOCKET_PORT));

			// Listen and wait for a client socket connection to occur
			client = server.accept();

			handler.post(new Runnable() {

				@Override
				public void run() {
					serverView.setText("Server is done");
				}
			});

			// Create the input and output stream reader and writer
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

			// Create a PrintWriter to write to the output stream
			pw = new PrintWriter(out);

			String line = null;

			// Wait for a connection message to be sent.  Once received send one back
			while (true) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						serverView.setText("Pre Read Line");
					}
				});
				line = in.readLine();
				handler.post(new Runnable() {

					@Override
					public void run() {
						serverView.setText("Read Line");
					}
				});
				
				// If the socket is closed, break
				if (line == null) {
					break;
				}

				// When we receive the start message, send the start message back and then break out
				if (START_MESSAGE.equals(line)) {
					pw.println(START_MESSAGE);
					pw.flush();
					break;
				}
				
				// Otherwise loop until we receive the start message
			}

			// Only execute this if the socket hasn't been closed yet
			if (line != null) {
				
				// Wait for the send file list message, and then send the file list!
				while (true) {
					line = in.readLine();

					// If the client wants the file list, send the file list followed by the end of file list message
					if (SEND_FILE_LIST_MESSAGE.equals(line)) {
						pw.println(FileListing
								.getSortedFileListString(sdCardRoot));
						pw.println(END_FILE_LIST_MESSAGE);
						pw.flush();
					}
					// If the client wants to exit, send back the exit message and then break
					else if (EXIT_MESSAGE.equals(line)) {
						pw.println(EXIT_MESSAGE);
						pw.flush();
						break;
					}
					// If the socket is closed, exit
					else if (line == null) break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// This is always run
		finally {
			isConnected = false; // We are no longer connected
			try {
				// Close everything that was opened
				if (in != null)	in.close();
				if (out != null) out.close();
				if (pw != null) pw.close();
				if (client != null)	client.close();
				if (server != null)	server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
