package com.arif19.noticemanagement;


import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arif19.noticemanagement.modal.PendingPostItem;
import com.arif19.noticemanagement.recycleview.AddGroupMemberAdapter;
import com.arif19.noticemanagement.recycleview.PendingPostAdapter;
import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;
import com.arif19.noticemanagement.user.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class view_pending_group_post extends AppCompatActivity{

    private Toolbar customActionBar; // Use the correct Toolbar class
    ImageButton backButtonProfile;
    TextView actionBarTitleProfile;
    TextView group_name;

    String groupId="-1";
    String group_title="";
    String groupAdmin="0";

    RecyclerView recyclerViewGroupPost;
    private com.arif19.noticemanagement.recycleview.PendingPostAdapter PendingPostAdapter;
    private List<PendingPostItem> pendingPostItems;
    private ArrayList<String> selectedPostId;

    AppCompatButton addPendingPost;
    AppCompatButton deletePendingPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_profile);
        setContentView(R.layout.activity_view_pending_group_post);


        /// for back  page
        customActionBar = findViewById(R.id.custom_action_bar_for_profile);
        actionBarTitleProfile=customActionBar.findViewById(R.id.actionBarTitleProfile);
        backButtonProfile=customActionBar.findViewById(R.id.backButtonProfile);
        actionBarTitleProfile.setText(UserName.userName);


        backButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view_pending_group_post.this, all_group.class);
                startActivity(intent);
                // finish();
            }
        });

        deletePendingPost = findViewById(R.id.deletePendingPost);
        addPendingPost = findViewById(R.id.addPendingPost);

        /// finding intent value ///groupId
        group_name = findViewById(R.id.group_name);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("groupId")) {
            groupId = intent.getStringExtra("groupId");
            group_title = intent.getStringExtra("groupName");
            groupAdmin = intent.getStringExtra("groupAdmin");
            group_name.setText(group_title);

            String userId= UserId.userId;
            String userRole= UserRole.userRole;


            if(!userId.equals(groupAdmin)){
                if(!userRole.equals("3")){
                    addPendingPost.setVisibility(View.GONE);
                }
            }
        }else{
            Intent iiiii = new Intent(view_pending_group_post.this, all_group.class);
            startActivity(iiiii);
        }
        
        
        


        /// for set the group post
        
        recyclerViewGroupPost = findViewById(R.id.recyclerViewGroupPost);
        recyclerViewGroupPost.setLayoutManager(new LinearLayoutManager(this));

        pendingPostItems = new ArrayList<>();
        PendingPostAdapter = new PendingPostAdapter(this, pendingPostItems);
        recyclerViewGroupPost.setAdapter(PendingPostAdapter);


        ////finding postId 
        selectedPostId = new ArrayList<>();

        PendingPostAdapter.setOnAddMemberClickListener(new AddGroupMemberAdapter.OnAddMemberClickListener() {
            @Override
            public void onAddMemberClick(String postId) {
                // Handle the button click with memberId here
                 Toast.makeText(view_pending_group_post.this, "Add button clicked for memberId: " + postId, Toast.LENGTH_SHORT).show();
                if (selectedPostId.contains(postId)) {
                    // ID exists, remove it
                    selectedPostId.remove(postId);
                } else {
                    // ID doesn't exist, add it
                    selectedPostId.add(postId);
                }
            }
        });


        addPendingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataForAddPost(selectedPostId);
            }
        });


        deletePendingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataForDeletePost(selectedPostId);
            }
        });

        // Fetch data from the API
        fetchPostData();
    }

    /// finding post

    private void addDataToPostPage(JSONObject item) {
        PendingPostItem item_val = new PendingPostItem();
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

            // Set the parsed data to PendingPostItem object
            item_val.setAvatarImage(user_profile);
            item_val.setReporterName(name);
            item_val.setPostText(post_text);
            item_val.setImageUrls(imageUrls);
            item_val.setVideoUrl(video_url);
            item_val.setPostId(id);


            // Add the PendingPostItem to the list
            pendingPostItems.add(item_val);

            // Notify adapter for the data change
            PendingPostAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fetchPostData() {
        String userId = UserId.userId;
        int isAdmin = 0;
        if (userId.equals("0")) {
            return;
        }
        if(groupAdmin.equals(userId)){
            isAdmin=1;
        }

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
            jsonData.put("groupId", groupId);
            jsonData.put("isAdmin", isAdmin);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = rootUrl + "VDB/find_group_pending_post.php";
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
                                Toast.makeText(view_pending_group_post.this, "No Post Found !", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view_pending_group_post.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void sendDataForAddPost(ArrayList<String> selectedPostId) {
        String userId = UserId.userId;
        if (userId.equals("0")) {
            return;
        }
        if (groupId.equals("-1")) {
            return;
        }

        if(selectedPostId.size()==0){
            Toast.makeText(view_pending_group_post.this, "Select Post First", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
            // Add the selectedMemberId list to the JSON data
            JSONArray selectedPostIdArray = new JSONArray(selectedPostId);
            jsonData.put("selectedPostId", selectedPostIdArray);
            jsonData.put("groupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = rootUrl + "VDB/admin_aprove_post.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");


                            if (status.equals("success")) {
                                Toast.makeText(view_pending_group_post.this, message, Toast.LENGTH_LONG).show();

                                Intent iiiiii = new Intent(view_pending_group_post.this, group_post.class);
                                iiiiii.putExtra("groupId",groupId);
                                startActivity(iiiiii);

                            } else {
                                Toast.makeText(view_pending_group_post.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view_pending_group_post.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void sendDataForDeletePost(ArrayList<String> selectedPostId) {
        String userId = UserId.userId;
        if (userId.equals("0")) {
            return;
        }
        if (groupId.equals("-1")) {
            return;
        }

        if(selectedPostId.size()==0){
            Toast.makeText(view_pending_group_post.this, "Select Post First", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jsonData = new JSONObject();
        try {

            // Add the selectedMemberId list to the JSON data
            JSONArray selectedPostIdArray = new JSONArray(selectedPostId);
            jsonData.put("selectedPostId", selectedPostIdArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = rootUrl + "VDB/delete_post.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");


                            if (status.equals("success")) {
                                Toast.makeText(view_pending_group_post.this, message, Toast.LENGTH_LONG).show();

                                Intent iiiiii = new Intent(view_pending_group_post.this, group_post.class);
                                iiiiii.putExtra("groupId",groupId);
                                startActivity(iiiiii);

                            } else {
                                Toast.makeText(view_pending_group_post.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view_pending_group_post.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}