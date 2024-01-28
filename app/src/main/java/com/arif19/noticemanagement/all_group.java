package com.arif19.noticemanagement;



import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;
import static com.arif19.noticemanagement.user.UserRole.userRole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arif19.noticemanagement.modal.AllGroupItem;
import com.arif19.noticemanagement.recycleview.AllGroupAdapter;
import com.arif19.noticemanagement.user.UserId;
import com.arif19.noticemanagement.user.UserName;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class all_group extends AppCompatActivity {
    private Toolbar customActionBar; // Use the correct Toolbar class
    ImageButton backButtonProfile;
    TextView actionBarTitleProfile;

    private RecyclerView recyclerView;
    private com.arif19.noticemanagement.recycleview.AllGroupAdapter AllGroupAdapter;
    private List<AllGroupItem> allGroupAdapterItems;
    private ArrayList<String> selectedMemberId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_profile);
        setContentView(R.layout.activity_all_group);


        /// for back profile page
        customActionBar = findViewById(R.id.custom_action_bar_for_profile);
        actionBarTitleProfile=customActionBar.findViewById(R.id.actionBarTitleProfile);
        backButtonProfile=customActionBar.findViewById(R.id.backButtonProfile);
        actionBarTitleProfile.setText(UserName.userName);


        backButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(all_group.this, PostActivity.class);
                startActivity(intent);
                // finish();
            }
        });



        recyclerView = findViewById(R.id.recyclerViewAllGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allGroupAdapterItems = new ArrayList<>();
        AllGroupAdapter = new AllGroupAdapter(this, allGroupAdapterItems);
        recyclerView.setAdapter(AllGroupAdapter);


        AllGroupAdapter.setOnAddMemberClickListener(new AllGroupAdapter.OnAddMemberClickListener() {
            @Override
            public void onAddMemberClick(String groupId) {
                Intent iiiii = new Intent(all_group.this, group_post.class);
                iiiii.putExtra("groupId", groupId);
                startActivity(iiiii);
            }
        });

        // Fetch data from the API
        fetchGroupInfo();


    }

    private void addDataToGroupPage(JSONObject item) {
        AllGroupItem item_val = new AllGroupItem();
        try {
            // Parse individual attributes from the JSON object
            String id = item.getString("id");
            String image_url = item.getString("img_url");
            String group_name = item.getString("group_name");

            String group_pic = "";
            if (!item.isNull("img_url")) {
                group_pic = rootUrl + image_url;
            }


            // Set the parsed data to NewsFeedItem object
            item_val.setAvatarImage(group_pic);
            item_val.setReporterName(group_name);
            item_val.setgroupId(id);


            // Add the NewsFeedItem to the list
            allGroupAdapterItems.add(item_val);

            // Notify adapter for the data change
            AllGroupAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchGroupInfo() {
        String userId = UserId.userId;
        if (userId.equals("0")) {
            return;
        }

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userId", userId);
            jsonData.put("userRole", userRole);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = rootUrl + "VDB/find_all_group.php";
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
                                    addDataToGroupPage(item);
                                    // Access other properties as needed
                                }
                            } else {
                                Toast.makeText(all_group.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(all_group.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}