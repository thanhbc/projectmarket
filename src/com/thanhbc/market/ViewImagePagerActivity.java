package com.thanhbc.market;

import java.util.ArrayList;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.thanhbc.fragments.ImageFragment;

public class ViewImagePagerActivity extends FragmentActivity{	
	
	ActionBar actionBar;
	@Override
	protected void onCreate(Bundle arg0) {
	
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_view_pager);
		
		ViewPager pager= (ViewPager) findViewById(R.id.viewPager);
		
		ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager());
		adapter.setAllItems((getIntent().getStringArrayListExtra("imgLink")));
		Log.d("THANHBC", "list size in ViewImagePager" + getIntent().getStringArrayListExtra("imgLink").size());
		pager.setAdapter(adapter);		
		
		int pos = getIntent().getIntExtra("pos", 0);
				
		pager.setCurrentItem(pos);
		
		actionBar = getActionBar();
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	
	private class ImagePagerAdapter extends FragmentPagerAdapter{

		ArrayList<String > items= new ArrayList<String>();
		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}
		
		public void setAllItems(ArrayList<String> items){
			this.items=items;
			this.notifyDataSetChanged();
		}
				

		@Override
		public Fragment getItem(int position) {
			Log.d("THANHBC", "list size in ImagePager Adapter" + items.size());
			// TODO Auto-generated method stub
			return ImageFragment.newInstance(position, items);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}
		
	}
}
