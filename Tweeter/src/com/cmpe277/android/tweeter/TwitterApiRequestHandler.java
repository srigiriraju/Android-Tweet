package com.cmpe277.android.tweeter;


/**
 * Interface for Activity wanting to use TwitterApiRequestTask. This interface requires
 * a class to implement the parseTwitterResult method that will handle the JSON result
 * from calling TwitterApiRequestTask.
 *  
 * @author quynhquach
 *
 */
public interface TwitterApiRequestHandler {

	/**
	 * Method to process the result from TwitterApiRequestTask. 
	 * @param result - String that contains Twitter API result in JSON format
	 */
	public void parseTwitterResult(String result);
	
}
