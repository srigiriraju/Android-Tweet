package com.cmpe277.android.tweeter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import com.cmpe277.android.tweeter.adapters.TweetListAdapter;
import com.cmpe277.android.tweeter.models.Storage;
import com.cmpe277.android.tweeter.models.Tweet;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends ListActivity implements TwitterApiRequestHandler {

	private final static String TWITTER_API_URL_TIMELINE = "http://twitter.com/statuses/user_timeline/%s.json";
	
	private TextView timelineTitle;
	private Button userHomeTimelineButton;
	
	private String username;
	
	private TweetListAdapter adapter;
	
	private ArrayList<Tweet> userTimelineTweets;
	private ArrayList<Tweet> homeTimelineTweets;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_tweets_list);
        
        adapter = new TweetListAdapter(this, new ArrayList<Tweet>());
        setListAdapter(adapter);
        
        Intent callerIntent = this.getIntent();
        Bundle callerIntentExtras = callerIntent.getExtras();
        
        // Retrieve all the logged in username from TimelineActivity
        if (callerIntentExtras != null)
        {
	        String str = callerIntentExtras.getString(TweeterMenuActivity.USERNAME_KEY);
	        if (str != null && str.length() > 0) {
	        	username = str;
	        }
        }
     	
     	setupViews();
    }
	
	private void requestHomeTimelineTweets() {
		if (homeTimelineTweets != null) {
			adapter.setTweets(homeTimelineTweets);
		} else {
			try{
				Storage store = Storage.getInstance(getBaseContext());
				AccessToken acToken = store.getAccessToken();
				if(acToken != null){
					(new RunGetHomeTimeline()).execute(acToken);				
				}
				else{
					Toast.makeText(this,"No access token. Cannot send tweet.", Toast.LENGTH_LONG).show();
				}
			}
			catch(Exception ex){
				Toast.makeText(this,"Exception occurred. Could not send tweet. Please try again.", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/**
	 * Async task to retrieve Home Timeline. An authenticated request.
	 * @author quynhquach
	 *
	 */
	public class RunGetHomeTimeline extends AsyncTask<AccessToken,String,ResponseList<twitter4j.Status>> {
		protected ResponseList<twitter4j.Status> doInBackground(AccessToken ... params) {
			ResponseList<twitter4j.Status> responseList = null;
			try {
				AccessToken acToken = (AccessToken)params[0];
				Twitter twitter = new TweeterFactory().getTwitter();
				//OAuth signed the request
				twitter.setOAuthAccessToken(acToken);
				responseList = twitter.getHomeTimeline();
			} catch (TwitterException e) {
				e.printStackTrace(System.out);
			}
			return responseList;
		}
				 
		protected void onPostExecute(ResponseList<twitter4j.Status> responseList) {
	    	super.onPostExecute(responseList);
	    	
	    	ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	    	
	    	for (twitter4j.Status status : responseList) {
	    		// Create new Tweet instance
	    		String user = status.getUser().getName();
				String username = status.getUser().getScreenName();
				String imageUrl = status.getUser().getProfileImageURL().toString();
				String tweetText = status.getText();
				String locationText = null;
				
				final Tweet tweet = new Tweet(user, username, tweetText, imageUrl, locationText);
				
				tweets.add(tweet);
	    	}
	    	
	    	homeTimelineTweets = tweets;
	    	adapter.setTweets(tweets);
		}
	}
	
	private void requestUserTimelineTweets() {
		if (userTimelineTweets != null) {
			adapter.setTweets(userTimelineTweets);
		} else {
			// New location update so search local tweets from Twitter API
			String requestURL = String.format(TWITTER_API_URL_TIMELINE, username);
			TwitterApiRequestTask twitterApiRequestTask = new TwitterApiRequestTask(this);
			twitterApiRequestTask.execute(requestURL);
		}
	}
	
	/**
	 * The callback method that is required to be implemented for interface TwitterApiRequestHandler.
	 * This method gets result from a JSON request in JSON format.
	 * 
	 * Sample JSON format for Twitter API search response
	 * 	[
	 * 		{
	 * 			"user": {
	 * 				"name":"google",
	 * 				"screen_name": "A Googler",
	 * 				"profile_image_url": "http:\/\/a0.twimg.com\/profile_images\/77186109\/favicon_normal.png"
	 * 			}
	 * 			"text": "Toward a simpler, more beautiful Google: a more functional & flexible version of Google+ rolling out now http:\/\/t.co\/NJlyGUrk",
	 * 		},
	 * 			....
	 * 		}
	 * 	]	
	 * 	
	 * @param result - A string representing the result of a JSON request in JSON format
	 */
	@Override
	public void parseTwitterResult(String result) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		
		try {
			JSONArray jTweets = new JSONArray(result);
			for (int i = 0; i < jTweets.length(); i++) {
				JSONObject jTweet = jTweets.getJSONObject(i);
				
				JSONObject jUser = jTweet.getJSONObject("user");
				
				// Create new Tweet instance
				String user = jUser.getString("name");
				String username = jUser.getString("screen_name");
				String imageUrl = jUser.getString("profile_image_url");
				String tweetText = jTweet.getString("text");
				String locationText = null;
				
				final Tweet tweet = new Tweet(user, username, tweetText, imageUrl, locationText);
				
				tweets.add(tweet);
			}
		} catch (JSONException e) {
			Log.e(LocalTweetsActivity.class.toString(), "Failed to parse twitter result for timeline request.", e);
		}
		
		userTimelineTweets = tweets;
		adapter.setTweets(tweets);

	}
	
	private void setupViews() {
		//requestUserTimelineTweets();
		requestHomeTimelineTweets();
		
		timelineTitle = (TextView)findViewById(R.id.title_timeline_tweets_textview);
		userHomeTimelineButton = (Button)findViewById(R.id.user_home_timeline_button);
		userHomeTimelineButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Button thisButton = (Button) v;
				if (thisButton.getText().equals(getString(R.string.user_timeline_text))) {
					thisButton.setText(getString(R.string.home_timeline_text));
					timelineTitle.setText(getString(R.string.activity_title_personal_timeline_tweets));
					requestUserTimelineTweets();
				} else {
					thisButton.setText(getString(R.string.user_timeline_text));
					timelineTitle.setText(getString(R.string.activity_title_home_timeline_tweets));
					requestHomeTimelineTweets();
				}
			}
		});
	}

}
