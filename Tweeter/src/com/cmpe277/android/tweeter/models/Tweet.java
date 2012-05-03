package com.cmpe277.android.tweeter.models;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

/**
 * Model class containing data associating to a single tweet.
 * @author quynhquach
 *
 */
public class Tweet implements Serializable {

	/**
	 * Serialization UID generated by Eclipse
	 */
	private static final long serialVersionUID = -1148335265592752043L;
	
	private String user;			// User, i.e. "openculture"
	private String username;		// Username, i.e. "Open Culture" 
	private String tweetText;		// Tweet text content
	private String imageUrl;		// URL to the download user profile image
	private Bitmap image;			// Bitmap of the user profile image once downloaded saved to avoid downloading repetitively while user scroll the tweet list
	private double latitude;		// Latitude coordinate of geo attribute of the tweet. Might not be set if geo attr is null for tweet
	private double longitude;		// Longitude coordinate of geo attribute of the tweet. Might not be set if geo attr is null for tweet
	private String locationText;	// Text address of location. Could be empty but usually has data
	private boolean hasGeoLocation = false;	// Set to false as default. Only set to true if Tweet object created with lat and long coordinates
	
	public Tweet(String user, String username, String tweetText, String imageUrl, String locationText) {
		this.user = user;
		this.username = username;
		this.tweetText = tweetText;
		this.imageUrl = imageUrl;
		this.locationText = locationText;
	}
	
	public Tweet(String user, String username, String tweetText, String imageUrl, String locationText, double latitude, double longitude) {
		this.user = user;
		this.username = username;
		this.tweetText = tweetText;
		this.imageUrl = imageUrl;
		this.locationText = locationText;
		this.latitude = new Double(latitude);
		this.longitude = new Double(longitude);
		this.hasGeoLocation = true;
	}

	public static final int MAX_ADDRESS_PER_LOCATION_TEXT_LOOKUP = 1;
	
	public void retrieveCoordinatesForLocationText(Geocoder geocoder) {
		if (locationText != null && locationText.length() > 0) {
			try {
				List<Address> addresses = geocoder.getFromLocationName(locationText, MAX_ADDRESS_PER_LOCATION_TEXT_LOOKUP);
				if (addresses != null && addresses.size() > 0) {
					latitude = addresses.get(0).getLatitude();
					longitude = addresses.get(0).getLongitude();
					hasGeoLocation = true;
					Log.i(Tweet.class.toString(), "Got " + latitude + ", " + longitude + " for location " + locationText);
				}
			} catch (Exception e) {
				// Print error log and no location will be updated to the map
				Log.e(Tweet.class.toString(), "ERROR : Fail to retrieve address from location " + locationText + ".\n");
				e.printStackTrace();
			}
		}
	}
	
	public String getUser() {
		return user;
	}

	public String getUsername() {
		return username;
	}

	public String getTweetText() {
		return tweetText;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setCoordinate(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.hasGeoLocation = true;
	}

	public String getLocationText() {
		return locationText;
	}

	public boolean hasGeoLocation() {
		return hasGeoLocation;
	}
	
}
