package com.orange.weibopusher;

import com.orange.weiboservice.Award;
import com.orange.weiboservice.WeiboContent;

public class Main {
	
	private final static int COUNT = 1; // top 3 drawings.
	
	private static WeiboContent weiboContent = new WeiboContent();
	private static SinaWeibo sinaWeibo = new SinaWeibo(weiboContent);
	private static TencentWeibo tencentWeibo = new TencentWeibo(weiboContent);
	
	private final static Award awardService = Award.getInstance();
	private final static String CUSTOMER_SERVICE_UID = "888888888888888888888888";
	
	public static void main(String[] args) {
		
		// 发新浪微博
		String sinaAccessToken = args[0]; 
		sinaWeibo.updateSinaWeibo(sinaAccessToken);
		
		// 发腾讯微博
		String tencentAccessToken = args[1];
		tencentWeibo.updateTencentWeibo(tencentAccessToken);
		
		// 奖励金币，并发私信告知
		for (int i = COUNT-1; i >= 0; i--) {
			String userId = weiboContent.getUserId(i);
			String opus = weiboContent.getWord(i);
			
			awardService.chargeAwardCoins(userId, i);
			awardService.sendAwardMessage(CUSTOMER_SERVICE_UID, userId, opus, i+1);
		}
	}
}
