package com.thanhbc.market;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.thanhbc.fragments.NavigationDrawerFragment;
import com.thanhbc.fragments.NavigationDrawerFragment.NavigationDrawerCallBacks;
import com.thanhbc.market.menus.HomeFragment;

public class MainActivity extends Activity implements NavigationDrawerCallBacks {

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
	public void onNavigationDrawerItemSelected(int position) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		
		switch (position) {
		case 0:
			ft.replace(R.id.container, new HomeFragment(), "HomeFragment");
			ft.commit();			
			break;

		default:
			break;
		}
					
	}
}
