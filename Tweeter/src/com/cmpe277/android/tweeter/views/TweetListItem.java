package com.cmpe277.android.tweeter.views;

import com.cmpe277.android.tweeter.models.Tweet;
import com.cmpe277.android.tweeter.DownloadImageTask;
import com.cmpe277.android.tweeter.DownloadImageTaskHandler;
import com.cmpe277.android.tweeter.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TweetListItem extends RelativeLayout implements DownloadImageTaskHandler {

	private Tweet tweet;
	private TextView username;
	private TextView tweetText;
	private ImageView profileImage;
	
	public TweetListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		profileImage = (ImageView)findViewById(R.id.tweet_list_item_profile_image);
		username = (TextView)findViewById(R.id.tweet_list_item_username_text);
		tweetText = (TextView)findViewById(R.id.tweet_list_item_tweet_text);
	}
	
	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
		
		// Pulls information from tweet object to put into the tweet list item view
		username.setText(tweet.getUsername());
		tweetText.setText(tweet.getTweetText());
		
		// If image is already download then use it.
		if (tweet.getImage() != null) {
			profileImage.setImageBitmap(tweet.getImage());
		} else {
			// If image url is not null and not empty, start download image and set to image view
			if (tweet.getImageUrl() != null && tweet.getImageUrl().length() > 0) {
				// Initialize the downloadImageTask with this activity so the task can call our
				// callback method processDownloadImage once the image is download.
				DownloadImageTask downloadImageTask = new DownloadImageTask(this);
				downloadImageTask.execute(tweet.getImageUrl());
			}
		}
	}

	public Tweet getTweet() {
		return tweet;
	}

	/**
	 * Callback method for DownloadImageTask that needs to be implemented to set the image
	 * to the profile image view and save the image to the tweet object.
	 */
	@Override
	public void processDownloadImage(Bitmap image) {
		profileImage.setImageBitmap(image);
		tweet.setImage(image);
	}

}
