package com.cmpe277.android.tweeter.models;

import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.content.Context;
import twitter4j.auth.AccessToken;

public class Storage{
	private final String ACCESS_TOKEN = "accessToken";
	private final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
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
        Editor editor = prefs.edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.putString(ACCESS_TOKEN_SECRET, secret);
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
}
