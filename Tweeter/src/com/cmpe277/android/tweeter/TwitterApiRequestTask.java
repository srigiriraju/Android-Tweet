package com.cmpe277.android.tweeter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class TwitterApiRequestTask extends AsyncTask<String, Void, String> {

	private TwitterApiRequestHandler callerActivity = null;
	
	public TwitterApiRequestTask(TwitterApiRequestHandler caller) {
		callerActivity = caller;
	}
	
	@Override
	protected String doInBackground(String... params) {
		return makeTwitterApiRequest(params[0]);
	}
	
	@Override
	protected void onPostExecute(String result) {
		// Call the caller activity callback method which the caller activity should have implemented
		callerActivity.parseTwitterResult(result);
	}
	
	/**
	 * Method that will make HTTP request with the Twitter API URL being passed in. Response will be returned as String.
	 * @param requestURL - Twitter API URL to make a request to.
	 * @return A String response in JSON format.
	 */
	private String makeTwitterApiRequest(String requestURL) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

	    Log.i(callerActivity.getClass().getName(), "Search local request URL: " + requestURL);
		
		HttpGet httpGet = new HttpGet(requestURL);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(callerActivity.getClass().getName(), "Failed to get response from URL " + requestURL);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

}
