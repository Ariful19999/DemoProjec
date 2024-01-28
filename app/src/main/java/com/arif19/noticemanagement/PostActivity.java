package com.arif19.noticemanagement;



import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.arif19.noticemanagement.device.LogOut;
import com.arif19.noticemanagement.modal.NewsFeedItem;
import com.arif19.noticemanagement.recycleview.NewsFeedAdapter;
import com.arif19.noticemanagement.user.UserId;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsFeedAdapter newsFeedAdapter;
    private List<NewsFeedItem> newsFeedItems;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    ImageView notification_btn;
    ImageView video_btn;
    ImageView messenger_btn;
    ImageView home_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);




        /// for navigation bar

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(PostActivity.this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.nav_view);
        setupDrawerContent(navigationView);
        ////////////


        // Customize ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//          actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24_inactive); // Set your menu icon
            actionBar.setDisplayShowTitleEnabled(true); // Disable default title
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.white)); // Set background color


        }

        home_btn = findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView = findViewById(R.id.recyclerViewNewsFeed);
                recyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));

                newsFeedItems = new ArrayList<>();
                newsFeedAdapter = new NewsFeedAdapter(PostActivity.this, newsFeedItems);
                recyclerView.setAdapter(newsFeedAdapter);

                // Fetch data from the API
                fetchPostData();
            }
        });

        video_btn = findViewById(R.id.video_btn);
        video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(PostActivity.this, video.class);
                startActivity(ii);
            }
        });




        recyclerView = findViewById(R.id.recyclerViewNewsFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        newsFeedItems = new ArrayList<>();
        newsFeedAdapter = new NewsFeedAdapter(this, newsFeedItems);
        recyclerView.setAdapter(newsFeedAdapter);

        // Fetch data from the API
        fetchPostData();
    }


    //// app menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Handle menu item clicks here
                        switch (menuItem.getItemId()) {
                            case R.id.action_item_profile:
                                Intent intent = new Intent(PostActivity.this, user_profile.class);
                                startActivity(intent);
                                break;
                            case R.id.action_item_add_post:
                                Intent ii = new Intent(PostActivity.this, add_post.class);
                                startActivity(ii);
                                break;
                            case R.id.action_item_add_group:
                                Intent iiii = new Intent(PostActivity.this, add_group.class);
                                startActivity(iiii);
                                break;
                            case R.id.btn_logout:
                                LogOut.destroySaveSession(PostActivity.this);
                                break;
                            case R.id.action_item_view_group_post:
                                Intent iiiii = new Intent(PostActivity.this, all_group.class);
                                startActivity(iiiii);
                                break;

                        }

                        // Close the drawer when an item is selected
                        drawer.closeDrawers();
                        return true;
                    }
                });
    }

///// menu finished ////


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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = rootUrl + "VDB/find_post.php";
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
                                Toast.makeText(PostActivity.this, "Something is Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
