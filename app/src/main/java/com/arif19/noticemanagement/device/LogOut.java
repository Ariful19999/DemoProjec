package com.arif19.noticemanagement.device;



import static com.arif19.noticemanagement.device.DeviceImei.IMEI;
import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arif19.noticemanagement.LoginActivity;
import com.arif19.noticemanagement.PostActivity;
import com.arif19.noticemanagement.user.UserId;


import org.json.JSONException;
import org.json.JSONObject;

public class LogOut {
    public static ProgressDialog sending;
    public static void destroySaveSession(Activity page) {

        if (IMEI.equals("-1")) {
            Toast.makeText(page, "Unable to Save Session", Toast.LENGTH_LONG).show();
            return;
        }

        sending = ProgressDialog.show(page, "Please wait...", "", false, false);

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("imei", IMEI);
            jsonData.put("user_id",  UserId.userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl =rootUrl+"VDB/logout.php";

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sending.dismiss();
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if (status.equals("Success")) {

                                Toast.makeText(page, message, Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(page, LoginActivity.class);
                                page.startActivity(intent);

                                page.finish(); // Optional: finish the current activity if you don't want to come back to it

                            } else {
                                Toast.makeText(page, "Unable to Save Session", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(page, PostActivity.class);
                                page.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(page, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(page);
        requestQueue.add(jsonObjectRequest);
    }
}
