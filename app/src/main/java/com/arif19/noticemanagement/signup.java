package com.arif19.noticemanagement;


import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arif19.noticemanagement.Config.SignUp;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;



public class signup extends AppCompatActivity implements View.OnClickListener {

    EditText name;
    EditText email;
    EditText pass;
    EditText edu_institute;
    EditText work_institute;
    EditText blood_group;
    EditText date_of_birth;
    ImageView uploadProfile;
    ImageButton camera;
    AppCompatButton create_acc;

    String image_id="";


    private ProgressDialog sending;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_login);
        setContentView(R.layout.activity_signup);



         // In your activity's onCreate or appropriate method

         camera = (ImageButton) findViewById(R.id.camera);
         camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
         });


        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);
        edu_institute = (EditText) findViewById(R.id.edu_institute);
        work_institute = (EditText) findViewById(R.id.work_institute);
        blood_group = (EditText) findViewById(R.id.blood_group);
        date_of_birth = (EditText) findViewById(R.id.date_of_birth);
        create_acc=(AppCompatButton) findViewById(R.id.create_acc);
        uploadProfile= findViewById(R.id.uploadProfile);
//        create_acc.setOnClickListener(this);

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataBack();
            }
        });


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                uploadProfile.setImageBitmap(bitmap);
                // Send the image to the server
                sendImageToServer(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendImageToServer(Bitmap bitmap) {
        sending = ProgressDialog.show(this, "Please wait...", "", false, false);

        // Define your API URL
        String apiUrl = rootUrl+"notice_management/upload_image.php";

        // Convert the image to a Base64-encoded string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Create the request body
        String requestBody = "image=" + Uri.encode(encodedImage);


        // Create a request using StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                response -> {
                    sending.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        image_id = jsonResponse.getString("image_id");

                        Toast.makeText(signup.this, message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(signup.this, "Error parsing JSON response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    sending.dismiss();
                    Toast.makeText(signup.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            public byte[] getBody() {
                return requestBody.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void sendDataBack() {
        String s_name = name.getText().toString().trim();
        String s_email = email.getText().toString().trim();
        String s_pass = pass.getText().toString().trim();
        String s_edu_institute = edu_institute.getText().toString().trim();
        String s_work_institute = work_institute.getText().toString().trim();
        String s_blood_group = blood_group.getText().toString().trim();
        String s_date_of_birth = date_of_birth.getText().toString().trim();


        if (s_name.equals("") || s_email.equals("") || s_pass.equals("")) {
            Toast.makeText(this, "Please Fill Up All Fields", Toast.LENGTH_LONG).show();
            return;
        }

        sending = ProgressDialog.show(this, "Please wait...", "", false, false);

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("name", s_name);
            jsonData.put("email", s_email);
            jsonData.put("pass", s_pass);
            jsonData.put("edu_institute", s_edu_institute);
            jsonData.put("work_institute", s_work_institute);
            jsonData.put("blood_group", s_blood_group);
            jsonData.put("date_of_birth", s_date_of_birth);
            jsonData.put("image_id", image_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl = SignUp.DATA_URL;

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sending.dismiss();
                        Toast.makeText(signup.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(signup.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(signup.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }



    @Override
    public void onClick(View v) {
        //sendDataBack();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}