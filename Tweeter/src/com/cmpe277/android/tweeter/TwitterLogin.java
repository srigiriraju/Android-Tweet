package com.cmpe277.android.tweeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import twitter4j.*;
import twitter4j.auth.RequestToken;

public class TwitterLogin extends Activity {
	private final String TAG = this.toString();
	private final String TWITTER_KEY="TWITTER_DATA";
    private final String ACCESS_TOKEN = "accessToken";
    private final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
	private final String CALLBACK_URL="login-to-twitter-sqb-01-android";
	private final String DISCRIMINATOR="!X2aB%$)Yc3";
	private SharedPreferences storage;
	private Button loginButton;
	private EditText usernameEditText;
	private String username;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"Entering onCreate");
        storage = getSharedPreferences(TWITTER_KEY, MODE_PRIVATE);
        setContentView(R.layout.log_into_twitter);
	    setupView();
	}
	
	private void setupView(){
		usernameEditText=(EditText)findViewById(R.id.username_edit);
		loginButton = (Button)findViewById(R.id.login_button);
		
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				username = usernameEditText.getText().toString();
				if(username != null){
					logIntoAccount();
				}
				else{
					new AlertDialog.Builder(TwitterLogin.this)
					.setTitle(R.string.username_not_entered_title)
					.setMessage(R.string.username_not_entered_text)
					.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					})
					.create()
					.show();
				}
			}
		});
	}
	
	private void logIntoAccount(){
		 try{
			 Twitter twitter = new TweeterFactory().getTwitter();
			 (new RunQuery()).execute(twitter);
	     }catch (Exception e){
	       	  e.printStackTrace(System.out);
	     }
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
	     finish();
	 } 
	 
	 public class RunQuery extends AsyncTask<Twitter,String,RequestToken>{
		 protected RequestToken doInBackground(Twitter ... twitter){
			 RequestToken reqToken=null;
			 try{
			 	Log.i(TAG, "Inside doInBackgroud - calling getOAuthRequestToken");
			 	reqToken = twitter[0].getOAuthRequestToken(CALLBACK_URL);
			 } catch (TwitterException e) {
		        e.printStackTrace(System.out);
			 }
			 return reqToken;
		 }
		  
         protected void onPostExecute(RequestToken token) {
        	 super.onPostExecute(token);
             try{
            	 Log.i(TAG, "Starting Webview to log into twitter");
                 WebView twitterSite = new WebView(TwitterLogin.this);
                 twitterSite.loadUrl(token.getAuthenticationURL());
                 setContentView(twitterSite);
             }
             catch(Exception e){
            	 e.printStackTrace(System.out);
             }
         }
	 }
}
