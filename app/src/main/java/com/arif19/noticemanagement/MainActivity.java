package com.arif19.noticemanagement;



import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arif19.noticemanagement.device.DeviceImei;
import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;
import com.arif19.noticemanagement.user.UserRole;


import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private static final long DELAY_MILLIS = 3000; // 3 seconds

    private static final int PERMISSION_REQUEST_CODE = 123;
    private ProgressDialog sending;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delay the navigation to another page after 4 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextPage();
                //ipInfo();
            }
        }, DELAY_MILLIS);



    }

//    private void ipInfo(){
//        /// finding device Ip
//
//        String ipAddress = IPAddressHelper.getIPAddress();
//        if (ipAddress != null) {
//            // Use the obtained IP address as needed
//            Toast.makeText(this, "Ip : "+ipAddress, Toast.LENGTH_LONG).show();
//        } else {
//            // Handle the case where no IP address is found
//            Toast.makeText(this, "Unable to retrieve device IP address", Toast.LENGTH_LONG).show();
//        }
//
//    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getIMEI();
            } else {
                // Permission denied
                // Handle accordingly, e.g., show a message or disable functionality
            }
        }
    }

    private void getIMEI() {
        TelephonyManager telephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For Android Oreo and newer
                String imei = telephonyManager.getImei();
               // Log.d("IMEI", "IMEI: " + imei);
              //  Toast.makeText(this, "IMEI : "+imei, Toast.LENGTH_LONG).show();
                DeviceImei.IMEI=imei;
            } else {
                // For older Android versions
                String imei = telephonyManager.getDeviceId();
              //  Toast.makeText(this, "IMEI : "+imei, Toast.LENGTH_LONG).show();
                DeviceImei. IMEI=imei;
            }
        }
    }




    private void navigateToNextPage() {
//        Intent intent = new Intent(this, LoginActivity.class); // Replace NextActivity with your actual activity
//        startActivity(intent);
//        finish(); // Optional: finish the current activity if you don't want to come back to it

        findUserInfo();
    }


    private void findUserInfo() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                // Permission already granted
                getIMEI();
            } else {
                // Request permission
                requestPermission();
            }
        } else {
            // For devices below Android 6.0, no need to request runtime permission
           getIMEI();
        }


        if (DeviceImei.IMEI.equals("-1")) {
            Intent iiiii = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(iiiii);
           finish();
        }

        sending = ProgressDialog.show(this, "Please wait...", "", false, false);

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("imei", DeviceImei.IMEI);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl =rootUrl+"VDB/login_with_session.php";

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sending.dismiss();
                        try {
                            String status = response.getString("status");

                            if (status.equals("Success")) {
                                String role = response.getString("role");
                                String userId = response.getString("userId");
                                String userName = response.getString("userName");

                                /// set for session value
                                UserRole.userRole=role;
                                UserId.userId=userId;
                                UserName.userName=userName;

//                                Intent intent = new Intent(LoginActivity.this, user_profile.class);
//                                startActivity(intent);

                                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                                startActivity(intent);

                                finish(); // Optional: finish the current activity if you don't want to come back to it

                            } else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}