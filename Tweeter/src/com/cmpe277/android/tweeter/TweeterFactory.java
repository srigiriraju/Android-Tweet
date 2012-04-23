package com.cmpe277.android.tweeter;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweeterFactory {
	private final String AUTH_CONSUMER_KEY="U1zAPJ2c4G7L7zjBisFmJg";
	private final String AUTH_CONSUMER_SECRET="zk1q4HFyfovLDtLA5kZ9KIWpVuglwvBcHd4Rfk8rY";
	
	public Twitter getTwitter(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(AUTH_CONSUMER_KEY);
		cb.setOAuthConsumerSecret(AUTH_CONSUMER_SECRET);
		return new TwitterFactory(cb.build()).getInstance();
	}
}
