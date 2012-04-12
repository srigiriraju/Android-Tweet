package com.cmpe277.android.tweeter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

	private DownloadImageTaskHandler callerActivity;
	
	public DownloadImageTask(DownloadImageTaskHandler callerActivity) {
		this.callerActivity = callerActivity;
	}
	
	@Override
	protected Bitmap doInBackground(String... urls) {
		return loadImageFromNetwork(urls[0]);
	}
	
	@Override
	protected void onPostExecute(Bitmap image) {
		callerActivity.processDownloadImage(image);
	}
	
	private Bitmap loadImageFromNetwork(String url) {
		Bitmap bitmap = null;

	    try {
	    	URL imageUrl = new URL(url);
	    	bitmap = BitmapFactory.decodeStream((InputStream)imageUrl.getContent());
	    } catch (IOException e) {
	        Log.e(DownloadImageTask.class.toString(), "Could not load Bitmap from: " + url);
	    }

	    return bitmap;
	}
	
}
