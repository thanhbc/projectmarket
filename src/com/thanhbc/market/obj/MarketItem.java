package com.thanhbc.market.obj;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarketItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;	
	private String linkDetail;
	public String getLinkDetail() {
		return linkDetail;
	}
	public void setLinkDetail(String linkDetail) {
		this.linkDetail = linkDetail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImgLink() {
		return imgLink;
	}
	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}
	
	private String imgLink;
	
public static ArrayList<MarketItem> parseValue(String data) {
		
		ArrayList<MarketItem> shopItems = new ArrayList<MarketItem>();
		
		try {
			
			JSONArray jsonArray = new JSONArray(data);
			
			for (int i=0; i<jsonArray.length(); i++) {
				
				JSONObject  jsonObject = jsonArray.getJSONObject(i);
				
				MarketItem item = new MarketItem();
				item.setName(jsonObject.getString("name"));				
				item.setImgLink(jsonObject.getString("imgLink"));
				item.setLinkDetail(jsonObject.getString("linkDetails"));
				shopItems.add(item);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return shopItems;
	}

}
