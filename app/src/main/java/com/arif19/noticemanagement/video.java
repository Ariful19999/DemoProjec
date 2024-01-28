package com.arif19.noticemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class video extends AppCompatActivity {

    private Toolbar customActionBar; // Use the correct Toolbar class
    ImageButton backButtonProfile;
    TextView actionBarTitleProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.post_tool_bar);
        setContentView(R.layout.activity_video);

        /// for back  page
        customActionBar = findViewById(R.id.post_toolbar);
        backButtonProfile=customActionBar.findViewById(R.id.backButtonProfile);


        backButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(video.this, PostActivity.class);
                startActivity(intent);
                // finish();
            }
        });
    }
}
