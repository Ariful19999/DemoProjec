package com.arif19.noticemanagement;



import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class user_profile extends AppCompatActivity {

    private Toolbar customActionBar; // Use the correct Toolbar class
    private ImageButton backButton;

    ImageView profileImage;
    de.hdodenhof.circleimageview.CircleImageView profilePic;

    TextView work_institute;
    TextView edu_institute;
    TextView blood_group;
    TextView name;
    Button editProfile;
    Button addPost;
    TextView actionBarTitleProfile;
    ImageButton backButtonProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_profile);
        setContentView(R.layout.activity_user_profile);

        profileImage = findViewById(R.id.profileImage);
        profilePic = findViewById(R.id.profilePic);
        work_institute = findViewById(R.id.work_institute);
        edu_institute = findViewById(R.id.edu_institute);
        blood_group = findViewById(R.id.blood_group);
        name = findViewById(R.id.name);
        name = findViewById(R.id.name);
        editProfile = findViewById(R.id.editProfile);
        addPost = findViewById(R.id.addPost);

        /// set toolbar value
        customActionBar = findViewById(R.id.custom_action_bar_for_profile);
        actionBarTitleProfile=customActionBar.findViewById(R.id.actionBarTitleProfile);
        backButtonProfile=customActionBar.findViewById(R.id.backButtonProfile);


        backButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_profile.this, PostActivity.class);
                startActivity(intent);
                // finish();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_profile.this, edit_profile.class); // Replace NextActivity with your actual activity
                startActivity(intent);
               // finish();
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_profile.this, add_post.class); // Replace NextActivity with your actual activity
                startActivity(intent);
                finish();
            }
        });


        FindData();


    }

    private void FindData() {

        String userId= UserId.userId;

        if (userId.equals("0")) {
            return;
        }

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl = rootUrl+"notice_management/user/find_data.php";

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String name_s = response.getString("name");
                            String email_s = response.getString("email");
                            String edu_institute_s = response.getString("edu_institute");
                            String work_institute_s = response.getString("work_institute");
                            String blood_group_s = response.getString("blood_group");
                            String date_of_birth_s = response.getString("date_of_birth");
                            String profile_path_s = response.getString("profile_path");


                            if (status.equals("Success")) {

                                actionBarTitleProfile.setText(name_s);
                                UserName.userName=name_s;

                                work_institute.setText(work_institute_s);
                                edu_institute.setText(edu_institute_s);
                                blood_group.setText(blood_group_s);
                                name.setText(name_s);

                                /// setting image
                                String profileImageUrl = rootUrl+profile_path_s; // Replace with the actual URL

                                // Using Picasso library for image loading and caching
                                Picasso.get()
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.profile)
                                        .into(profileImage);

                                Picasso.get()
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.profile)
                                        .into(profilePic);


                            } else {
                                // Handle other status cases if needed
                                Toast.makeText(user_profile.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(user_profile.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}