package com.thanhbc.market.obj;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarketItemDetail implements Serializable{

	private static final long serialVersionUID = 1L;
	private String appName; 
	private String appDescription;
	private String appImgLink;
	private ArrayList<String> listImgLink;
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppDescription() {
		return appDescription;
	}
	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}
	public String getAppImgLink() {
		return appImgLink;
	}
	public void setAppImgLink(String appImgLink) {
		this.appImgLink = appImgLink;
	}
	public ArrayList<String> getListImgLink() {
		return listImgLink;
	}
	public void setListImgLink(ArrayList<String> listImgLink) {
		this.listImgLink = listImgLink;
	}
	
	public static ArrayList<MarketItemDetail> parserValue(String data){
		ArrayList<MarketItemDetail> listItems = new ArrayList<MarketItemDetail>();

		try {
			
			JSONArray jsonArray = new JSONArray(data);
			
			for (int i=0; i<jsonArray.length(); i++) {
				
				JSONObject  jsonObject = jsonArray.getJSONObject(i);
				
				MarketItemDetail item = new MarketItemDetail();
				item.setAppName(jsonObject.getString("name"));				
				item.setAppImgLink(jsonObject.getString("imgLink"));
				item.setAppDescription(jsonObject.getString("desc"));
				JSONArray imgDescriptionLinks = new JSONArray(jsonObject.getString("imgDescriptionLinks"));
				ArrayList<String> imgLink = new ArrayList<String>();
				for (int j = 0; j < imgDescriptionLinks.length(); j++) {					
					JSONObject  jsonObject1 = imgDescriptionLinks.getJSONObject(i);
					imgLink.add(jsonObject1.getString("link"));					
				}
				item.setListImgLink(imgLink);
				listItems.add(item);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listItems;
	}
}
