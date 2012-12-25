package com.orange.weiboservice;


import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.oauthv2.OAuthV2;

/**
 * Get AccessToken
 * step 1 :
 * https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id=801123669&response_type=code&redirect_uri=http://caicaihuahua.me
 *    then write down the [code] and the [openkey]
 *    
 * step 2 :
 *  https://open.t.qq.com/cgi-bin/oauth2/access_token?client_id=801123669&client_secret=30169d80923b984109ee24ade9914a5c&redirect_uri=http://caicaihuahua.me&grant_type=authorization_code&code=YOUR_CODE
 *  	then write down the [access token]
 *  
 *  
 *  Renew access token
 *  https://open.t.qq.com/cgi-bin/oauth2/access_token?client_id=801123669&grant_type=refresh_token&refresh_token=57ef56fd40eabb4d55715b389bd3ba41
 */
public class TencentWeibo {


	private final  CommonWeiboContent weiboContent;
	
	public TencentWeibo(CommonWeiboContent content) {
		this.weiboContent = content;
	}
	
	public void sendDailyTencentWeibo(String accessToken, String openKey, int topCount) {

		for (int i = topCount-1; i >= 0; i--) {
			String drawingPath = weiboContent.getdrawing(i);
			String QQId = weiboContent.getQQId(i);
			String word = weiboContent.getWord(i);
			
			if (QQId == null) {
				QQId = "玩家" + weiboContent.getNickName(i);
			} else {
				QQId = "@"+QQId;
			}
			String text = "今日#猜猜画画作品榜#第" + (i + 1) + "名：" + QQId + " 的【" + word
			+ "】。欣赏每日精彩涂鸦, 获取猜猜画画最新动态，敬请关注@drawlively 。";

			sendOneTencentWeibo(accessToken, openKey, drawingPath, text);
//			sendOneTencentWeibo(accessToken, openKey, "/home/larmbr/Downloads/dog.jpg", "最后一条测试微博"+i);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void sendContestTencentWeibo(String accessToken, String openKey, int topCount) {
		
		for (int i = topCount-1; i >= 0; i--) {
			String drawingPath = weiboContent.getdrawing(i);
			String QQId = weiboContent.getQQId(i);
			String contestSubject = ((ContestWeiboContent)weiboContent).getContestSubject();
			int participatorCount = ((ContestWeiboContent)weiboContent).getParticipatorCount();
			
			if (QQId == null) {
				QQId = "玩家" + weiboContent.getNickName(i);
			} else {
				QQId = "@"+QQId;
			}
			String text = "#"+contestSubject+"#结束啦！  恭喜" + QQId + " 在参赛的"+participatorCount+"名玩家中脱颖而出， " 
				      +"荣获第"+(i+1)+"名。 让我们期待下一次比赛吧，敬请关注@drawlively 。";

			sendOneTencentWeibo(accessToken, openKey, drawingPath, text);
//			sendOneTencentWeibo(accessToken, openKey, "/home/larmbr/Downloads/dog.jpg", text);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void sendOneTencentWeibo(String accessToken, String openKey, String drawingPath, String text) {

		  OAuthV2 oAuth = new OAuthV2();
		  
		  oAuth.setClientId("801123669");
        oAuth.setClientSecret("30169d80923b984109ee24ade9914a5c");
        oAuth.setRedirectUri("http://caicaihuahua.me");
        oAuth.setOpenid("3002527FED5211195D60F934E5AF75AD");
        oAuth.setOpenkey("D0557CED6D7BAF8041D4A032A9B62AA5");
        oAuth.setOpenkey(openKey);
        oAuth.setExpiresIn("1209600"); // 14天
         
        oAuth.setAccessToken(accessToken);
        
        TAPI tAPI=new TAPI(oAuth.getOauthVersion());
        
        String format = "json"; // 返回格式
        String clientip = "127.0.0.1";   // 用户IP(以分析用户所在地
        String longitude = null; // 经度
        String latitude = null; // 纬度 
        String syncFlag = ""; // 微博同步到空间分享标记（可选，0-同步，1-不同步，默认为0）  
        try {
			   tAPI.addPic(oAuth, format, text, clientip, longitude, latitude, drawingPath, syncFlag);
		  } catch (Exception e) {
			  e.printStackTrace();
		   }
	}

}
