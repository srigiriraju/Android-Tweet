package com.cmpe277.android.tweeter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cmpe277.android.tweeter.R;
import com.cmpe277.android.tweeter.adapters.TweetListAdapter;
import com.cmpe277.android.tweeter.listeners.TweeterLocationListener;
import com.cmpe277.android.tweeter.models.Tweet;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocalTweetsActivity extends ListActivity implements TwitterApiRequestHandler {

	private final static String TWITTER_API_URL_SEARCH_LOCAL = "http://search.twitter.com/search.json?geocode=%.7f,%.7f,%dmi&rpp=20&include_entities=true&result_type=mixed";
	private final static int LOCAL_TWEETS_SEARCH_RADIUS = 20; // in miles
	
	// Location related variable
	private TweeterLocationListener locationListener;
	private LocationManager locationManager;
	private Location currentLocation;
	
	private TweetListAdapter adapter;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_tweets_main);
        
        adapter = new TweetListAdapter(this, new ArrayList<Tweet>());
        setListAdapter(adapter);
        
        // Initialize the location manager by retrieving system Location Manager and setting listener
     	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
     	
        // Initialize app location listener so it can respond to new location updates when tracking is turn ON
     	locationListener = new TweeterLocationListener() {
     		public void useNewLocation() {
     			updateLocalTweetsList();
     		}
     	};
     	
     	setupViews();
    }
	
	/**
	 * Calls when this activity goes into the foreground either being started for the first
	 * time or being restarted.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		turnOnLocationTracking();
		
		adapter.forceReload();
	}
	
	/**
	 * Calls when another activity enters the foreground and this activity is no longer at the
	 * top of the stack.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		turnOffLocationTracking();
	}

/*
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		adapter.toggleTaskCompleteAtPosition(position);
		Task t = adapter.getItem(position);
		app.saveTask(t);
	}
*/
	
	private void turnOnLocationTracking() {
		// Register the listener with the Location Manager to receive location updates for GPS and Network
		//List<String> listOfProviders = locationManager.getAllProviders();
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 200, locationListener);
		}
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,200, locationListener);
		}
	}
	
	private void turnOffLocationTracking() {
		// Remove provider location listener
		locationManager.removeUpdates(locationListener);
	}
	
	/**
	 * Gets called every time the TweeterLocationListener callback useNewLocation method gets called.
	 */
	private void updateLocalTweetsList() {
		Location newLocation = locationListener.getCurrentBestLocation();
		if (currentLocation == null ||
				((currentLocation != null) && 
				((currentLocation.getLatitude() != newLocation.getLatitude()) || 
				(currentLocation.getLongitude() != newLocation.getLongitude())))) {
			
			currentLocation = newLocation;
			Toast.makeText(this, String.format("New location: %1$.5f, %2$.5f", currentLocation.getLatitude(), currentLocation.getLongitude()), Toast.LENGTH_SHORT).show();
			
			// New location update so search local tweets from Twitter API
			String requestURL = String.format(TWITTER_API_URL_SEARCH_LOCAL, currentLocation.getLatitude(), currentLocation.getLongitude(), LOCAL_TWEETS_SEARCH_RADIUS);
			TwitterApiRequestTask twitterApiRequestTask = new TwitterApiRequestTask(this);
			twitterApiRequestTask.execute(requestURL);
		}
	}
	
	/**
	 * The callback method that is required to be implemented for interface TwitterApiRequestHandler.
	 * This method gets result from a JSON request in JSON format.
	 * 
	 * Sample JSON format for Twitter API search response
	 * 	{
	 * 		"results": [
	 * 			{
	 * 				"from_user":"google",
	 * 				"from_user_name": "A Googler",
	 * 				"profile_image_url": "http:\/\/a0.twimg.com\/profile_images\/77186109\/favicon_normal.png",
	 * 				"text": "Toward a simpler, more beautiful Google: a more functional & flexible version of Google+ rolling out now http:\/\/t.co\/NJlyGUrk",
	 * 	if exists-> "location": "Mountain View, CA",
	 * 	if exists->	"geo": {
	 * 					"coordinates": [37.4220050,-122.0840950],
	 * 					"type": "Point"
	 * 				}
	 * 			},
	 * 			....
	 * 		]
	 * 	}		
	 * 	
	 * @param result - A string representing the result of a JSON request in JSON format
	 */
	public void parseTwitterResult(String result) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		
		try {
			JSONObject jTwitterResult = new JSONObject(result);
			JSONArray jTweets = jTwitterResult.getJSONArray("results");
			for (int i = 0; i < jTweets.length(); i++) {
				JSONObject jTweet = jTweets.getJSONObject(i);
				
				// Create new Tweet instance
				String user = jTweet.getString("from_user");
				String username = jTweet.getString("from_user_name");
				String imageUrl = jTweet.getString("profile_image_url");
				String tweetText = jTweet.getString("text");
				
				String locationText = null;
				if (!jTweet.isNull("location")) {
					locationText = jTweet.getString("location");
				}
				
				Tweet tweet = new Tweet(user, username, tweetText, imageUrl, locationText);
				
				//Object obj = jTweet.get("geo");
				//boolean objExist = jTweet.has("geo");
				
				// If geo object exists, proceed to grab the lat and long coordinates
				if (!jTweet.isNull("geo")) {
					JSONObject geo = jTweet.getJSONObject("geo");
					JSONArray coordinates = geo.getJSONArray("coordinates");
					tweet.setLatitude(coordinates.getDouble(0));
					tweet.setLongitude(coordinates.getDouble(1));
					tweet.setHasGeoLocation(true);
				}
				
				tweets.add(tweet);
			}
		} catch (JSONException e) {
			Log.e(LocalTweetsActivity.class.toString(), "Failed to parse twitter result for search local request.", e);
		}
		
		adapter.setTweets(tweets);
	}
	
	/**
	 * Sample code to read twitter timeline
	 * @return
	 */
	/*
	public String readTwitterFeed() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				"http://twitter.com/statuses/user_timeline/vogella.json");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(ParseJSON.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	*/
	
	private void setupViews() {
		;
	}

	
}
