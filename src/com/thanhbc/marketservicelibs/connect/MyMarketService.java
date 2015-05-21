package com.thanhbc.marketservicelibs.connect;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.thanhbc.marketservicelibs.utils.ParseJson;

public abstract class MyMarketService extends AsyncTask implements DataReceiver{
	
	
	
	
	private ProgressDialog mProgressDialog;
	private Activity mActivity;
	
	public MyMarketService (Activity activity) {
		mActivity=activity;
	}
	
	public abstract void receiveData(int status, String data);
	
	public void getAllMarketItem(){
		JSONObject jsonObject = new JSONObject();
	  execute(new String[]{"","https://dl.dropboxusercontent.com/s/byekjw85sil3293/jsonMarket.txt?dl=0"});
	}
	
	
	public abstract ProgressDialog showWaitingDialog();
	
	@Override
	protected void onPreExecute() {
		 mProgressDialog = showWaitingDialog();
	        if(mProgressDialog != null)
	        {
	            mProgressDialog.show();
	        }
	        super.onPreExecute();
	}
	
	
	protected String doInBackground(String incomingParams[]) {
		HttpsURLConnection connection;
		
		
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			   e.printStackTrace();
		}
		
		
		connection=null;
		String s;
		try {
			URL url = new URL(incomingParams[1]);
			connection = (HttpsURLConnection)url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("User-Agent", "");
	        connection.setDoOutput(true);
	        connection.setInstanceFollowRedirects(true);
	        //put params 
	        OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
	        outputStream.write(incomingParams[0].getBytes());
	        outputStream.flush();
	        outputStream.close();
	        
	        InputStream inputStream = connection.getInputStream();
	        
	        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	        StringBuffer sb = new StringBuffer();
	        String line;
	        while((line=br.readLine())!=null){
	        	sb.append(line);
	        	sb.append('\r');
	        }
	        br.close();
	        s = sb.toString();
	        if(connection != null)
	        {
	            connection.disconnect();
	        }
	        return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
		
	protected void onPostExecute(String result) {
		if(mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
		
		if(result!=null){
			int status =ParseJson.getResultStatus(result);
			String data= ParseJson.getResultMessage(result);
			receiveData(status, data);
		}else{
			receiveData(500, "Could not connect to server");
		}
		
	}
	

    protected void onPostExecute(Object obj)
    {
        onPostExecute((String)obj);
    }

    protected Object doInBackground(Object... aobj)
    {
        return doInBackground((String[])aobj);
    }

}
