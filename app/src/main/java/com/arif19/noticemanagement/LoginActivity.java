package com.arif19.noticemanagement;



import static com.arif19.noticemanagement.device.DeviceImei.IMEI;
import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arif19.noticemanagement.Config.LogIn;
import com.arif19.noticemanagement.device.DeviceImei;
import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;
import com.arif19.noticemanagement.user.UserRole;


import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton signup;
    AppCompatButton login;
    EditText userEmail;
    EditText userPass;



    private ProgressDialog sending;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_login);
        setContentView(R.layout.activity_login);


        signup=findViewById(R.id.create_acc);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii=new Intent(LoginActivity.this,signup.class);
                startActivity(ii);
            }
        });
        userEmail=findViewById(R.id.userEmail);
        userPass=findViewById(R.id.userPass);
        login= findViewById(R.id.button);

        login.setOnClickListener(this);

    }

    /// for finding IMEI

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
                IMEI=imei;
            } else {
                // For older Android versions
                String imei = telephonyManager.getDeviceId();
               // Toast.makeText(this, "IMEI : "+imei, Toast.LENGTH_LONG).show();
                IMEI=imei;
            }
        }
    }

    private void saveSession(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setView(customLayout);

        TextView dialogTitle = customLayout.findViewById(R.id.dialogTitle);
        TextView dialogMessage = customLayout.findViewById(R.id.dialogMessage);

        dialogTitle.setText("Alert");
        dialogMessage.setText("Do you want to save LoginSession!");

        Button btnOk = customLayout.findViewById(R.id.btnOk);
        Button btnCancel = customLayout.findViewById(R.id.btnCancel);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnOk.setOnClickListener(v -> {
            // Perform actions for OK button click
            saveSessionInfoToBack();
            alertDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            alertDialog.dismiss();

            Intent intent = new Intent(LoginActivity.this, user_profile.class);
            startActivity(intent);
            finish();

        });
    }


    private void saveSessionInfoToBack() {


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


        if (IMEI.equals("-1")) {
            Toast.makeText(LoginActivity.this, "Unable to Save Login Session !" , Toast.LENGTH_LONG).show();

            Intent intent = new Intent(LoginActivity.this, PostActivity.class);
            startActivity(intent);
            finish();
        }

        sending = ProgressDialog.show(this, "Please wait...", "", false, false);

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("imei", IMEI);
            jsonData.put("user_id",  UserId.userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl =rootUrl+"notice_management/insert_login_session.php";

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sending.dismiss();
                        try {
                            String status = response.getString("status");

                            if (status.equals("success")) {

                                Toast.makeText(LoginActivity.this, "Successfully Save Session", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(LoginActivity.this, user_profile.class);
                                startActivity(intent);

                                finish(); // Optional: finish the current activity if you don't want to come back to it

                            } else {
                                Toast.makeText(LoginActivity.this, "Unable to Save Session", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, PostActivity.class);
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
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }



    private void sendDataBack() {
        String s_email = userEmail.getText().toString().trim();
        String s_pass = userPass.getText().toString().trim();

        if (s_email.equals("") || s_pass.equals("")) {
            Toast.makeText(this, "Please Fill Up All Fields", Toast.LENGTH_LONG).show();
            return;
        }

        sending = ProgressDialog.show(this, "Please wait...", "", false, false);

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("email", s_email);
            jsonData.put("pass", s_pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl = LogIn.DATA_URL;

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sending.dismiss();
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                            if (status.equals("Success")) {
                                String role = response.getString("role");
                                String userId = response.getString("userId");
                                String userName = response.getString("userName");

                                /// set for session value
                                UserRole.userRole=role;
                                UserId.userId=userId;
                                UserName.userName=userName;

                                saveSession(); /// check user would like to save session or not


//                                Intent intent = new Intent(LoginActivity.this, user_profile.class);
//                                startActivity(intent);

//                                Intent intent = new Intent(LoginActivity.this, PostActivity.class);
//                                startActivity(intent);

                                //finish(); // Optional: finish the current activity if you don't want to come back to it

                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }



    @Override
    public void onClick(View v) {
        sendDataBack();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}