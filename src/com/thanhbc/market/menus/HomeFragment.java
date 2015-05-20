package com.thanhbc.market.menus;


import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.thanhbc.market.R;
import com.thanhbc.market.adapter.MarketItemAdapter;
import com.thanhbc.market.obj.MarketItem;

public class HomeFragment extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	GridView rootView = (GridView) inflater.inflate(R.layout.fragment_market_items, null);
	
	DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
	int columnWidth = 350;
	int numColumn = (int)(displayMetrics.widthPixels/columnWidth);
	
	columnWidth = displayMetrics.widthPixels/numColumn;
	rootView.setNumColumns(numColumn);
	rootView.setVerticalSpacing(5);
	rootView.setHorizontalSpacing(5);
	
	MarketItem item1 = new MarketItem();
	MarketItem item2 = new MarketItem();
	item1.setName("App1");
	item2.setName("App2");
	ArrayList<MarketItem> items = new ArrayList<MarketItem>();
	items.add(item1);
	items.add(item2);
	
	MarketItemAdapter adapter = new MarketItemAdapter(getActivity(), columnWidth);
	adapter.setMarketItems(items);
	
	rootView.setAdapter(adapter);
	
	Toast.makeText(getActivity(), "This is toast from HomeFragment", Toast.LENGTH_SHORT).show();
	return rootView;
	}

}
