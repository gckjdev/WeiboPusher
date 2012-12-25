package com.orange.weiboservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.orange.game.constants.ServiceConstant;

public class ContestWeiboContent extends CommonWeiboContent {
	
	
	public enum WeiboType {CONTEST_START, CONTEST_ENDING};
	
   // contestInfoUrl, gives response: subject, start date, ending date, total participators, etc
   private final String contestInfoUrl = 
	   "http://58.215.184.18:8100/api/i?&m=gcl&uid=4f95717e260967aa715a5af4&tp=0&lang=1&os=0&ct=1";
   // opusUrl, gives response: subject, opus 
	private final String opusUrl;
	
	
	// to store http response
	private String contestInfoResponse;
	private String OpusResponse;
		
	// for contest subject
	private String contestSubject;
	// for total  participator number;
	private int participatorCount;
	// for start date / ending date
	private String startDate;
	private String endingDate;
	// for contest poster
	private String posterUrl;

	private enum ContentType { CONTEST_INFO, CONTEST_OPUS };
	public ContestWeiboContent(WeiboType type, int topCountAward, String contestID) {
			
		opusUrl = "http://58.215.184.18:8100/api?m=gcto&lang=1&ct="+topCountAward+"&os=0&cid="
				+ contestID;
		
		contestInfoResponse = query(contestInfoUrl);
		parseJson(contestInfoResponse, ContentType.CONTEST_INFO);
		
		if ( type == WeiboType.CONTEST_ENDING ) {
			OpusResponse = query(opusUrl);
			parseJson(OpusResponse, ContentType.CONTEST_OPUS);
		}
	}
		
		 
	 private void parseJson(String result, ContentType contentType) {
			 
		   if ( result == null ) {
		    	return;
		    }
		   
			JSONObject jObject = JSONObject.fromObject(result);
			
			if ( contentType == ContentType.CONTEST_INFO ) {
				JSONObject data = (JSONObject)jObject.getJSONArray("dat").get(0);
				contestSubject = (String) data.get("tt");
				startDate = toDateString((Integer) data.get("sd"));
				endingDate = toDateString((Integer) data.get("ed"));
				posterUrl = (String) data.get("cu");
				participatorCount = (Integer)data.get("pc"); // of course, this field is valid only at ending.
			} 
			else if ( contentType == ContentType.CONTEST_OPUS ) {
				JSONObject data = jObject.getJSONObject("dat");
				contestSubject = (String)data.get("tt");
				JSONArray jArray = data.getJSONArray("lt");
				for ( Object object : jArray ) {
					userIdList.add( ((JSONObject)object).getString(ServiceConstant.PARA_USERID));
					urlList.add( ((JSONObject)object).getString(ServiceConstant.PARA_IMAGE_URL));
				}
			}
	}
			
		
	private String toDateString(int seconds) {
			
		Calendar c = new GregorianCalendar(); 
		c.setTimeInMillis( (long)seconds * 1000 );
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		    
		return format.format(c.getTime());
	}

		
	public String getContestSubject() {
		return contestSubject;
	}
	
		
	public int getParticipatorCount() {
		return  participatorCount;
	}
		
		
	public String getStartDateString() {
		return startDate;
	}
		
		
	public String getEndingDateString() {
		return endingDate;
	}
		
		
	public String getPosterUrl() {
		return posterUrl;
	}
}
