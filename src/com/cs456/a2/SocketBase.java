package com.cs456.a2;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.AsyncTask;
import android.os.Handler;

public abstract class SocketBase extends AsyncTask {
	protected final int SOCKET_PORT = 62009;
	protected final String EXIT_MESSAGE = "Goodbye Cruel World";
	protected final String START_MESSAGE = "Hello Cruel World";
	protected final String SEND_FILE_LIST_MESSAGE = "File List Cruel World";
	protected final String END_FILE_LIST_MESSAGE = "No More File List Cruel World";
	
	protected Handler handler = new Handler();
	
	protected boolean isRunning = false;
	
	@Override
	protected abstract Object doInBackground(Object... params);
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
	
	/***
     * Testing code
     */
    protected String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }
}
