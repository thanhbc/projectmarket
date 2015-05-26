package com.thanhbc.market;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.thanhbc.fragments.NavigationDrawerFragment;
import com.thanhbc.fragments.NavigationDrawerFragment.NavigationDrawerCallBacks;
import com.thanhbc.market.menus.MainFragment;
import com.thanhbc.market.menus.HomeFragment;

public class MainActivity extends FragmentActivity implements
		NavigationDrawerCallBacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);

		setContentView(R.layout.activity_main);

		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		 FragmentTransaction ft =
		 getSupportFragmentManager().beginTransaction();
		 Fragment fragment;
		 switch (position) {
		 case 0:
		 fragment = getSupportFragmentManager().findFragmentByTag("MainFragment");
		 if(fragment==null){
		 ft.replace(R.id.container, new MainFragment(), "MainFragment");
		 }else{
			 ft.replace(R.id.container, fragment, "MainFragment");
		 }
		 ft.commit();
		 
		 break;
		
		 default:
		 break;
		 }

	}

	}
