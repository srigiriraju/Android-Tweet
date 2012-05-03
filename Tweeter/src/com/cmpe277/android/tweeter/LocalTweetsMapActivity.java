package com.cmpe277.android.tweeter;

import java.util.ArrayList;
import java.util.List;

import com.cmpe277.android.tweeter.models.Tweet;
import com.cmpe277.android.tweeter.views.TweetItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class LocalTweetsMapActivity extends MapActivity {

	private Button showListButton;
	
	private ArrayList<Tweet> localTweets;
	private GeoPoint currentLocation;
	
	// Map handle and related variables
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private Drawable drawableMapMarker;
	private TweetItemizedOverlay itemizedOverlay;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_tweets_map);
        
        Intent callerIntent = this.getIntent();
        Bundle callerIntentExtras = callerIntent.getExtras();
        
        // Retrieve all the local tweets and current location latitude and longitude from LocalTweetsActivity
        if (callerIntentExtras != null)
        {
	        Object obj = callerIntentExtras.getSerializable(LocalTweetsActivity.LOCAL_TWEETS_LIST_KEY);
	        if (obj instanceof ArrayList) {
	        	localTweets = (ArrayList<Tweet>) obj;
	        }
	        
	        double latitude = callerIntentExtras.getDouble(LocalTweetsActivity.CURRENT_LOCATION_LATITUDE_KEY);
	        double longitude = callerIntentExtras.getDouble(LocalTweetsActivity.CURRENT_LOCATION_LONGITUDE_KEY);
	        currentLocation = convertCoordinatesToGeoPoint(latitude, longitude);
        }
        
        setupViews();
    }
	
	/**
	 * Loops through the list of local tweets to add the tweet to the map
	 */
	private void mapTweets() {
		if (localTweets != null && currentLocation != null) {
			mapOverlays.clear(); // Clear of any existing overlay item on map
			for (Tweet tweet : localTweets) {
				if (tweet.hasGeoLocation()) {
					GeoPoint point = convertCoordinatesToGeoPoint(tweet.getLatitude(), tweet.getLongitude());
					OverlayItem overlayItem = new OverlayItem(point, tweet.getUsername(), tweet.getTweetText());
					itemizedOverlay.addOverlay(overlayItem);
				}
			}
			mapOverlays.add(itemizedOverlay);
			Log.i(LocalTweetsMapActivity.class.toString(), "Map itemizedOverlay count for markers: " + itemizedOverlay.size());
			mapView.getController().animateTo(currentLocation);
			mapView.getController().setZoom(11);
		}
	}
    
    private void setupViews() {
    	// Setup map and its overlay with default marker
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();
		drawableMapMarker = this.getResources().getDrawable(R.drawable.tweet_map_icon);
		itemizedOverlay = new TweetItemizedOverlay(this, drawableMapMarker, mapView);
		
		mapTweets();
    			
		showListButton = (Button)findViewById(R.id.show_list_button);
		showListButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Just finish and go back to the List view
				finish();
			}
		});
	}
    
    /**
     * Convenient method to convert latitude and longitude into GeoPoint object
     * @param latitude
     * @param longitude
     * @return
     */
    private GeoPoint convertCoordinatesToGeoPoint(double latitude, double longitude) {
    	return new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_S) {
	      mapView.setSatellite(!mapView.isSatellite());
	      return(true);
	    }
	    else if (keyCode == KeyEvent.KEYCODE_Z) {
	      mapView.displayZoomControls(true);
	      return(true);
	    }
	    
	    return(super.onKeyDown(keyCode, event));
	  }
	
	/**
	 * Get method for mapView so TweetItemizedOverlay can retrieve the view to add popup
	 * @return
	 */
	public MapView getMapView() {
		return this.mapView;
	}
	
	/**
	 * Returns the tweet object if the localTweets contains a tweet object at that index.
	 * Else returns null.
	 * 
	 * @param Integer index of the tweet object in the Tweets array from the adapter.
	 * 
	 * @return Tweet object at index i or null if localTweets is empty or object doesn't
	 * at index
	 */
	public Tweet getTweet(int i) {
		return localTweets.size() > i ? localTweets.get(i) : null;
	}
}
