package com.cmpe277.android.tweeter;

import android.graphics.Bitmap;

/**
 * Interface for Activity wanting to use DownloadImageTask. This interface requires
 * a class to implement the processDownloadImage method that will take in the Bitmap
 * just download by DownloadImageTask.
 *  
 * @author quynhquach
 *
 */
public interface DownloadImageTaskHandler {
	
	/**
	 * This method should be implemented to use the image that just got download. This
	 * method should be used to set the image view, save profile image in tweet object,
	 * etc.
	 * 
	 * @param image - Bitmap object representing the image just download
	 */
	public void processDownloadImage(Bitmap image);

}
