package com.thanhbc.marketservicelibs.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseJson {

	public static int getResultStatus(String data)
    {
        try {
        	 JSONObject jsonObject = new JSONObject(data);
 	        return jsonObject.getInt("result");
		} catch (JSONException e) {						       
	        e.printStackTrace();
		}
       
        
        return 0x29c51;
    }

public static String getResultMessage(String data)
    {
        try {
        	JSONObject jsonObject = new JSONObject(data);
        	return jsonObject.getString("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}	        	        	        	     
        
        return null;
    }

}
