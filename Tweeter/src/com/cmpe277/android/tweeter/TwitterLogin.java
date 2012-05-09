package com.cmpe277.android.tweeter;

import com.cmpe277.android.tweeter.models.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.*;
import android.util.Log;
import android.webkit.*;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import twitter4j.*;
import twitter4j.auth.RequestToken;
import twitter4j.auth.AccessToken;

public class TwitterLogin extends Activity {
	private final String TAG = this.toString();	
    private final String CALLBACK_URL="login-to-twitter-sqb-01-android:///";
	private Twitter twitter;
	private Storage stored;
	private RequestToken reqToken;
	
	// Key used in key-pair values to hold loginStatus when stored in result intent going back to TweeterMenuActivity
	public final static String LOGIN_STATUS = "com.cmpe277.android.tweeter.TweeterMenuActivity.LoginStatus";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"Entering onCreate");
		stored = Storage.getInstance(this);
		
		Intent callerIntent = this.getIntent();
        Bundle callerIntentExtras = callerIntent.getExtras();
        
        // Check to see if this is a logged out request from TimelineActivity
        if (callerIntentExtras != null)
        {
	        boolean needToLogout = callerIntentExtras.getBoolean(TweeterMenuActivity.LOGOUT_KEY);
	        if (needToLogout) {
	        	logout();
	        }
        } else {
        	// Call setupView to start the logging in process
        	setupView();
        }
	}
	
	private void setupView(){
		if(stored.getAccessToken()!= null){
			new AlertDialog.Builder(TwitterLogin.this)
			.setTitle(R.string.user_already_logged_into_twitter_title)
			.setMessage(R.string.user_already_logged_into_twitter_text)
			.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					logIntoAccount();
				}
			})
			.setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.create()
			.show();
		}
		else{			
			logIntoAccount();
		}		
	}
	
	private void logIntoAccount(){
		 try{
			 twitter = new TweeterFactory().getTwitter();
			 (new RunTwitterSignOn()).execute(twitter);
	     }catch (Exception e){
	       	  e.printStackTrace(System.out);
	     }
	}
	
	private void logout() {
		// Clear cookie so user can be logged out
		CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		
		// Clear all data saved of user
		stored.clearUserData();
		
		Intent resultIntent = new Intent();
		
		// Pass task location back to TweeterMenuActivity
		resultIntent.putExtra(LOGIN_STATUS, false);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
	
	@Override
	 protected void onResume() {
	     super.onResume();
	     Log.i(TAG,"Entering onResume()");
	 }

	 @Override
	 protected void onNewIntent(Intent intent) {
	     super.onNewIntent(intent);
	     Log.i(TAG,"Entering onNewIntent()");
	     //returned from twitter login so set main menu back online.  Not required, but used for better screen appearance.
	     setContentView(R.layout.main);
	     Uri uri = intent.getData();
	     //If the user has just logged in get data received from Twitter.
	     if (uri != null && uri.toString().startsWith(CALLBACK_URL)) { 
             String oauthVerifier = uri.getQueryParameter("oauth_verifier");
             (new RunCreateAccessToken()).execute(oauthVerifier);
	     }
	     else{
	    	 Log.i(TAG,"uri not from login. uri is " + uri);	    	 
	    	 finish();
	     }
	 } 
			     
     /**
      * Background thread for doing the sign on the twitter
      */
	 public class RunTwitterSignOn extends AsyncTask<Twitter,String,RequestToken>{
		 protected RequestToken doInBackground(Twitter ... twitter){
			 RequestToken theReqToken = null;
			 try{
			 	Log.i(TAG, "Inside doInBackgroud - calling getOAuthRequestToken");
			 	theReqToken = twitter[0].getOAuthRequestToken(CALLBACK_URL);
			 } catch (TwitterException e) {
		        e.printStackTrace(System.out);
			 }
			 return theReqToken;
		 }
		  
         protected void onPostExecute(RequestToken token) {
        	 super.onPostExecute(token);
             try{
            	 reqToken=token;
            	 Log.i(TAG, "Starting Webview to log into twitter");
                 WebView twitterSite = new WebView(TwitterLogin.this);
                 Log.i(TAG,"Loading url to twitterSite WebView");
                 twitterSite.loadUrl(token.getAuthenticationURL());
                 Log.i(TAG,"Setting twitterSite as ContentView");
                 setContentView(twitterSite);                 
             }
             catch(Exception e){
            	 e.printStackTrace(System.out);
             }
         }
	 }
	 
	 /**
      * Background thread for requesting authorization token from Twitter
      */
	 public class RunCreateAccessToken extends AsyncTask<String,String,String>{
		 protected String doInBackground(String ... params){
			 try{				
				String oAuthVer = params[0];
				AccessToken atToken = twitter.getOAuthAccessToken(reqToken, oAuthVer);
                stored.saveAccessToken(atToken);
			 } catch (Exception e) {
		        e.printStackTrace(System.out);
			 }
			 return null;
		 }
		  
         protected void onPostExecute(String empty) {
        	 super.onPostExecute(empty);
        	 if(stored.getAccessToken()==null){
        		 Toast.makeText(TwitterLogin.this, "Twitter authorization error x01. Please try again later", Toast.LENGTH_SHORT).show();
        	 }
        	 else{
        		 Toast.makeText(TwitterLogin.this, "Login Successful!", Toast.LENGTH_SHORT).show();
        	 }
        	 
        	 // Pass task location back to TweeterMenuActivity
        	 Intent resultIntent = new Intent();
     		 resultIntent.putExtra(LOGIN_STATUS, true);
     		 setResult(Activity.RESULT_OK, resultIntent);
     		 finish();
         }
	 }
	 
}
