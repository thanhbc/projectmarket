package com.thanhbc.fragments;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thanhbc.market.R;
import com.thanhbc.market.adapter.MarketItemAdapter;
import com.thanhbc.market.obj.MarketItem;
import com.thanhbc.market.utils.Loading;
import com.thanhbc.marketservicelibs.connect.MyMarketService;

public class MarketItemCategoryFragment extends Fragment{


	ArrayList<MarketItem> items = new ArrayList<MarketItem>();

	MarketItemAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout mainView = (LinearLayout) inflater.inflate(
				R.layout.fragment_market_items, null);
		final RelativeLayout loadingLayout = (RelativeLayout) mainView
				.findViewById(R.id.loadingPanel);
		ImageView loadingImage = (ImageView) loadingLayout
				.findViewById(R.id.loadingImage);
		GridView rootView = (GridView) mainView.findViewById(R.id.gridView1);

		Loading.showLoading(loadingImage,getActivity());

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

		Toast.makeText(getActivity(), "Getting data from server....",
				Toast.LENGTH_SHORT).show();
		return mainView;
	}
	

}
