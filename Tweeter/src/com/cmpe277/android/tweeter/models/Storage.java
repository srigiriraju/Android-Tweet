package com.cmpe277.android.tweeter.models;

import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.content.Context;
import twitter4j.auth.AccessToken;

public class Storage{
	private final String ACCESS_TOKEN = "accessToken";
	private final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
	private final String SCREEN_NAME = "screenName";
	private final String USER_ID = "userId";
	private final String PROFILE_IMAGE_URL = "profileImageUrl";
	private final String TWITTER_KEY="TWITTER_DATA";
	private SharedPreferences prefs;
	private static Storage storage;
	
	//private Constructor
	private Storage(Context context){
		prefs = context.getSharedPreferences(TWITTER_KEY, Context.MODE_PRIVATE);
	}
	
	public static Storage getInstance(Context context){
		if(storage == null){
			storage = new Storage(context);
		}
		return storage;
	}
	
	public void saveAccessToken(AccessToken at) {
        String token = at.getToken();
        String secret = at.getTokenSecret();
        String screenName = at.getScreenName();
        Long userId = at.getUserId();
        Editor editor = prefs.edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.putString(ACCESS_TOKEN_SECRET, secret);
        editor.putString(SCREEN_NAME, screenName);
        editor.putLong(USER_ID, userId);
        editor.commit();
	}
	
	 /**
     * The user had previously given our app permission to use Twitter</br>
     * Therefore we retrieve these credentials and fill out the Twitter4j helper
     */
    public AccessToken getAccessToken() {
    		AccessToken at = null;
    		
            String token = prefs.getString(ACCESS_TOKEN, null);
            String secret = prefs.getString(ACCESS_TOKEN_SECRET, null);

            if(token != null && secret != null){
            	// Create the twitter access token from the credentials we got previously
            	at = new AccessToken(token, secret);
            }
            return at;
    }
    
    public boolean isUserLoggedIntoTwitter(){
        String token = prefs.getString(ACCESS_TOKEN, null);
        String secret = prefs.getString(ACCESS_TOKEN_SECRET, null);

        if(token != null && secret != null){
        	return true;
        }
        return false;
    }
    
    public String getUsername() {
    	return prefs.getString(SCREEN_NAME, null);
    }
    
    public Long getUserId() {
    	return prefs.getLong(USER_ID, -1);
    }
    
    public void setProfileImageUrl(String imageUrl) {
    	if (imageUrl != null && imageUrl.length() > 0) {
	    	Editor editor = prefs.edit();
	    	editor.putString(PROFILE_IMAGE_URL, imageUrl);
	    	editor.commit();
    	}
    }
    
    public String getProfileImageUrl() {
    	return prefs.getString(PROFILE_IMAGE_URL, null);
    }
    
    public void clearUserData() {
    	Editor editor = prefs.edit();
    	editor.remove(ACCESS_TOKEN);
    	editor.remove(ACCESS_TOKEN_SECRET);
    	editor.remove(SCREEN_NAME);
    	editor.remove(USER_ID);
    	editor.remove(PROFILE_IMAGE_URL);
    	editor.commit();
    }
}
