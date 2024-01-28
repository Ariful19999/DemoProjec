package com.arif19.noticemanagement;



import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;
import static com.arif19.noticemanagement.user.UserRole.userRole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.arif19.noticemanagement.modal.NewsFeedItem;
import com.arif19.noticemanagement.recycleview.NewsFeedAdapter;
import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class group_post extends AppCompatActivity  {

    private Toolbar customActionBar; // Use the correct Toolbar class
    ImageButton backButtonProfile;
    TextView actionBarTitleProfile;
    Button add_member;
    Button view_pending_post;
    FloatingActionButton floatingActionButton_member;
    FloatingActionButton floatingActionButton_add_post;
    String groupId="-1";
    String group_title="";
    String group_admin="-1";

    ImageView group_photo;
    TextView group_name;
    LinearLayout add_member_all_member;
    LinearLayout pending_post_layout;

    RecyclerView recyclerViewGroupPost;
    private NewsFeedAdapter newsFeedAdapter;
    private List<NewsFeedItem> newsFeedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_profile);
        setContentView(R.layout.activity_group_post);

        /// for back  page
        customActionBar = findViewById(R.id.custom_action_bar_for_profile);
        actionBarTitleProfile=customActionBar.findViewById(R.id.actionBarTitleProfile);
        backButtonProfile=customActionBar.findViewById(R.id.backButtonProfile);
        actionBarTitleProfile.setText(UserName.userName);


        backButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(group_post.this, all_group.class);
                startActivity(intent);
                // finish();
            }
        });

        add_member = findViewById(R.id.add_member);
        add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(group_post.this, add_group_member.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", group_title);
                startActivity(intent);
                // finish();
            }
        });

        floatingActionButton_member = findViewById(R.id.floatingActionButton_member);
        TooltipCompat.setTooltipText(floatingActionButton_member, "See Group Member");
        floatingActionButton_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(group_post.this, all_group_member.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", group_title);
                startActivity(intent);
                // finish();
            }
        });

        floatingActionButton_add_post = findViewById(R.id.floatingActionButton_add_post);
        floatingActionButton_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(group_post.this, add_group_post.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", group_title);
                startActivity(intent);
                // finish();
            }
        });

        /// see your pending post

        view_pending_post = findViewById(R.id.view_pending_post);
        view_pending_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(group_post.this, view_pending_group_post.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", group_title);
                intent.putExtra("groupAdmin", group_admin);
                startActivity(intent);
                // finish();
            }
        });


        ////
        add_member_all_member = findViewById(R.id.add_member_all_member);
        pending_post_layout = findViewById(R.id.pending_post_layout);
        group_name = findViewById(R.id.group_name);
        group_photo = findViewById(R.id.group_photo);

        /// finding intent value ///groupId

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("groupId")) {
             groupId = intent.getStringExtra("groupId");
            FindData();
        }else{
            Intent iiiii = new Intent(group_post.this, all_group.class);
            startActivity(iiiii);
        }

        /// for set the group post

        recyclerViewGroupPost = findViewById(R.id.recyclerViewGroupPost);
        recyclerViewGroupPost.setLayoutManager(new LinearLayoutManager(this));

        newsFeedItems = new ArrayList<>();
        newsFeedAdapter = new NewsFeedAdapter(this, newsFeedItems);
        recyclerViewGroupPost.setAdapter(newsFeedAdapter);

        // Fetch data from the API
        fetchPostData();
    }

    /// finding group data

    private void FindData() {

        String userId= UserId.userId;

        if (userId.equals("0")) {
            return;
        }

        // Create a JSONObject to hold the data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
            jsonData.put("groupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Define your API URL
        String apiUrl = rootUrl+"VDB/find_single_group.php";

        // Create a request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                             group_title = response.getString("group_title");
                            String group_pic = response.getString("group_pic");
                            group_admin = response.getString("group_admin");



                            if (status.equals("Success")) {
                                group_name.setText(group_title);

                                if(userRole!="3"){
                                    if(!group_admin.equals(userId)){
                                        add_member_all_member.setVisibility(View.GONE);
//                                        pending_post_layout.setVisibility(View.INVISIBLE);
                                    }
                                }


                                /// setting image
                                String profileImageUrl = rootUrl+group_pic; // Replace with the actual URL

                                // Using Picasso library for image loading and caching
                                Picasso.get()
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.group_avatar)
                                        .error(R.drawable.group_avatar)
                                        .into(group_photo);


                            } else {
                                // Handle other status cases if needed
                                Toast.makeText(group_post.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(group_post.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    /// finding post
    private void addDataToPostPage(JSONObject item) {
        NewsFeedItem item_val = new NewsFeedItem();
        try {
            // Parse individual attributes from the JSON object
            String id = item.getString("id");
            String post_text = item.getString("post_text");
            String post_date = item.getString("post_date");
            String name = item.getString("name");

            String user_profile = "";
            if (!item.isNull("user_profile")) {
                user_profile = rootUrl + item.getString("user_profile");
            }

            String video_url = "";
            if (!item.isNull("video_url")) {
                video_url = rootUrl + item.getString("video_url");
            }

            // Similarly parse 'image_url' array from the JSON object
            JSONArray imageUrlArray = item.getJSONArray("image_url");
            List<String> imageUrls = new ArrayList<>();
            for (int j = 0; j < imageUrlArray.length(); j++) {
                String imageUrl = rootUrl + imageUrlArray.getString(j);
                imageUrls.add(imageUrl);
            }

            // Set the parsed data to NewsFeedItem object

            System.out.println(user_profile);
            item_val.setAvatarImage(user_profile);
            item_val.setReporterName(name);
            item_val.setPostText(post_text);
            item_val.setImageUrls(imageUrls);
            item_val.setVideoUrl(video_url);


            // Add the NewsFeedItem to the list
            newsFeedItems.add(item_val);

            // Notify adapter for the data change
            newsFeedAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchPostData() {
        String userId = UserId.userId;
        if (userId.equals("0")) {
            return;
        }

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
            jsonData.put("groupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = rootUrl + "VDB/find_group_post.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            JSONArray dataArray = response.getJSONArray("data");

                            if (status.equals("Success")) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject item = dataArray.getJSONObject(i);
                                    addDataToPostPage(item);
                                    // Access other properties as needed
                                }
                            } else {
                                Toast.makeText(group_post.this, "No Post Found !", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(group_post.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}