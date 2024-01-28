package com.arif19.noticemanagement;




import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class add_post extends AppCompatActivity {

    private Toolbar customActionBar; // Use the correct Toolbar class
    private ImageButton backButton;

    Button addPhotos;
    Button removePhotos;
    Button addVideos;
    Button addPost;
    EditText postText;
    ImageButton backButtonProfile;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressDialog sending;
    private ProgressDialog sending_video;
    private ProgressDialog sending_image;
    TextView actionBarTitleProfile;
    String date_time_for_image;


    private static final int REQUEST_CODE_PICK_VIDEO = 101;
    private static final int PERMISSION_REQUEST_STORAGE = 102;
    private static final String SERVER_URL =rootUrl+"VDB/uploadfile.php"; // Replace with your server URL

    private Button selectVideoButton;
    private Button uploadVideoButton;
    private Uri selectedVideoUri;
    private VideoView videoView;
    private MediaController mediaController;

    ArrayList<Bitmap> dataPhotos = new ArrayList<>();
    // Add code to fetch Bitmaps from MediaStore and add them to the 'data' ArrayList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_profile);
        setContentView(R.layout.activity_add_post);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        date_time_for_image = dateFormat.format(calendar.getTime());



        /// for back profile page
        customActionBar = findViewById(R.id.custom_action_bar_for_profile);
        actionBarTitleProfile=customActionBar.findViewById(R.id.actionBarTitleProfile);
        backButtonProfile=customActionBar.findViewById(R.id.backButtonProfile);

        actionBarTitleProfile.setText(UserName.userName);

        backButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_post.this, user_profile.class);
                startActivity(intent);
               // finish();
            }
        });

        /// for adding photos

        postText = (EditText) findViewById(R.id.postText);
        addPhotos = (Button) findViewById(R.id.addPhotos);
        addPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        /// for remove post

        removePhotos = (Button) findViewById(R.id.removePhotos);
        removePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteImageFromDatabase();

                dataPhotos.clear();
                RecyclerView recyclerView = findViewById(R.id.recyclerViewPhotos);
                com.arif19.noticemanagement.recycleview.post_photos adapter = new com.arif19.noticemanagement.recycleview.post_photos(dataPhotos);
                recyclerView.setAdapter(adapter);


            }
        });

        /// for adding post

        addPost = (Button) findViewById(R.id.addPost);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(add_post.this);
                View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
                builder.setView(customLayout);

                TextView dialogTitle = customLayout.findViewById(R.id.dialogTitle);
                TextView dialogMessage = customLayout.findViewById(R.id.dialogMessage);

                dialogTitle.setText("Alert");
                dialogMessage.setText("Are You Want to Add This Post!");

                Button btnOk = customLayout.findViewById(R.id.btnOk);
                Button btnCancel = customLayout.findViewById(R.id.btnCancel);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btnOk.setOnClickListener(v -> {
                    // Perform actions for OK button click
                    sendDataBack();
                    alertDialog.dismiss();
                });

                btnCancel.setOnClickListener(v -> alertDialog.dismiss());
            }
        });


        //// for video upload
        selectVideoButton = findViewById(R.id.selectVideoButton);
        videoView = findViewById(R.id.videoView);

        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
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

        /// for set the image in recycle view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                dataPhotos.add(bitmap);

                RecyclerView recyclerView = findViewById(R.id.recyclerViewPhotos);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                com.arif19.noticemanagement.recycleview.post_photos adapter = new com.arif19.noticemanagement.recycleview.post_photos(dataPhotos);
                recyclerView.setAdapter(adapter);

                //// call a function for send image to the server
                sending_image = ProgressDialog.show(add_post.this, "Please wait...", "Adding Image", false, false);


                sendImageToServer(bitmap,date_time_for_image);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /// for showing the selected video
        if (requestCode == REQUEST_CODE_PICK_VIDEO && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            showSelectedVideo();

            //// create new object for sending videos file to the server

            if (selectedVideoUri != null) {
                // Start the video upload task
               // new VideoUploadTask().execute(selectedVideoUri);

                String videoId=date_time_for_image+ UserId.userId;

                sending_video = ProgressDialog.show(add_post.this, "Please wait...", "Adding Video", false, false);


                new VideoUploadTask().execute(videoId, selectedVideoUri.toString());
            } else {
                Toast.makeText(add_post.this, "Please select a video first.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void sendDataBack() {
        String postText_S = postText.getText().toString().trim();
        String userId = UserId.userId;

        sending = ProgressDialog.show(add_post.this, "Please wait...", "Adding Post", false, false);

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("post_text", postText_S);
            jsonData.put("user_id", userId);
            jsonData.put("image_id", date_time_for_image);
            jsonData.put("video_id", date_time_for_image);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl = rootUrl+"VDB/user/add_post.php";

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         sending.dismiss();
                         //Toast.makeText(add_post.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(add_post.this, user_profile.class);
                        startActivity(intent);
                        finish();
                        int postId=-1;
                        try {
                            String post_id = response.getString("post_id");
                            postId = Integer.parseInt(post_id); // Convert String to int
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sending.dismiss();
                        Toast.makeText(add_post.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    private void deleteImageFromDatabase() {

        String userId = UserId.userId;

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("user_id", userId);
            jsonData.put("image_id", date_time_for_image);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl = rootUrl+"VDB/user/delete_image_file.php";

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Toast.makeText(add_post.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(add_post.this, LoginActivity.class);
//                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(add_post.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void sendImageToServer(Bitmap bitmap,String postId) {
        //sending = ProgressDialog.show(this, "Please wait...", "", false, false);

        // Define your API URL

        String apiUrl = rootUrl+"VDB/upload_post_image.php";

        // Convert the image to a Base64-encoded string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Create the request body

        String requestBody = "image=" + Uri.encode(encodedImage) + "&post_id=" + postId+"&user_id="+ UserId.userId;

        // Toast.makeText(add_post.this, "Post is "+postId, Toast.LENGTH_LONG).show();

        // Create a request using StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                response -> {
                    // sending.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        sending_image.dismiss();

                        // Toast.makeText(add_post.this, message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(add_post.this, "Error parsing JSON response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // sending.dismiss();
                    Toast.makeText(add_post.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

       // callback.onImageUploadedSuccessfully();
    }


    //// all below code for upload and play video

    private  void  showSelectedVideo(){
        // Set the selected video URI to the VideoView
        videoView.setVideoURI(selectedVideoUri);

        // Create an instance of MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        // Set MediaController to the VideoView
        videoView.setMediaController(mediaController);

        // Start playing the video
        videoView.start();
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        } else {
            openVideoPicker();
        }
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openVideoPicker();
            } else {
                Toast.makeText(this, "Permission denied. Cannot select a video.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private class VideoUploadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String videoId = params[0];
            Uri videoUri = Uri.parse(params[1]); // Assuming the video URI is passed as the second parameter
            File videoFile = createTemporaryFile(videoUri);

            if (videoFile == null) {
                return "Failed to create temporary video file.";
            }

            try {
                FileInputStream fileInputStream = new FileInputStream(videoFile);
                URL url = new URL(SERVER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set connection properties
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                String boundary = "*****";
                String lineEnd = "\r\n";
                String twoHyphens = "--";

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

                // Add video_id parameter to the request
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"video_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(videoId); // Write the video_id value
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"video\"; filename=\"" + videoFile.getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // Create a buffer for reading the video file
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Close streams
                fileInputStream.close();
                dos.flush();
                dos.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    sending_video.dismiss();
                    return "Video Uploaded successfully";
                } else {
                    return "Upload failed with response code " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Upload failed with exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(add_post.this, result, Toast.LENGTH_SHORT).show();
        }
    }


    private File createTemporaryFile(Uri uri) {
        try {
            File cacheDir = getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File videoFile = File.createTempFile("video", null, cacheDir);
            FileOutputStream out = new FileOutputStream(videoFile);
            InputStream in = getContentResolver().openInputStream(uri);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();

            return videoFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}






