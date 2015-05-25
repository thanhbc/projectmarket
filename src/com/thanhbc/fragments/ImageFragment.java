package com.thanhbc.fragments;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thanhbc.libs.imageview.PhotoView;
import com.thanhbc.market.R;

public class ImageFragment extends Fragment{

	
	private static final String IMAGE_POSITION="position";
	private static final String IMAGE_LIST_LINK="list_link";
	
	private int pos ;
	private ArrayList<String> link=new ArrayList<String>();
	
	public static ImageFragment newInstance(int position, ArrayList<String> listImg){
		ImageFragment fragment = new ImageFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(IMAGE_POSITION, position);
		bundle.putStringArrayList(IMAGE_LIST_LINK,listImg);				
		Log.d("THANHBC", "list size" + listImg.size());
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		pos = getArguments().getInt(IMAGE_POSITION);
		link= getArguments().getStringArrayList(IMAGE_LIST_LINK);
		Log.d("THANHBC", "list size 2" + link.size());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
//		View  v = inflater.inflate(R.layout.list_item_details_img,container, false);	
//	PhotoView imageView = (PhotoView) v.findViewById(R.id.imgDescription);
		PhotoView imageView = new PhotoView(getActivity());
	String imgLink = link.get(pos).toString();
	
	Drawable mEmptyDrawable = getActivity().getResources().getDrawable(R.drawable.empty_photo);
	try {
		imageView.setImageURL(new URL(imgLink), true, mEmptyDrawable);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return imageView;
	}
	
	
}
