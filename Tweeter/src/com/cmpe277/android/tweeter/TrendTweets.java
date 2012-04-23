package com.cmpe277.android.tweeter;

import java.util.*;

import com.cmpe277.android.tweeter.adapters.TrendTweetListAdapter;
import twitter4j.*;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.*;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class TrendTweets extends ListActivity{
	private final String TAG=TrendTweets.this.toString();
	private Button followTweeterButton;
	private TrendTweetListAdapter adapter;
	private ListView trendTweetsList;
	private String trendName=null;
	private Tweet selectedTweet=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"Entering onCreate");
		
		Bundle extras=getIntent().getExtras();
		if(extras != null){
			trendName = extras.getString("TREND_NAME_FOR_QUERY");
		}
	    setContentView(R.layout.trend_tweets_list);  
	    adapter = new TrendTweetListAdapter(this, new ArrayList<Tweet>());
	    setListAdapter(adapter);
	    Log.i(TAG,"onCreate calling setupView");
	    setupView(trendName);
	}
	 
	private void setupView(String queryStr){
		getTrendTweets(queryStr);
		followTweeterButton = (Button)findViewById(R.id.follow_tweeter_button);
		trendTweetsList = (ListView) findViewById(android.R.id.list);
    	trendTweetsList.setAdapter(adapter); 
    	
		trendTweetsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG,"Enter setOnItemClickListener");
				selectedTweet = (Tweet)trendTweetsList.getAdapter().getItem(position);
				Log.i(TAG,"Selected Tweet From User" + selectedTweet.getFromUser());
			}
		});
		followTweeterButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(selectedTweet == null){
					new AlertDialog.Builder(TrendTweets.this)
					.setTitle(R.string.tweeter_not_selected_title)
					.setMessage(R.string.tweeter_not_selected_text)
					.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					})
					.create()
					.show();
				}
				else{
					Intent intent = new Intent(TrendTweets.this, TwitterLogin.class);
					startActivity(intent);				
				}
			}
		});
	}
	 
	private void getTrendTweets(String queryStr){
		try {
			Log.i(TAG,"Entering getTrendTweets");
			RunQuery runQuery = new RunQuery();
			runQuery.execute(queryStr);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
		 
	//Async background/foreground for querying for trends.
	public class RunQuery extends AsyncTask<String,String,List<Tweet>>{
		protected List<Tweet> doInBackground(String ... params){
			List<Tweet> resultList=new ArrayList<Tweet>();
			try{
				Log.i(TAG,"Entering doInBackground");
				Twitter twitter = new TweeterFactory().getTwitter();
				Query query = new Query(params[0]);
				Log.i(TAG,"doInBackground query is " + query.getQuery());
				QueryResult result = twitter.search(query);
				if(result != null){
					resultList.addAll(result.getTweets());
				}
			} catch (TwitterException e) {
				e.printStackTrace(System.out);
			}
			return resultList;
		}	 
			 
		protected void onPostExecute(List<Tweet> result ) {
	    	super.onPostExecute(result);
	    	Log.i(TAG,"Entering onPostExecute");
	    	if(result != null && result.size() > 0){
	    		adapter.setTrendTweets(result);
	        }
	        else{
	        	Resources res = getResources();
	        	String msg = String.format(res.getString(R.string.tweets_not_found_text),trendName);
	        	new AlertDialog.Builder(TrendTweets.this)
				.setTitle(R.string.tweets_not_found_title)
				.setMessage(msg)
				.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.create()
				.show();
	         }
		}
	}
}
