package com.cmpe277.android.tweeter.adapters;

import java.util.*;
import twitter4j.Tweet;
import com.cmpe277.android.tweeter.views.TrendTweetListItem;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TrendTweetListAdapter extends BaseAdapter {

	private ArrayList<Tweet> tweets;
	private Context context;
	
	public TrendTweetListAdapter(Context context, ArrayList<Tweet> tweets) {
		this.tweets = tweets;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return tweets.size();
	}

	@Override
	public Object getItem(int position) {
		return (null == tweets) ? null : tweets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TrendTweetListItem tli;
		if (convertView == null) {
			tli = (TrendTweetListItem)View.inflate(context, com.cmpe277.android.tweeter.R.layout.trend_tweet_list_item, null);
		} else {
			tli = (TrendTweetListItem)convertView;
		}

		tli.setTrendTweetUserName(tweets.get(position).getFromUser().toString());
		tli.setTrendTweetText(tweets.get(position).getText().toString());
		tli.setTrendTweetImageUrl(tweets.get(position).getProfileImageUrl());
		
		return tli;
	}
	
	public void setTrendTweets(List<Tweet>trendTweets) {
		tweets.addAll(trendTweets); 
		notifyDataSetChanged();
	}
	
	public void forceReload() {
		notifyDataSetChanged();
	}

}
