package com.orange.weibopusher;


import com.orange.weiboservice.WeiboContent;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.oauthv2.OAuthV2;


public class TencentWeibo {

	private final static int DEFAULT_COUNT = 3; // top 3 drawings.

	private final  WeiboContent weiboContent;
	private final  int topCount;
	
	TencentWeibo(WeiboContent content) {
		this.weiboContent = content;
		this.topCount = DEFAULT_COUNT;
	}
	
	TencentWeibo(WeiboContent content, int topCount) {
		this.weiboContent = content;
		this.topCount = topCount;
	}
	
	public void updateTencentWeibo(String accessToken) {

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

			sendWeibo(accessToken, drawingPath, text);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param accessToken  Tencent access token
	 * @param drawingPath  drawing path
	 * @param text         weibo content
	 */
	private void sendWeibo(String accessToken, String drawingPath, String text) {

		  OAuthV2 oAuth = new OAuthV2();
		  
		  oAuth.setClientId("801123669");
        oAuth.setClientSecret("30169d80923b984109ee24ade9914a5c");
        oAuth.setRedirectUri("http://caicaihuahua.me");
        oAuth.setOpenid("3002527FED5211195D60F934E5AF75AD");
        oAuth.setOpenkey("B0551CAE10C551037B76925F5D41FAA9");
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
