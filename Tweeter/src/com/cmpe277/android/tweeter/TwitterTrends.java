package com.cmpe277.android.tweeter;

import java.util.ArrayList;

import com.cmpe277.android.tweeter.adapters.TrendListAdapter;
import twitter4j.*;
import android.app.ListActivity;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class TwitterTrends extends ListActivity{
	private final int GLOBAL=1;
	private final String TAG=TwitterTrends.this.toString();
	private Button backButton;
	private TrendListAdapter adapter;
	private ListView trendsList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.trends_list);  
	    adapter = new TrendListAdapter(this, new ArrayList<Trend>());
	    setListAdapter(adapter);
	    setupView();
	}
	 
	private void setupView(){
		getTrends();
		trendsList = (ListView) findViewById(android.R.id.list);
    	trendsList.setAdapter(adapter); 
    	
		trendsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Trend trend = (Trend)trendsList.getAdapter().getItem(position);
				//Brett start here now query tweets from Trend. Use trend.getUrl() to get Tweets.
				Log.i(TAG,"Brett got Trend " + trend.getName() + "; " + trend.getQuery() + "; " + trend.getUrl());
				Intent intent = new Intent(TwitterTrends.this, TrendTweets.class);
				intent.putExtra("TREND_NAME_FOR_QUERY", trend.getQuery());
				startActivity(intent);				
			}
		});
	}
	 
	 private void getTrends(){
		
		try {
			Twitter twitter = new TweeterFactory().getTwitter();
			RunQuery runQuery = new RunQuery();
	        runQuery.execute(twitter,null);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	 }
	 
	 //Async background/foreground for querying for trends.
	 public class RunQuery extends AsyncTask<Twitter,String,Trends>{
		 protected Trends doInBackground(Twitter ... twitter){
			 Trends resultList=null;
			 try{
				 resultList = twitter[0].getLocationTrends(GLOBAL);
			 } catch (TwitterException e) {
		        e.printStackTrace(System.out);
			 }
			 return resultList;
		 }	 
		 
         protected void onPostExecute(Trends result ) {
                 super.onPostExecute(result);
                 if(result != null){
                	 adapter.setTrends(result.getTrends());
                 }
                 else{
                	 Toast.makeText(TwitterTrends.this,"No Trends were found!", Toast.LENGTH_SHORT);
                 }
         }
	 }	 
}
