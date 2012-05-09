package com.cmpe277.android.tweeter;

import twitter4j.ProfileImage;
import twitter4j.Twitter;

import com.cmpe277.android.tweeter.R;
import com.cmpe277.android.tweeter.models.Storage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TweeterMenuActivity extends Activity implements DownloadImageTaskHandler {
	private Button tweetButton;
	private Button timelineButton;
	private Button trendsButton;
	private Button localButton;
	private Button twitterLoginButton;
	private ImageView profileImage;
	
	private Bitmap userProfileImage;
	
	public static final String USERNAME_KEY = "username";
	public static final String LOGOUT_KEY = "logout";
	
	private static final int LOGIN_LOGOUT = 1000;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupViews();
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		Storage store = Storage.getInstance(getBaseContext());
		updateLoginButton(store.isUserLoggedIntoTwitter());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case (LOGIN_LOGOUT) : {
				if (resultCode == Activity.RESULT_OK) {
					// Parse extras for our task location, if any
					Bundle extras = data.getExtras();
					if(extras != null && !extras.isEmpty() && extras.containsKey(TwitterLogin.LOGIN_STATUS)) {
						boolean isLoggedIn = extras.getBoolean(TwitterLogin.LOGIN_STATUS);
						updateLoginButton(isLoggedIn);
					}
				}
				break;
			} 
		}
	}
    
    private void updateLoginButton(boolean isLoggedIn) {
    	if (isLoggedIn) {
			// Set button text to logout text
			final Storage store = Storage.getInstance(getBaseContext());
			Resources res = getResources();
        	String logoutText = String.format(res.getString(R.string.logout_button),store.getUsername());
			twitterLoginButton.setText(logoutText);
			
			if (store.getProfileImageUrl() == null) {
				// Run async task to get the user's profile image URL
				(new RunGetUserProfileImage()).execute(store.getUsername());
			} else if (userProfileImage == null) {
				startDownloadImageTask(store.getProfileImageUrl());
			} else {
				profileImage.setImageBitmap(userProfileImage);
			}
		} else {
			// Set button text to login text
			twitterLoginButton.setText(R.string.menu_button_log_into_twitter_title);
			
			userProfileImage = null;
			profileImage.setImageBitmap(null);
		}
    }
    
    private void setupViews() {
    	twitterLoginButton = (Button)findViewById(R.id.menu_log_into_twitter_button);
    	tweetButton = (Button)findViewById(R.id.menu_tweet_button);
    	timelineButton = (Button)findViewById(R.id.menu_timeline_button);
    	trendsButton = (Button)findViewById(R.id.menu_trends_button);
    	localButton = (Button)findViewById(R.id.menu_local_button);
    	profileImage = (ImageView)findViewById(R.id.user_profile_image);
    	
    	final Storage store = Storage.getInstance(getBaseContext());
    	
    	// If user is logged in, changed text according with their username
		if(store.isUserLoggedIntoTwitter()){
			Resources res = getResources();
        	String logoutText = String.format(res.getString(R.string.logout_button),store.getUsername());
			twitterLoginButton.setText(logoutText);
		}
		
    	twitterLoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TweeterMenuActivity.this, TwitterLogin.class);
				
				// If user is logged in, this will add key-value to the intent to tell the TwitterLogin class to log the user out.
				if (store.isUserLoggedIntoTwitter()) {
					intent.putExtra(LOGOUT_KEY, true);
				}
				startActivityForResult(intent, LOGIN_LOGOUT);				
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
				} else {
					Intent intent = new Intent(TweeterMenuActivity.this, TimelineActivity.class);
					intent.putExtra(USERNAME_KEY, store.getUsername());
					startActivity(intent);
				}
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
		
		// Download the image if there is a profile image url stored
		if (userProfileImage == null) {
			profileImage.setImageBitmap(null);
			
			if (store.getProfileImageUrl() != null) {
				startDownloadImageTask(store.getProfileImageUrl());
			}
		} else {
			profileImage.setImageBitmap(userProfileImage);
		}
		
    }
    
    /**
	  * Background thread for getting the user profile image from Twitter
	  */
	 public class RunGetUserProfileImage extends AsyncTask<String, Void, ProfileImage> {
		 Storage store = Storage.getInstance(getBaseContext());
		 
		 protected ProfileImage doInBackground(String ...params) {
			 ProfileImage image = null;
			 try {
					
					Twitter twitter = new TweeterFactory().getTwitter();
					image = twitter.getProfileImage(params[0], ProfileImage.BIGGER);
					
			} catch (Exception e) {
				Log.e(TwitterLogin.class.toString(), "Fail to get user profile image of @" + store.getUsername());
				e.printStackTrace(System.out);
			}
			 return image;
		 }
		 
		 protected void onPostExecute(ProfileImage image) {
			 super.onPostExecute(image);
			 
			 store.setProfileImageUrl(image.getURL());
			 startDownloadImageTask(image.getURL());
		 }
	 }

    public void startDownloadImageTask(String imageUrl) {
    	// Initialize the downloadImageTask with this activity so the task can call our
    	// callback method processDownloadImage once the image is download.
    	DownloadImageTask downloadImageTask = new DownloadImageTask(this);
    	downloadImageTask.execute(imageUrl);
    }

	@Override
	public void processDownloadImage(Bitmap image) {
		userProfileImage = image;
		profileImage.setImageBitmap(userProfileImage);
	}
    
}