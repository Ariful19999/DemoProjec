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
import com.arif19.noticemanagement.modal.AddGroupMemberItem;
import com.arif19.noticemanagement.recycleview.AddGroupMemberAdapter;
import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class add_group_member extends AppCompatActivity {
    private Toolbar customActionBar; // Use the correct Toolbar class
    ImageButton backButtonProfile;
    TextView actionBarTitleProfile;


    private RecyclerView recyclerView;
    private AddGroupMemberAdapter addGroupMemberAdapter;
    private List<AddGroupMemberItem> addGroupMemberAdapterItems;
    private ArrayList<String> selectedMemberId;
    String groupId="-1";
    String groupName="";
    TextView group_name;
    AppCompatButton addSelectedMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_profile);
        setContentView(R.layout.activity_add_group_member);


        /// for back profile page
        customActionBar = findViewById(R.id.custom_action_bar_for_profile);
        actionBarTitleProfile=customActionBar.findViewById(R.id.actionBarTitleProfile);
        backButtonProfile=customActionBar.findViewById(R.id.backButtonProfile);
        actionBarTitleProfile.setText(UserName.userName);


        backButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_group_member.this, group_post.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
                // finish();
            }
        });


        /// finding intent value ///groupId
        group_name=findViewById(R.id.group_name);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("groupId")) {
            groupId = intent.getStringExtra("groupId");
            groupName = intent.getStringExtra("groupName");
            group_name.setText(groupName);
        }else{
            Intent iiiii = new Intent(add_group_member.this, all_group.class);
            startActivity(iiiii);
        }
        //// end /////

        recyclerView = findViewById(R.id.recyclerViewAddMember);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addGroupMemberAdapterItems = new ArrayList<>();
        addGroupMemberAdapter = new AddGroupMemberAdapter(this, addGroupMemberAdapterItems);
        recyclerView.setAdapter(addGroupMemberAdapter);

        selectedMemberId = new ArrayList<>();

        addGroupMemberAdapter.setOnAddMemberClickListener(new AddGroupMemberAdapter.OnAddMemberClickListener() {
            @Override
            public void onAddMemberClick(String memberId) {
                // Handle the button click with memberId here
              // Toast.makeText(add_group_member.this, "Add button clicked for memberId: " + memberId, Toast.LENGTH_SHORT).show();
                if (selectedMemberId.contains(memberId)) {
                    // ID exists, remove it
                    selectedMemberId.remove(memberId);
                } else {
                    // ID doesn't exist, add it
                    selectedMemberId.add(memberId);
                }
            }
        });

        // Fetch data from the API
        fetchMemberData();

        addSelectedMember = findViewById(R.id.addSelectedMember);
        addSelectedMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData(selectedMemberId);
            }
        });


    }

    private void addDataToPostPage(JSONObject item) {
        AddGroupMemberItem item_val = new AddGroupMemberItem();
        try {
            // Parse individual attributes from the JSON object
            String id = item.getString("id");
            String image_url = item.getString("img_url");
            String name = item.getString("name_addi");

            String user_profile = "";
            if (!item.isNull("img_url")) {
                user_profile = rootUrl + image_url;
            }


            // Set the parsed data to NewsFeedItem object
            item_val.setAvatarImage(user_profile);
            item_val.setReporterName(name);
            item_val.setMemberId(id);


            // Add the NewsFeedItem to the list
            addGroupMemberAdapterItems.add(item_val);

            // Notify adapter for the data change
            addGroupMemberAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchMemberData() {
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
                            JSONArray dataArray = response.getJSONArray("data");

                            if (status.equals("Success")) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject item = dataArray.getJSONObject(i);
                                    addDataToPostPage(item);
                                    // Access other properties as needed
                                }
                            } else {
                                Toast.makeText(add_group_member.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(add_group_member.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void sendData(ArrayList<String> selectedMemberId) {
        String userId = UserId.userId;
        if (userId.equals("0")) {
            return;
        }
        if (groupId.equals("-1")) {
            return;
        }

        if(selectedMemberId.size()==0){
            Toast.makeText(add_group_member.this, "Select Member First", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
            // Add the selectedMemberId list to the JSON data
            JSONArray selectedMemberIdArray = new JSONArray(selectedMemberId);
            jsonData.put("selectedMemberId", selectedMemberIdArray);
            jsonData.put("groupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = rootUrl + "VDB/add_member_to_group.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");


                            if (status.equals("success")) {
                                Toast.makeText(add_group_member.this, message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(add_group_member.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(add_group_member.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }


}