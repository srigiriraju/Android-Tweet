package com.cmpe277.android.tweeter.adapters;

import java.util.ArrayList;

import com.cmpe277.android.tweeter.models.Tweet;
import com.cmpe277.android.tweeter.views.TweetListItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TweetListAdapter extends BaseAdapter {

	private ArrayList<Tweet> tweets;
	private Context context;
	
	public TweetListAdapter(Context context, ArrayList<Tweet> tweets) {
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
		TweetListItem tli;
		if (convertView == null) {
			tli = (TweetListItem)View.inflate(context, com.cmpe277.android.tweeter.R.layout.tweet_list_item, null);
		} else {
			tli = (TweetListItem)convertView;
		}
		tli.setTweet(tweets.get(position));
		
		return tli;
	}
	
	public ArrayList<Tweet> getTweets() {
		return tweets;
	}
	
	public void setTweets(ArrayList<Tweet> newTweets) {
		tweets = newTweets;
		notifyDataSetChanged();
	}
	
	public void forceReload() {
		notifyDataSetChanged();
	}

}
