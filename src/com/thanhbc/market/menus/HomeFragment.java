package com.thanhbc.market.menus;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thanhbc.market.R;
import com.thanhbc.market.adapter.MarketItemAdapter;
import com.thanhbc.market.obj.MarketItem;
import com.thanhbc.market.obj.Serializer;
import com.thanhbc.marketservicelibs.connect.MyMarketService;

public class HomeFragment extends Fragment {

	ArrayList<MarketItem> items = new ArrayList<MarketItem>();

	MarketItemAdapter adapter;
	
	
	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			try {
				// Restore State
				Log.d("THANHBC", "Restore State");
				items = (ArrayList<MarketItem>) Serializer
						.deserialize(savedInstanceState
								.getByteArray("MarketItem"));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		if(savedInstanceState!=null){
			try {
				Log.d("THANHBC", "onViewStateRestored");
				items = (ArrayList<MarketItem>) Serializer.deserialize(savedInstanceState.getByteArray("MarketItem"));
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			Log.d("THANHBC", "SaveState");
			outState.putByteArray("MarketItem", Serializer.serialize(items));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {		
		super.onViewCreated(view, savedInstanceState);
		if(savedInstanceState==null){
			Bundle bundle = new Bundle();
			onSaveInstanceState(bundle);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		LinearLayout mainView = (LinearLayout) inflater.inflate(
				R.layout.fragment_market_items, null);
		final RelativeLayout loadingLayout = (RelativeLayout) mainView
				.findViewById(R.id.loadingPanel);
		// ImageView loadingImage = (ImageView) loadingLayout
		// .findViewById(R.id.loadingImage);
		GridView rootView = (GridView) mainView.findViewById(R.id.gridView1);

		// Loading.showLoading(loadingImage,getActivity());

		DisplayMetrics displayMetrics = getActivity().getResources()
				.getDisplayMetrics();
		int columnWidth = 350;
		int numColumn = (int) (displayMetrics.widthPixels / columnWidth);

		columnWidth = displayMetrics.widthPixels / numColumn;
		rootView.setNumColumns(numColumn);
		rootView.setVerticalSpacing(5);
		rootView.setHorizontalSpacing(5);

		adapter = new MarketItemAdapter(getActivity(), columnWidth);
		if (savedInstanceState == null) {
			
			MyMarketService service = new MyMarketService(getActivity()) {

				@Override
				public ProgressDialog showWaitingDialog() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void receiveData(int status, String data) {
					if (status == 1) {
						items.addAll(MarketItem.parseValue(data));
						adapter.setMarketItems(items);
					}

				}

				@Override
				public RelativeLayout loadingPanel() {
					return loadingLayout;
				}
			};

			service.getAllMarketItem();
			//Toast.makeText(getActivity(), "Getting data from server....",Toast.LENGTH_SHORT).show();
		}else{
			adapter.setMarketItems(items);
		}

		// MarketItem item1 = new MarketItem();
		// MarketItem item2 = new MarketItem();
		// item1.setName("App1");
		// item2.setName("App2");
		//
		// items.add(item1);
		// items.add(item2);

		// adapter.setMarketItems(items);
		//
		rootView.setAdapter(adapter);

		// Toast.makeText(getActivity(), "Getting data from server....",
		// Toast.LENGTH_SHORT).show();
		return mainView;
	}
	
	

}
