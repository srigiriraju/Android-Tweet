package com.cmpe277.android.tweeter;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cmpe277.android.tweeter.R;
import com.cmpe277.android.tweeter.adapters.TweetListAdapter;
import com.cmpe277.android.tweeter.listeners.TweeterLocationListener;
import com.cmpe277.android.tweeter.models.Tweet;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LocalTweetsActivity extends ListActivity implements TwitterApiRequestHandler {

	private final static String TWITTER_API_URL_SEARCH_LOCAL = "http://search.twitter.com/search.json?geocode=%.7f,%.7f,%dmi&rpp=20&include_entities=true&result_type=mixed";
	//private final static String TWITTER_API_URL_SEARCH_LOCAL = "http://search.twitter.com/search.json?geocode=%.7f,%.7f,%dmi&rpp=100&include_entities=true&result_type=mixed";
	private final static int LOCAL_TWEETS_SEARCH_RADIUS = 20; // in miles
	
	public final static String LOCAL_TWEETS_LIST_KEY = "local_tweets_list";
	public final static String CURRENT_LOCATION_LATITUDE_KEY = "current_location_latitude";
	public final static String CURRENT_LOCATION_LONGITUDE_KEY = "current_location_longitude";
	
	// Location related variable
	private TweeterLocationListener locationListener;
	private LocationManager locationManager;
	private Location currentLocation;
	
	// Layout related objects
	private Button showMapButton;
	
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
				
				final Tweet tweet = new Tweet(user, username, tweetText, imageUrl, locationText);

				// If geo object exists, proceed to grab the lat and long coordinates
				if (!jTweet.isNull("geo")) {
					JSONObject geo = jTweet.getJSONObject("geo");
					JSONArray coordinates = geo.getJSONArray("coordinates");
					tweet.setCoordinate(coordinates.getDouble(0), coordinates.getDouble(1));
				} else {
					// Get the Geocoder to pass to the tweet object so it can try to retrieve
					// geo coordinates from the location text
					final Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
					
					// Create new thread to have the tweet object go look up the coordinates so it
					// would not slow down the UI thread.
					new Thread(new Runnable() {
						public void run() {
							tweet.retrieveCoordinatesForLocationText(geocoder);
						}
					}).start();
					
				}
				
				tweets.add(tweet);
			}
		} catch (JSONException e) {
			Log.e(LocalTweetsActivity.class.toString(), "Failed to parse twitter result for search local request.", e);
		}
		
		adapter.setTweets(tweets);
	}
	
	private void setupViews() {
		
		// Setup for the "Show Map" button
		showMapButton = (Button)findViewById(R.id.show_map_button);
		showMapButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Go to Map view of local tweets
				Intent intent = new Intent(LocalTweetsActivity.this, LocalTweetsMapActivity.class);
				
				// Only pass the tweets and the location if they exist
				if (currentLocation != null && !adapter.isEmpty()) {
					intent.putExtra(LOCAL_TWEETS_LIST_KEY, removeImageBitmapForTweets(adapter.getTweets()));
					intent.putExtra(CURRENT_LOCATION_LATITUDE_KEY, currentLocation.getLatitude());
					intent.putExtra(CURRENT_LOCATION_LONGITUDE_KEY, currentLocation.getLongitude());
				}
				startActivity(intent);
			}
		});
	}

	/**
	 * This method is needed to clear out image because Bitmap is causing problem retrieving Tweet object
	 * as a Serializable extra in the intent. Also, it helps reduce memory usage to not pass all images.
	 * Image URL is still available for other Activity to use for downloading image.
	 * 
	 * @param tweets
	 * @return
	 */
	private ArrayList<Tweet> removeImageBitmapForTweets(ArrayList<Tweet> tweets) {
		for (Tweet tweet : tweets) {
			tweet.setImage(null);
		}
		return tweets;
	}
}
