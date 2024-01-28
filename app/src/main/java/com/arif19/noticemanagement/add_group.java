package com.arif19.noticemanagement;



import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arif19.noticemanagement.spiner.CustomSpinnerAdapterAddGroup;
import com.arif19.noticemanagement.spiner.CustomSpinnerItemAddGroup;
import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class add_group extends AppCompatActivity {

    String jsonString_all_user;
    Spinner spinner;
    AutoCompleteTextView autoCompleteTextView;

    ImageView uploadProfile;
    ImageButton camera;
    private static final int PICK_IMAGE_REQUEST = 1;
    String image_id="";
    TextInputEditText group_name;
    AppCompatButton add_group_btn;


    private ProgressDialog sending;

    private Toolbar customActionBar; // Use the correct Toolbar class
    ImageButton backButtonProfile;
    TextView actionBarTitleProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_profile);
        setContentView(R.layout.activity_add_group);



        /// for handle Action bar

        /// for back profile page
        customActionBar = findViewById(R.id.custom_action_bar_for_profile);
        actionBarTitleProfile=customActionBar.findViewById(R.id.actionBarTitleProfile);
        backButtonProfile=customActionBar.findViewById(R.id.backButtonProfile);

        actionBarTitleProfile.setText(UserName.userName);

        backButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_group.this, PostActivity.class);
                startActivity(intent);
                 finish();
            }
        });
        //////



         spinner = findViewById(R.id.spinner);
         autoCompleteTextView = findViewById(R.id.autoCompleteTextViewSearchAdmin);

         findAllUser();

        // In your activity's onCreate or appropriate method


        camera = (ImageButton) findViewById(R.id.camera);
        uploadProfile = (ImageView) findViewById(R.id.group_photo);
        group_name = (TextInputEditText) findViewById(R.id.name);
        add_group_btn = (AppCompatButton) findViewById(R.id.add_group_btn);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        add_group_btn.setOnClickListener(new View.OnClickListener() {
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
        String apiUrl = rootUrl+"VDB/upload_group_image.php";

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

                        Toast.makeText(add_group.this, message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(add_group.this, "Error parsing JSON response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    sending.dismiss();
                    Toast.makeText(add_group.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void setSpinnerData(){
        try {
            // Parse the JSON array
            JSONArray jsonArray = new JSONArray(jsonString_all_user);

            // Create a custom adapter from the JSON array
            CustomSpinnerAdapterAddGroup adapter = new CustomSpinnerAdapterAddGroup(this, jsonArray);

            // Set up the AutoCompleteTextView

            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CustomSpinnerItemAddGroup selectedItem = (CustomSpinnerItemAddGroup) parent.getItemAtPosition(position);
                    // Handle the selected item (you can use selectedItem.getText() or selectedItem.getImageUrl())
                    Toast.makeText(add_group.this, "Selected: " + selectedItem.getText(), Toast.LENGTH_SHORT).show();
                    autoCompleteTextView.setText(selectedItem.getText());
                }
            });

            // Set up the Spinner

            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Handle the selected item
                    CustomSpinnerItemAddGroup selectedItem = (CustomSpinnerItemAddGroup) parentView.getItemAtPosition(position);
                    // You can use selectedItem.getText() or selectedItem.getImageUrl()
                    Toast.makeText(add_group.this, "Selected: " + selectedItem.getText(), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing here
                }
            });


            // Add text change listener to AutoCompleteTextView for searching
            autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Not used
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Filter the adapter based on the entered text
                    adapter.getFilter().filter(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Not used
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findAllUser() {
        String userId = UserId.userId;
        if (userId.equals("0")) {
            return;
        }

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = rootUrl + "VDB/user/find_all_user.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");

                            if (status.equals("Success")) {
                                 jsonString_all_user = response.getString("data");
                                 setSpinnerData();
                            } else {
                                Toast.makeText(add_group.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(add_group.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void sendDataBack() {
        String s_group_name = group_name.getText().toString().trim();
        String s_admin_id = autoCompleteTextView.getText().toString().trim();

        if (s_group_name.equals("")) {
            Toast.makeText(this, "Please Add Group Name", Toast.LENGTH_LONG).show();
            return;
        }
        if (s_admin_id.equals("")) {
            Toast.makeText(this, "Please Select Group Admin", Toast.LENGTH_LONG).show();
            return;
        }

        sending = ProgressDialog.show(this, "Please wait...", "", false, false);

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("group_title", s_group_name);
            jsonData.put("group_pic",image_id);
            jsonData.put("group_admin", s_admin_id);
            jsonData.put("user_id", UserId.userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl = rootUrl+"VDB/user/add_new_group.php";

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sending.dismiss();
                        Toast.makeText(add_group.this, "Group Added Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(add_group.this, PostActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sending.dismiss();
                        Toast.makeText(add_group.this, "Group Added Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(add_group.this, PostActivity.class);
                        startActivity(intent);
                        finish();
//                        Toast.makeText(add_group.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
