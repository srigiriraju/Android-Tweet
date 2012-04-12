package com.cmpe277.android.tweeter.models;

import android.graphics.Bitmap;

/**
 * Model class containing data associating to a single tweet.
 * @author quynhquach
 *
 */
public class Tweet {

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

	public String getLocationText() {
		return locationText;
	}

	public boolean isHasGeoLocation() {
		return hasGeoLocation;
	}

	public void setHasGeoLocation(boolean hasGeoLocation) {
		this.hasGeoLocation = hasGeoLocation;
	}
}
