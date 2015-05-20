package com.thanhbc.market.adapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.thanhbc.libs.imageview.PhotoView;
import com.thanhbc.market.R;
import com.thanhbc.market.obj.MarketItem;

public class MarketItemAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<MarketItem> items = new ArrayList<MarketItem>();
	
	private int colHeight;
	public MarketItemAdapter(Context context, int colHeight) {
		this.context = context;
		this.colHeight =colHeight;
	}
	
	public void setMarketItems(ArrayList<MarketItem> items){
		this.items = items;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder= null;
		if(convertView==null){
			holder = new ViewHolder();
			
			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_app, null);
			
			holder.textName = (TextView) convertView.findViewById(R.id.textName);
			holder.img = (PhotoView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.textName.setText(items.get(position).getName());
		LayoutParams params = new LayoutParams(colHeight, colHeight);
		
		holder.img.setLayoutParams(params);
		
		Drawable mEmptyDrawable = context.getResources().getDrawable(R.drawable.empty_photo);
		
		try {
			holder.img.setImageURL(new URL(items.get(position).getImgLink()), true, mEmptyDrawable);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertView;
	}

	
private class ViewHolder{

	private TextView textName;	
	private PhotoView img; 
}
}
