package com.thanhbc.market.menus;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.thanhbc.market.R;
import com.thanhbc.market.obj.Serializer;

public class MainFragment extends Fragment implements TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	ActionBar mActionBar;
	private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.activity_home, null);
		
		mActionBar = getActivity().getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
		mViewPager  = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
			}
		});
		
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			mActionBar.addTab(mActionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		return rootView;
	}	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		getRetainInstance();
	}
	
	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		getRetainInstance();
//		if(savedInstanceState!=null){
//			try {
//				mFragments = (ArrayList<Fragment>) Serializer.deserialize(savedInstanceState.getByteArray("Fragments"));
//			} catch (ClassNotFoundException | IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
		
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);		
//		try {
//			outState.put("Fragments", Serializer.serialize(mFragments));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
		

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		FragmentTransaction mTransaction;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
				

		@Override
		public Fragment getItem(int position) {			
				mFragments.add(position, HomeFragment.newInstance());									
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			switch (position) {
			case 0:

				return "Application";
			case 1:
				return "Game";
			case 2:
				return "Tutotial";
			}
			return null;
		}

		public String getTag(int position) {
			return mFragments.get(position).getClass().getSimpleName();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			return super.instantiateItem(container, position);		
		}
		
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);

			if (position <= getCount()) {
				FragmentManager manager = ((Fragment) object)
						.getFragmentManager();
				FragmentTransaction trans = manager.beginTransaction();
				trans.remove((Fragment) object);
				trans.commit();
			}
		}
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

}
