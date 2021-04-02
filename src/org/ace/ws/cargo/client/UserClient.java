package org.ace.ws.cargo.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.ace.insurance.user.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserClient {
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://localhost:4040/ggip/cargows/addUser");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		User user = new User();
		Gson gson = new GsonBuilder().create();
		String requestMessage = gson.toJson(user);
		OutputStream outputStream = conn.getOutputStream();
		System.out.println("Request Message");
		System.out.println(requestMessage);
		System.out.println("=================================");
		outputStream.write(requestMessage.getBytes());
		outputStream.flush();
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}
		System.out.println("Response Message");
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
		conn.disconnect();
	}

}
