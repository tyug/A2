package com.cs456.a2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Base64;

public class BLSQuery {

	private String macAddress;
	
	public BLSQuery(String macAddress) {
		String mccAddress=macAddress.replaceAll(":", "");
		setMacAddress(mccAddress.toLowerCase());
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public String[] query() {
		String[] returnInfo = null;
		try {
			String btmachash;
			MessageDigest md = null;
			InputStream content = null;
			md = MessageDigest.getInstance("SHA-1");
		
			md.update(macAddress.getBytes());
			btmachash=Base64.encodeToString(md.digest(),0);
			HttpClient httpclient = new DefaultHttpClient();
			String URI = "http://blow.cs.uwaterloo.ca/cgi-bin/bls_query.pl?btmachash="+btmachash.toString();
			URI=URI.replace("\n", "");
			HttpResponse response = httpclient.execute(new HttpGet(URI));
            content = response.getEntity().getContent();	        
	        
            //reading the input
	        BufferedReader rd = new BufferedReader(new InputStreamReader(content));
	        String line;
	        returnInfo = new String[5];
	        int i = 0;
	        while ((line=rd.readLine()) != null) {
	        	returnInfo[i]=line;
	        	i++;
	        }
	        
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			//some random error here
		} catch (ClientProtocolException e) {
            //error= "Upload Failed, Client protocol fail";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
        return returnInfo;
	}

	public static String[] query(String MACAddress) {
		BLSQuery tmp = new BLSQuery(MACAddress);
		return tmp.query();
	}
}
