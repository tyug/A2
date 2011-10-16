package com.cs456.a2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.widget.TextView;

public class SocketClient extends SocketBase {
	
	private TextView clientView;

	public SocketClient(TextView clientView) {
		this.clientView = clientView;
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		BufferedReader in = null;
		BufferedWriter out = null;
		Socket socket = null;
		try {
			isRunning = true;
			
			socket = new Socket("192.168.2.8", SOCKET_PORT);
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					clientView.setText("Socket connected");
				}
			});
			
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			PrintWriter pw = new PrintWriter(out);
			
			String line = null;
			
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
			
			if(line != null) {
				pw.println(SEND_FILE_LIST_MESSAGE);
				pw.flush();
				
				while (true) {
					
					line = in.readLine();
					
					if(END_FILE_LIST_MESSAGE.equals(line)) {
						pw.println(EXIT_MESSAGE);
						pw.flush();
						break;
					}
					else if(line == null) break;
					else {
						fileList += line + "\n";
					}
				}
				
				line = in.readLine();
			}
			
			final String test = fileList;
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					clientView.setText(test);
				}
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			isRunning = false;
			try {
				if(in != null) in.close();
				if(out != null) out.close();
				if(socket != null) socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}
		return null;
	}
	
}
