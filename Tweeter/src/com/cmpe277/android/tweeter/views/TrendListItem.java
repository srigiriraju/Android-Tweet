package com.cmpe277.android.tweeter.views;

import com.cmpe277.android.tweeter.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrendListItem extends RelativeLayout {

	private TextView trendName;
	
	public TrendListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		trendName = (TextView)findViewById(R.id.trend_text);
	}
	
	public void setTrendItemName(String name){
		trendName.setText(name);
	}
	
	public String getTrendItemName(){
		return trendName.getText().toString();
	}
}
