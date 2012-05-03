package com.cmpe277.android.tweeter.views;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.cmpe277.android.tweeter.LocalTweetsMapActivity;
import com.cmpe277.android.tweeter.R;
import com.cmpe277.android.tweeter.models.Tweet;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * Custom tweet overlay item to be used for overlaying over MapView with
 * custom marker.
 */
public class TweetItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	private LocalTweetsMapActivity localTweetsMapActivity;
	private TweetPopupOverlay popup;
	private HashMap<Integer,TweetPopupOverlay> popupMap = new HashMap<Integer,TweetPopupOverlay>();
	
	public TweetItemizedOverlay(LocalTweetsMapActivity localTweetsMapActivity, Drawable defaultMarker, MapView mapView) {
		// Make the center of the marker be the exact point of the coordinate
		super(boundCenter(defaultMarker));
		
		this.localTweetsMapActivity = localTweetsMapActivity;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	/**
	 * Callback to handle when the marker is being click on. This is where the popup gets build and drawn.
	 * The popup view will be tied to the GeoPoint itself so it moves automatically when the user moves or
	 * zoom in/out on the map.
	 */
	@Override
    protected boolean onTap(int i) {
	
		// Only create a new popup overlay if it does not yet exist in the map
		if (!popupMap.containsKey(new Integer(i))) {
			OverlayItem item = getItem(i);
			GeoPoint geo=item.getPoint();
			
			final MapView mapView = localTweetsMapActivity.getMapView();
		
		    // This listener doesn't do anything when map is touched
		    mapView.setOnTouchListener(
		    	new OnTouchListener() {
		    		@Override
		    		public boolean onTouch(View arg0, MotionEvent arg1) {
		    			return false;
		    		}
		    	});
		      
		    // Inflate the popup layout and set the MapView parent view as the parent view
		    ViewGroup parent = (ViewGroup) mapView.getParent();
		    popup = (TweetPopupOverlay) localTweetsMapActivity.getLayoutInflater().inflate(R.layout.tweet_map_overlay_popup, parent, false);
		    
		    // Set the on click listener to close the popup when the user clock on the popup
		    popup.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View v) {
		    		mapView.removeView(v);
			        mapView.invalidate();
			        
			        // Need to remove from popup map as well
			        popupMap.remove(new Integer(v.getId()));
		    	}
		    });
		      
		    // Fill in the popup views like username, tweet text and profile image
		    Tweet tweet = localTweetsMapActivity.getTweet(i);
		    popup.setupView(i, item.getTitle(), item.getSnippet(), tweet.getImageUrl());
		    
		    // Add new popup overlay to the hashmap
		    popupMap.put(new Integer(i), popup);
		      
		    // Dynamically draw the popup to fit in the window
		    MapView.LayoutParams mapParams = new MapView.LayoutParams(
		    		ViewGroup.LayoutParams.WRAP_CONTENT, 
		            ViewGroup.LayoutParams.WRAP_CONTENT,
		            geo,
		            0,
		            2,
		            MapView.LayoutParams.BOTTOM_CENTER);
		
		    // Remove the popup from the mapView if it is binded first before adding the popup view again
		    mapView.removeView(popup);
		    mapView.invalidate();
		    mapView.addView(popup, mapParams);
		    mapView.invalidate();
		}
		
		return(true);
    }
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

}