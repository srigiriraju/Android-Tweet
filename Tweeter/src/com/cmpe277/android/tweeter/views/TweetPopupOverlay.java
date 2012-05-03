package com.cmpe277.android.tweeter.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmpe277.android.tweeter.DownloadImageTask;
import com.cmpe277.android.tweeter.DownloadImageTaskHandler;
import com.cmpe277.android.tweeter.R;

public class TweetPopupOverlay extends RelativeLayout implements
		DownloadImageTaskHandler {

	// id represents the index of this popup overlay in a map of overlay stored in
	// custom TweetItemizedOverlay
	private int id;
	private TextView usernameTextView;
	private TextView tweetTextView;
	private ImageView profileImageView;
	
	public TweetPopupOverlay(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		profileImageView = (ImageView) findViewById(R.id.tweet_overlay_item_profile_image);
		usernameTextView = (TextView) findViewById(R.id.tweet_overlay_item_username_text);
		tweetTextView = (TextView) findViewById(R.id.tweet_overlay_item_tweet_text);
	}
	
	public void setupView(int id, String username, String tweetText, String imageURL) {
		this.id = id;
		
		// Pulls information from tweet object to put into the tweet list item view
		usernameTextView.setText(username);
		tweetTextView.setText(tweetText);
		
		// If image url is not null and not empty, start download image and set to image view
		if (imageURL != null && imageURL.length() > 0) {
			// Initialize the downloadImageTask with this activity so the task can call our
			// callback method processDownloadImage once the image is download.
			DownloadImageTask downloadImageTask = new DownloadImageTask(this);
			downloadImageTask.execute(imageURL);
		}
	}

	/**
	 * Callback method for DownloadImageTask that needs to be implemented to set the image
	 * to the profile image view and save the image to the tweet object.
	 */
	public void processDownloadImage(Bitmap image) {
		profileImageView.setImageBitmap(image);
	}
	
	/**
	 * Getter for id so onClickListener of the popup gets the popup object from the TweetItemizedOverlay
	 * popup map and remove it from the Map View properly. 
	 */
	public int getId() {
		return id;
	}

}
