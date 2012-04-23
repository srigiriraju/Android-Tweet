package com.cmpe277.android.tweeter.views;


import com.cmpe277.android.tweeter.DownloadImageTask;
import com.cmpe277.android.tweeter.DownloadImageTaskHandler;
import com.cmpe277.android.tweeter.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrendTweetListItem extends RelativeLayout implements DownloadImageTaskHandler {
	private TextView username;
	private TextView tweetText;
	private ImageView profileImage;
	
	public TrendTweetListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		profileImage = (ImageView)findViewById(R.id.tweet_list_item_profile_image);
		username = (TextView)findViewById(R.id.tweet_list_item_username_text);
		tweetText = (TextView)findViewById(R.id.tweet_list_item_tweet_text);
	}
	
	public void setTrendTweetUserName(String theUsername){
		username.setText(theUsername);
	}
	
	public String getTrendTweetUserName(){
		return username.getText().toString();
	}
	
	public void setTrendTweetText(String text){
		tweetText.setText(text);
	}
	
	public String getTrendTweetText(){
		return tweetText.getText().toString();
	}
	
	public void setTrendTweetImageUrl(String url){
		// Initialize the downloadImageTask with this activity so the task can call our
		// callback method processDownloadImage once the image is download.
		DownloadImageTask downloadImageTask = new DownloadImageTask(this);
		downloadImageTask.execute(url);
	}
	
	/**
	 * Callback method for DownloadImageTask that needs to be implemented to set the image
	 * to the profile image view and save the image to the tweet object.
	 */
	@Override
	public void processDownloadImage(Bitmap image) {
		profileImage.setImageBitmap(image);
	}

}
