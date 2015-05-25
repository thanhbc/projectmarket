package com.thanhbc.fragments;

import java.net.URL;
import java.util.ArrayList;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thanhbc.libs.horizontallistview.HorizontalListView;
import com.thanhbc.libs.imageview.PhotoView;
import com.thanhbc.market.R;
import com.thanhbc.market.ViewImagePagerActivity;
import com.thanhbc.market.adapter.MarketItemDetailImageAdapter;
import com.thanhbc.market.obj.MarketItemDetail;
import com.thanhbc.market.utils.Loading;
import com.thanhbc.marketservicelibs.connect.MyMarketService;

public class MarketItemDetailFragment extends Fragment implements
		OnItemClickListener {

	MarketItemDetailImageAdapter imgAdapter;
		
	String linkDetails = "";

	public static MarketItemDetailFragment newInstance(String linkDetails) {
		MarketItemDetailFragment fragment = new MarketItemDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString("linkDetails", linkDetails);	
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(
				R.layout.item_detail_layout, null);
		final TextView appName = (TextView) rootView.findViewById(R.id.appName);
		final TextView appDescription = (TextView) rootView
				.findViewById(R.id.appDescription);
		final PhotoView appImg = (PhotoView) rootView
				.findViewById(R.id.imgDetail);
		
		final RelativeLayout loadingLayout = (RelativeLayout) rootView
				.findViewById(R.id.loadingPanel);
		ImageView loadingImage = (ImageView) loadingLayout
				.findViewById(R.id.loadingImage);
		Loading.showLoading(loadingImage, getActivity());
		
		HorizontalListView imgDescription = (HorizontalListView) rootView
				.findViewById(R.id.gridImgDescription);

		imgAdapter = new MarketItemDetailImageAdapter(getActivity());
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
						ArrayList<MarketItemDetail> item=  MarketItemDetail.parserValue(data);
						
						Log.d("THANHBC", item.size()+"");
						MarketItemDetail itemDetail =item.get(0);
						imgAdapter.setAllImgLink(itemDetail.getListImgLink());
						appName.setText(itemDetail.getAppName());
						Drawable mEmptyDrawable = getActivity().getResources()
								.getDrawable(R.drawable.empty_photo);
						appDescription.setText(itemDetail.getAppDescription());
						try {
							appImg.setImageURL(
									new URL(itemDetail.getAppImgLink()), true,
									mEmptyDrawable);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}

				@Override
				public RelativeLayout loadingPanel() {
					// TODO Auto-generated method stub
					return loadingLayout;
				}
			};
			service.getMarketItemDetails(getArguments().getString("linkDetails"));
		}

		imgDescription.setAdapter(imgAdapter);		
		imgDescription.setOnItemClickListener(this);
		return rootView;
	}

		
	@Override
	public void onItemClick(AdapterView<?> groupView, View view, int position, long id) {
		Intent intent = new Intent(getActivity(),ViewImagePagerActivity.class);
		
		Bundle bundle = null;
		
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
			bundle=ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();			
		}
		
		if(bundle!=null){
			intent.putExtras(bundle);		
			
		}
		
		//intent.putExtra("imgLink", imgAdapter.getAllImgLink());
		intent.putExtra("pos",position);
		intent.putStringArrayListExtra("imgLink", imgAdapter.getAllImgLink());		
		//Toast.makeText(getActivity(), "CLick on image " + position , Toast.LENGTH_SHORT).show();
		startActivity(intent);

	}

}
