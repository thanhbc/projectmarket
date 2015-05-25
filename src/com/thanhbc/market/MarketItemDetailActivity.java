package com.thanhbc.market;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.thanhbc.fragments.MarketItemDetailFragment;

public class MarketItemDetailActivity  extends Activity{
	
	ActionBar actionBar;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;		
		getWindow().setAttributes(attrs);
		
		setContentView(R.layout.acitivty_details);
		actionBar = getActionBar();
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		
		Bundle dataLink = getIntent().getExtras();
				
		Log.d("THANHBC", dataLink.getString("linkDetail"));
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.details, MarketItemDetailFragment.newInstance(dataLink.getString("linkDetail")),"MarketItemDetailFragment");
		ft.commit();
		
	};

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == android.R.id.home) {
		finish();
	}
	return super.onOptionsItemSelected(item);
}	

}
