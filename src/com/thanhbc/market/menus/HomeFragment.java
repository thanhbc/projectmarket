package com.thanhbc.market.menus;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.thanhbc.marketservicelibs.connect.MyMarketService;

public class HomeFragment extends Fragment {

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

		showLoading(loadingImage);

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

		Toast.makeText(getActivity(), "This is toast from HomeFragment",
				Toast.LENGTH_SHORT).show();
		return mainView;
	}

	private void showLoading(ImageView loadingImage) {
		try {
			BitmapDrawable frame1 = (BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo1);
			BitmapDrawable frame2 = (BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo2);
			BitmapDrawable frame3 = (BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo3);
			BitmapDrawable frame4 = (BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo4);
			BitmapDrawable frame5 = (BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo5);
			BitmapDrawable frame6 = (BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo6);
			BitmapDrawable frame7 = (BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo7);

			AnimationDrawable Anim = new AnimationDrawable();
			Anim.addFrame(frame1, 100);
			Anim.addFrame(frame2, 100);
			Anim.addFrame(frame3, 100);
			Anim.addFrame(frame4, 100);
			Anim.addFrame(frame5, 100);
			Anim.addFrame(frame6, 100);
			Anim.setOneShot(false);
			loadingImage.setBackgroundDrawable(Anim);
			 Anim.start();
		} catch (Exception e) {

		}
	}

}
