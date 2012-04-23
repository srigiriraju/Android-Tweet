package com.cmpe277.android.tweeter;

import java.util.*;

import com.cmpe277.android.tweeter.adapters.TrendTweetListAdapter;
import twitter4j.*;
import android.app.ListActivity;
import android.os.*;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class TrendTweets extends ListActivity{
	private final String TAG=TrendTweets.this.toString();
	private Button followButton;
	private TrendTweetListAdapter adapter;
	private ListView trendTweetsList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"Entering onCreate");
		String queryStr=null;
		
		Bundle extras=getIntent().getExtras();
		if(extras != null){
			queryStr = extras.getString("TREND_NAME_FOR_QUERY");
		}
	    setContentView(R.layout.trend_tweets_list);  
	    adapter = new TrendTweetListAdapter(this, new ArrayList<Tweet>());
	    setListAdapter(adapter);
	    Log.i(TAG,"onCreate calling setupView");
	    setupView(queryStr);
	}
	 
	private void setupView(String queryStr){
		getTrendTweets(queryStr);
		trendTweetsList = (ListView) findViewById(android.R.id.list);
    	trendTweetsList.setAdapter(adapter); 
    	
		trendTweetsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Tweet tweet = (Tweet)trendTweetsList.getAdapter().getItem(position);
				Log.i(TAG,"Brett got Tweet " + tweet.getFromUser());
			}
		});
	}
	 
	private void getTrendTweets(String queryStr){
		try {
			Log.i(TAG,"Brett entering getTrendTweets");
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
				Log.i(TAG,"Brett entering doInBackground");
				Twitter twitter = new TweeterFactory().getTwitter();
				Query query = new Query("source:twitter4j " + params[0]);
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
	    	Log.i(TAG,"Brett entering onPostExecute");
	    	if(result != null && result.size() > 0){
	    		adapter.setTrendTweets(result);
				for (Tweet tweet : result) {
					System.out.println(tweet.getFromUser() + ":" + tweet.getText());
				}
	        }
	        else{
	        	Toast.makeText(TrendTweets.this,"No Tweets for this trend were found!", Toast.LENGTH_SHORT);	                 
	         }
		}
	}
}
