package com.orange.weiboservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.game.constants.ServiceConstant;
import com.orange.game.model.dao.User;
import com.orange.game.model.manager.UserManager;



public class WeiboContent {

	private static Logger logger = Logger.getLogger("WeiboContent");
	private static MongoDBClient mongoClient = new MongoDBClient("game");
	
	// only for top 3 drawings.
	private final static int COUNT = 3; 
	
//	String url = "http://58.215.184.18:8100/api?m=gtow&lang=1&ct=3&os=0";
	String url = "http://192.168.1.13:8100/api?m=gtow&lang=1&ct=3&os=0";
	String returnResult;
	
	 // for top 3 users' userId
	 List<String> userIdList = new ArrayList<String>();
	 // for top 3 drawings' url
	 List<String>  urlList = new ArrayList<String>();
	 // for top 3 drawings' keyword
	 List<String> wordList = new ArrayList<String>();

	public WeiboContent() {
		try {
			returnResult = sendGet(url);
		} catch (IOException e) {
			logger.info("Get weibo conten failed!!!");
			e.printStackTrace();
		}
		parseJson(returnResult);
	}
	
	 private String sendGet(String url) throws IOException {  
	        String result = "";  
	        BufferedReader in = null;  
	        java.net.URL connURL = new java.net.URL(url);  
	        
	        java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL  
	                    .openConnection();  
	            // 设置通用属性  
	        httpConn.setRequestProperty("Accept", "*/*");  
	        httpConn.setRequestProperty("Connection", "Keep-Alive");  
	        httpConn.setRequestProperty("User-Agent",  
	                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");  
	       try {
	            // 建立实际的连接  
	         httpConn.connect();  
	         Map<String, java.util.List<String>> headers = httpConn.getHeaderFields();  
	         for (String key : headers.keySet()) {  
	             System.out.println(key + "\t：\t" + headers.get(key));  
	          	}  
	         in = new BufferedReader(new InputStreamReader(httpConn  
	                    .getInputStream(), "UTF-8"));  
	         
	         // 读取返回的内容  
	         String line;  
	         while ((line = in.readLine()) != null) {  
	             result += line;  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            try {  
	                if (in != null) {  
	                    in.close();  
	                }  
	            } catch (IOException ex) {  
	                ex.printStackTrace();  
	            }  
	        }  
	       System.out.println(result);
	       return result;  
	    }

	 private void parseJson(String result) {
		 // result looks like: {"dat":[...],"ret":0}
		 // we just want the part: [...]
		 int beginIndex = result.indexOf('['); 
		 int endIndex = result.indexOf(']')+1;
		 System.out.println("beginIndex="+beginIndex+", endIndex="+endIndex);
		 String jString = result.substring(beginIndex, endIndex);
		 System.out.println(jString);
		 
		 JSONArray jArray = JSONArray.fromObject(jString);
		 for ( Object jObject : jArray ) {
			 userIdList.add( ((JSONObject)jObject).getString(ServiceConstant.PARA_USERID));
			 urlList.add( ((JSONObject)jObject).getString(ServiceConstant.PARA_IMAGE_URL));
			 wordList.add( ((JSONObject)jObject).getString(ServiceConstant.PARA_WORD));
		 }
	 }
	 
	public String getdrawing(int i) {
		if ( i < 0 || i+ 1  > COUNT ) 
			return null;
		String url = "/data/draw_image/";
		url += urlList.get(i);
		
		return url;
	}

	public String getSinaNickName(int i) {
		if ( i < 0 || i+ 1  > COUNT ) 
			return null;
		String uid = userIdList.get(i);
		User user = UserManager.findUserByUserId(mongoClient, uid);
		String sinaNickName = user.getSinaNickName();
		return sinaNickName;
	}

	public String getWord(int i) {
		if ( i < 0 || i+ 1  > COUNT ) 
			return null;
		String word = wordList.get(i);
		return word;
	}  
	    
}
