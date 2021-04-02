package org.ace.ws.cargo.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpUtility {

	public static String requestMessage(Object obj) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(obj);
	}

	public static String doWithUrl(String urlStr, Object object) throws Exception {
		StringBuffer sb = new StringBuffer();
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		String requestMessage = HttpUtility.requestMessage(object);
		OutputStream outputStream = conn.getOutputStream();
		outputStream.write(requestMessage.getBytes());
		outputStream.flush();
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			sb.append(conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		conn.disconnect();
		return sb.toString();
	}
}
