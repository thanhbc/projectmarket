package com.thanhbc.market.adapter;

import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;

import com.thanhbc.libs.imageview.PhotoView;
import com.thanhbc.market.R;

public class MarketItemDetailImageAdapter extends BaseAdapter {
	
	private Context context;
	ArrayList<String> listLinkImgs= new ArrayList<String>();
	public MarketItemDetailImageAdapter(Context context) {
		this.context=context;
	}
	
	public void setAllImgLink(ArrayList<String> links) {
		this.listLinkImgs = links;
		notifyDataSetChanged();
	}
	
	public ArrayList<String> getAllImgLink(){
		return this.listLinkImgs;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listLinkImgs.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return listLinkImgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder= null;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_details_img, null);
				holder.imgLink = (PhotoView) convertView.findViewById(R.id.imgDescription);
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();			
			}
//			LayoutParams params = new LayoutParams(240, 240);
//			holder.imgLink.setLayoutParams(params);
			
			Drawable mEmptyDrawable = context.getResources().getDrawable(R.drawable.empty_photo);
			try {
				holder.imgLink.setImageURL(new URL(listLinkImgs.get(position)), true, mEmptyDrawable);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			return convertView;
		}
		
		private class ViewHolder{
			private PhotoView imgLink;
		}
	}
	
	