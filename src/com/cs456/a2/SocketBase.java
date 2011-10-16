package com.cs456.a2;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.AsyncTask;
import android.os.Handler;

/**
 * An abstract base class containing common methods and data for both the server and client sockets
 * @author Janson
 *
 */
public abstract class SocketBase extends AsyncTask {
	protected final int SOCKET_PORT = 62009; // The port we are connecting on
	protected final String EXIT_MESSAGE = "Goodbye Cruel World"; // The exit message for the protocol
	protected final String START_MESSAGE = "Hello Cruel World"; // The initialization message for the protocol
	protected final String SEND_FILE_LIST_MESSAGE = "File List Cruel World"; // The request for the file list message for the protocol
	protected final String END_FILE_LIST_MESSAGE = "No More File List Cruel World"; // A message indicating the transfer of the file list is complete
	
	protected Handler handler = new Handler(); // A common handler used to post handle new runnables
	
	protected boolean isConnected = false; // Indicates if the socket is connected
	
	@Override
	protected abstract Object doInBackground(Object... params); // Must be implemented in derived classes
	
	/**
	 * Returns whether the socket is connected
	 * @return True if the socket is connected
	 */
	public boolean isConnected() {
		return this.isConnected;
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
