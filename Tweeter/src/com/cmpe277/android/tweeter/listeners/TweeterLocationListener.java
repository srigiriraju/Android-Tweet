package com.cmpe277.android.tweeter.listeners;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Tweeter location listener will handle new location updates. New location will be compare
 * with current location (if exist) to determine if new location is a better location to use
 * as the latest, most accurate user's current location. Calculation is based on timeliness
 * accuracy, and from which provider (GPS or WiFi). This calculation is done every time the
 * LocationListener callback method, onLocationChanged(), is called.
 * 
 * THIS IS AN ABSTRACT CLASS. The pattern to use this listener class is to create an anonymous
 * class in your activity and implement the abstract method useNewLocation(). This abstract
 * method will be called every time a better user location has been updated. The implementation
 * of this method should call the getCurrentBestLocation() method to get the best, most accurate
 * user location.
 * 
 * CREDIT: Using logic for determining better location from sample on Android Developer Website
 * http://developer.android.com/guide/topics/location/obtaining-user-location.html
 **/
public abstract class TweeterLocationListener implements LocationListener {

	/**
	 * Constant time use to determine whether new location is newer
	 */
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	private Location currentBestLocation;
	
	public TweeterLocationListener() {
		currentBestLocation = null;
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location newLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate 
	    		   && (isFromSameProvider || newLocation.getProvider().equals(LocationManager.GPS_PROVIDER))) {
	    	// MODIFY SLIGHTLY HERE TO GIVE GPS LOCATION PRIORITY
	    	// If providers are the same or if the new location is from a GPS provide then should accept
	    	// this location as being better.
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	public abstract void useNewLocation();
	
	/**
	 * Callback method to listen to the location changed
	 */
	public void onLocationChanged(Location location) {
		// Qualify new location if it is a better location before saving
		if (location != null && isBetterLocation(location)) {
			currentBestLocation = location;
			// Call the abstract method that must be implemented
			useNewLocation();
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	public Location getCurrentBestLocation() {
		return currentBestLocation;
	}
	
}
