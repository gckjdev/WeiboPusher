package com.orange.weiboservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.orange.common.log.ServerLog;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.game.model.dao.User;
import com.orange.game.model.manager.UserManager;
import com.orange.game.traffic.service.GameDBService;

public class CommonWeiboContent {
	
	protected final static MongoDBClient dbClient = GameDBService.getInstance().getMongoDBClient(0);
	
	 // for users' userId
	 protected List<String> userIdList = new ArrayList<String>();
	 // for drawings' url
	 protected List<String>  urlList = new ArrayList<String>();
	 // for drawings' keyword
	 protected List<String> wordList = new ArrayList<String>();
	
    protected String query(String url) {
		 
	     String result = "";  
	     BufferedReader in = null;  
	     
	     try {
	    	  URL connURL = new java.net.URL(url);  
	    	  HttpURLConnection httpConn = (HttpURLConnection)connURL.openConnection();  
	           // 建立实际的连接  
	        httpConn.connect();  
	        in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));  
	         
	          // 读取返回的内容  
	       String line;  
	       while ((line = in.readLine()) != null) {  
	           result += line;  
	          }  
	     } catch(MalformedURLException me) {
			 	ServerLog.info(0, "<ContestWeiboContent> Wrong query URL for img search, please check: " + url);
	     } catch(IOException ie) {
	    	 	ServerLog.info(0, "<ContestWeiboContent> Query for contest weibo content fails due to " + ie.toString());
	     } catch (Exception e) {
	    	 	ServerLog.info(0, "<ContestWeiboContent> Query for contest weibo content fails due to " + e.toString());
	     } finally {  
	    	 	try {  
		           if (in != null) {  
		              in.close();  
		               }  
		       } catch (IOException ex) {  
		           ex.printStackTrace();  
		         }  
	        }  
	      
	     return result;  
	 }
    

  protected User getUser(int i) {
		
		User user = null; 
		String userId = null;
		try {
			userId = userIdList.get(i);
		} catch(IndexOutOfBoundsException e) {
			ServerLog.info(0,"<WeiboContent>Get user fails, caused by " + e);
			return null;
		}
		user = UserManager.findUserByUserId(dbClient, userId);
		return user;
	}
	
	public String getUserId(int i) {
		
		String userId = null; 
		try {
			userId = userIdList.get(i);
		} catch (IndexOutOfBoundsException e) {
			ServerLog.info(0,"<WeiboContent>Get userId fails, caused by " + e);
		}
		
		return userId;
	}

	public String getdrawing(int i) {
		
		String url = null;
		try {
			url = "/data/draw_image/" + urlList.get(i); ; ///Library/WebServer/Documents/draw_image/";
		} catch (IndexOutOfBoundsException e) {
			ServerLog.info(0,"<WeiboContent>Get drawing path fails, caused by " + e);
		}
		
		return url;
	}
	
	public String getSinaNickName(int i) {
		User user = getUser(i);
		if (user == null) {
			return null;
		}
		String sinaNickName = user.getSinaNickName();
		return sinaNickName;
	}
	
	public String getQQId(int i) {
		User user = getUser(i);
		if (user == null) {
			return null;
		}
		String QQId = user.getQQID();
		return QQId;
	}

	public String getWord(int i) {
		
		String word = null;
		try {
			word = wordList.get(i);
		} catch (IndexOutOfBoundsException e) {
			ServerLog.info(0,"<WeiboContent>Get word fails, caused by " + e);
		}
		
		return word;
	}

	public String getNickName(int i) {
		User user = getUser(i);
		if (user == null) {
			return null;
		}
		return user.getNickName();
	}  

	
}
