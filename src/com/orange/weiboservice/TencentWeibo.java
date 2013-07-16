package com.orange.weiboservice;


import com.orange.weiboservice.WeiboApp.App;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.oauthv2.OAuthV2;

/**
 * Get AccessToken
 * step 1 :
 * https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id=801123669&response_type=code&redirect_uri=http://caicaihuahua.me
 *    记下 [code]备用
 *    
 * step 2 :
 *  https://open.t.qq.com/cgi-bin/oauth2/access_token?client_id=801123669&client_secret=30169d80923b984109ee24ade9914a5c&redirect_uri=http://caicaihuahua.me&grant_type=authorization_code&code=YOUR_CODE
 *  	填入上一步的code， 结果会获得 [access token]
 *  
 *  
 *  Renew access token
 *  https://open.t.qq.com/cgi-bin/oauth2/access_token?client_id=801123669&grant_type=refresh_token&refresh_token=57ef56fd40eabb4d55715b389bd3ba41
 */
public class TencentWeibo {

	private final String REDIRECT_URI = "http://caicaihuahua.me";

	private final  CommonWeiboContent weiboContent;
	private final  String clientID;
	private final  String clientSecret;
	private final  String openID;
	
	public TencentWeibo(CommonWeiboContent content, String clientID, String clientSecret, String openID) {
		this.weiboContent = content;
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.openID = openID;
	}
	
	public void sendDailyTencentWeibo(String accessToken, int topCount) {

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

			sendOneTencentWeibo(accessToken, drawingPath, text);
//			sendOneTencentWeibo(accessToken,  "/home/larmbr/Downloads/drawlively.jpg", "一条测试微博 "+i);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void sendDailyTencentWeibo(String accessToken, int topCount, App app) {

		for (int i = topCount-1; i >= 0; i--) {
			String drawingPath = weiboContent.getdrawing(i);
			String QQId = weiboContent.getQQId(i);
			String word = weiboContent.getWord(i);
			
			if (QQId == null) {
				QQId = "玩家" + weiboContent.getNickName(i);
			} else {
				QQId = "@"+QQId;
			}
			String text = "今日#" + app.getAppName() + "作品榜#第" + (i + 1) + "名：" + QQId + " 的【" + word
			+ "】。欣赏每日精彩涂鸦, 获取" + app.getAppName() + "最新动态，敬请关注@" + app.getTencentNick() +" 。";

			sendOneTencentWeibo(accessToken, drawingPath, text);
//			sendOneTencentWeibo(accessToken,  "/home/larmbr/Downloads/drawlively.jpg", "一条测试微博 "+i);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void sendContestTencentWeibo(App app, String accessToken, int topCount) {
		
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
			String text = "画画大赛#"+contestSubject+"#结束啦！  恭喜" + QQId + " 在参赛的"+participatorCount+"名玩家中脱颖而出， " 
				      +"荣获第"+(i+1)+"名。 让我们期待下一次比赛吧，敬请关注@" + app.getTencentNick() +" 。 ";

			sendOneTencentWeibo(accessToken, drawingPath, text);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void sendOneTencentWeibo(String accessToken, String drawingPath, String text) {

		OAuthV2 oAuth = new OAuthV2();
		  
		oAuth.setClientId(clientID);
        oAuth.setClientSecret(clientSecret);
        oAuth.setRedirectUri(REDIRECT_URI);
        oAuth.setOpenid(openID);
        oAuth.setExpiresIn("1209600"); // 14天
         
        oAuth.setAccessToken(accessToken);
        
        TAPI tAPI = new TAPI(oAuth.getOauthVersion());
        
        String format = "json"; // 返回格式
        String clientip = "127.0.0.1";   // 用户IP(以分析用户所在地)
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
