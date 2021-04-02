package org.ace.ws.fcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.ace.insurance.common.ErrorCode;
import org.ace.java.component.SystemException;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class FcmServer {

	final static private String FCM_URL = "https://fcm.googleapis.com/fcm/send";

	final static private String topic = "/topics/com_tat_ggi_noti";

	final static private String server_key = "AAAA1siUIuE:APA91bFU5jJaZCaaCTaiQpbxyVV5uYSo89nhK55ryJkBbnsS8ybUDt06EY4lHaO9eSuBTtJXQOeqXk6SXBTURCSuspvfvEJ28Jw4y_L8_e_kVGyt_FKFT2shiClfxLTtm3ocDPaddvKc";

	/**
	 * Method to send push notification to Android FireBased Cloud messaging
	 * Server.
	 * 
	 * @param tokenId
	 *            Generated and provided from Android Client Developer
	 * @param server_key
	 *            Key which is Generated in FCM Server
	 * @param message
	 *            which contains actual information.
	 */

	public static void send_FCM_Notification(JSONObject noti, JSONObject customData, int timeToLive) {

		try {

			// Create URL instance.
			URL url = new URL(FCM_URL);

			// create connection.

			HttpURLConnection conn;

			conn = (HttpURLConnection) url.openConnection();

			conn.setUseCaches(false);

			conn.setDoInput(true);

			conn.setDoOutput(true);

			// set method as POST or GET

			conn.setRequestMethod("POST");

			// Specify Message Format

			conn.setRequestProperty("Content-Type", "application/json");

			// pass FCM server key

			conn.setRequestProperty("Authorization", "key=" + server_key);

			// Create JSON Object & pass value

			JSONObject json = new JSONObject();

			json.put("to", topic.trim());

			json.put("notification", noti);

			json.put("data", customData);

			if (timeToLive > 0) {
				json.put("time_to_live", timeToLive);
			}

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(json.toString());
			wr.flush();
			int status = 0;

			if (null != conn) {
				status = conn.getResponseCode();
			}

			if (status != 0) {

				if (status == 200) {
					// SUCCESS message
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					System.out.println(json.toString());
					System.out.println("Mobile Notification Response : " + reader.readLine());
				} else if (status == 401) {
					// client side error
					System.out.println("Notification Response : Topic : " + topic + " Error occurred :");
					throw new SystemException(ErrorCode.FCM_CLIENT_SIDE_ERROR, "");
				} else if (status == 501) {
					// server side error
					System.out.println("Notification Response : [ errorCode=ServerError ] Topic : " + topic);
					throw new SystemException(ErrorCode.FCM_SERVER_SIDE_ERROR, "");
				} else if (status == 503) {
					// server side error
					System.out.println("Notification Response : FCM Service is Unavailable  Topic : " + topic);
					throw new SystemException(ErrorCode.FCM_SERVER_UNAVAILABLE, "FCM Service is unavailable.", "");
				}
			}

		} catch (

		MalformedURLException mlfexception) {
			// Protocol Error
			System.out.println("Error occurred while sending push Notification!.." + mlfexception.getMessage());
			throw new SystemException(ErrorCode.FCM_PUSH_NOTIFICATION_ERROR, "");
		} catch (IOException mlfexception) {
			// URL problem
			System.out.println("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
			throw new SystemException(ErrorCode.FCM_URL_PATTERN_ERROR, "");
		} catch (JSONException jsonexception) {
			// Message format error
			System.out.println("Message Format, Error occurred while sending push Notification!.." + jsonexception.getMessage());
			throw new SystemException(ErrorCode.FCM_MESSAGE_FORMAT_ERROR, "");
		}
	}
}
