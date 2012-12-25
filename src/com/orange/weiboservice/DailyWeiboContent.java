package com.orange.weiboservice;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.orange.game.constants.ServiceConstant;



public class DailyWeiboContent extends CommonWeiboContent {

	private String returnResult;
	
	public DailyWeiboContent(int topCount) {
		
		String url = "http://58.215.184.18:8100/api?m=gtow&lang=1&ct="+topCount+"&os=0";
		returnResult = query(url);
		if ( returnResult != null ) {
			parseJson(returnResult);
		}
	}

	private void parseJson(String result) {

		if ( result == null ) 
			return;
		 
		JSONObject jObject = JSONObject.fromObject(result);
		JSONArray jArray = jObject.getJSONArray("dat");
		for ( Object object : jArray ) {
			 userIdList.add( ((JSONObject)object).getString(ServiceConstant.PARA_USERID));
			 urlList.add( ((JSONObject)object).getString(ServiceConstant.PARA_IMAGE_URL));
			 wordList.add( ((JSONObject)object).getString(ServiceConstant.PARA_WORD));
		}
	 }
	 

		    
}
