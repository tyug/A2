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

public class SocketServer extends SocketBase {

	private File sdCardRoot = Environment.getExternalStorageDirectory();
	
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
		try {
			isRunning = true;
			server = new ServerSocket();

			server.setReuseAddress(true);

			handler.post(new Runnable() {

				@Override
				public void run() {
					serverView.setText("Listening: " + getLocalIpAddress());
				}
			});

			server.bind(new InetSocketAddress(SOCKET_PORT));

			client = server.accept();

			handler.post(new Runnable() {

				@Override
				public void run() {
					serverView.setText("Server is done");
				}
			});

			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream()));

			PrintWriter pw = new PrintWriter(out);

			String line = null;

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

				if (line == null) {
					break;
				}

				if (START_MESSAGE.equals(line)) {
					pw.println(START_MESSAGE);
					pw.flush();
					break;
				}
			}

			if (line != null) {
				while (true) {
					line = in.readLine();

					if (SEND_FILE_LIST_MESSAGE.equals(line)) {
						pw.println(FileListing
								.getSortedFileListString(sdCardRoot));
						pw.println(END_FILE_LIST_MESSAGE);
						pw.flush();
					} else if (EXIT_MESSAGE.equals(line)) {
						pw.println(EXIT_MESSAGE);
						pw.flush();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			isRunning = false;
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (client != null)
					client.close();
				if (server != null)
					server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
