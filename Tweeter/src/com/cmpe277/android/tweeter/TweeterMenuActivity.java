package com.cmpe277.android.tweeter;

import com.cmpe277.android.tweeter.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TweeterMenuActivity extends Activity {
	private Button tweetButton;
	private Button timelineButton;
	private Button trendsButton;
	private Button localButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupViews();
    }
    
    private void setupViews() {
    	tweetButton = (Button)findViewById(R.id.menu_tweet_button);
    	timelineButton = (Button)findViewById(R.id.menu_timeline_button);
    	trendsButton = (Button)findViewById(R.id.menu_trends_button);
    	localButton = (Button)findViewById(R.id.menu_local_button);
		
		tweetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO: Go to post tweet activity
				//Intent intent = new Intent(TweeterMenuActivity.this, <POST_TWEET_ACTIVITY_CLASSNAME>.class);
				//startActivity(intent);
			}
		});
		timelineButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO: Go to timeline activity
				//Intent intent = new Intent(TweeterMenuActivity.this, <TIMELINE_ACTIVITY_CLASSNAME>.class);
				//startActivity(intent);
			}
		});
		trendsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TweeterMenuActivity.this, TwitterTrends.class);
				startActivity(intent);
			}
		});
		localButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TweeterMenuActivity.this, LocalTweetsActivity.class);
				startActivity(intent);
			}
		});
    }
}