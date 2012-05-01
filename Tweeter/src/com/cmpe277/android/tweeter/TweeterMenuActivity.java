package com.cmpe277.android.tweeter;

import com.cmpe277.android.tweeter.R;
import com.cmpe277.android.tweeter.models.Storage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TweeterMenuActivity extends Activity {
	private Button tweetButton;
	private Button timelineButton;
	private Button trendsButton;
	private Button localButton;
	private Button twitterLoginButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupViews();
    }
    
    private void setupViews() {
    	twitterLoginButton = (Button)findViewById(R.id.menu_log_into_twitter_button);
    	tweetButton = (Button)findViewById(R.id.menu_tweet_button);
    	timelineButton = (Button)findViewById(R.id.menu_timeline_button);
    	trendsButton = (Button)findViewById(R.id.menu_trends_button);
    	localButton = (Button)findViewById(R.id.menu_local_button);
		
    	twitterLoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TweeterMenuActivity.this, TwitterLogin.class);
				startActivity(intent);				
			}
		});
    	
		tweetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {	
				Storage store = Storage.getInstance(getBaseContext());
				if(!store.isUserLoggedIntoTwitter()){
					new AlertDialog.Builder(TweeterMenuActivity.this)
					.setTitle(R.string.user_not_logged_into_twitter_title)
					.setMessage(R.string.user_not_logged_into_twitter_text)
					.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					})
					.create()
					.show();
				}
				else{
					Intent intent = new Intent(TweeterMenuActivity.this, SendTweet.class);
					startActivity(intent);
				}
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