package com.cmpe277.android.tweeter;

import com.cmpe277.android.tweeter.models.Storage;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.os.*;
import android.view.View;
import android.util.Log;
import android.widget.*;


public class SendTweet extends Activity{
	private final String TAG=SendTweet.this.toString();
	private EditText tweetText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"Entering onCreate");
		setContentView(R.layout.send_tweet);
		setupView();
	}
	
	private void setupView(){
		Button sendTweetButton = (Button)findViewById(R.id.send_tweet_button);
		tweetText = (EditText)findViewById(R.id.edit_text_tweet_msg);
		
		sendTweetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {				
				if(tweetText.getText()==null || tweetText.getText().length()==0){
						Toast.makeText(getBaseContext(), "Please enter your twitter message", Toast.LENGTH_LONG).show();					
				}
				else{
					sendTweetMsg(tweetText.getText().toString());
				}
			}
		});
	}
	
	private void sendTweetMsg(String tweetMsg){
		try{
			Storage store = Storage.getInstance(getBaseContext());
			AccessToken acToken = store.getAccessToken();
			if(acToken != null){
				Object obj[] = new Object[2];
				obj[0]=acToken;
				obj[1]=tweetMsg;
				
				(new RunSendTweet()).execute(obj);				
			}
			else{
				Toast.makeText(this,"No access token. Cannot send tweet.", Toast.LENGTH_LONG).show();
			}
		}
		catch(Exception ex){
			Toast.makeText(this,"Exception occurred. Could not send tweet. Please try again.", Toast.LENGTH_LONG).show();
		}
	}
	
	//Async background/foreground for sending a Tweet
	public class RunSendTweet extends AsyncTask<Object,String,Boolean>{
		protected Boolean doInBackground(Object ... params){
			Boolean bool = new Boolean(true);
			try{
					AccessToken acToken = (AccessToken)params[0];
					String tweetMsg = (String)params[1];
					Log.i(TAG,"Entering doInBackground");
					Twitter twitter = new TweeterFactory().getTwitter();
					//user authorization to follow is set here
					twitter.setOAuthAccessToken(acToken);
					twitter.updateStatus(tweetMsg);
				} catch (TwitterException e) {
					bool = new Boolean(false);
					e.printStackTrace(System.out);
				}
				return bool;
			}	 
				 
			protected void onPostExecute(Boolean bool) {
		    	super.onPostExecute(bool);
		    	Log.i(TAG,"Entering onPostExecute");
		    	if(bool.booleanValue()){
		    		Toast.makeText(SendTweet.this,"Your tweet has been sent", Toast.LENGTH_LONG).show();				
		    	}
		    	else{
		    		Toast.makeText(SendTweet.this,"Could not send tweet.  Please try again.", Toast.LENGTH_LONG).show();
		    	}
		    	finish();
			}
		}
}
