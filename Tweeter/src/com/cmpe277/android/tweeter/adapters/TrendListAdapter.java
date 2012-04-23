package com.cmpe277.android.tweeter.adapters;

import java.util.ArrayList;
import java.util.Collections;

import twitter4j.Trend;
import com.cmpe277.android.tweeter.views.TrendListItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TrendListAdapter extends BaseAdapter {

	private ArrayList<Trend> trends;
	private Context context;
	
	public TrendListAdapter(Context context, ArrayList<Trend> trends) {
		this.trends = trends;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return trends.size();
	}

	@Override
	public Object getItem(int position) {
		return (null == trends) ? null : trends.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TrendListItem tli;
		if (convertView == null) {
			tli = (TrendListItem)View.inflate(context, com.cmpe277.android.tweeter.R.layout.trend_list_item, null);
		} else {
			tli = (TrendListItem)convertView;
		}
		tli.setTrendItemName(trends.get(position).getName());
		
		return tli;
	}
	
	public void setTrends(Trend[] trendsArray) {
		Collections.addAll(trends,trendsArray); 
		notifyDataSetChanged();
	}
	
	public void forceReload() {
		notifyDataSetChanged();
	}

}
