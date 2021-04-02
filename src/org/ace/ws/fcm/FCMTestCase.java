package org.ace.ws.fcm;

import java.util.Date;

import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class FCMTestCase {

	public static void main(String[] args) throws JSONException {
		JSONObject noti = new JSONObject();
		noti.put("title", "Hello TTL");
		noti.put("body", new Date());

		JSONObject data = new JSONObject();
		data.put("id", "PNOTI001001000000000102022018");
		data.put("sound", "default");
		// Method to send Push Notification
		FcmServer.send_FCM_Notification(noti, data, 60);
	}

}
