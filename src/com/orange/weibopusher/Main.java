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
		final String sinaAccessToken = args[0]; 
		Thread sina = new Thread(new Runnable() {
			@Override
			public void run() {
				sinaWeibo.updateSinaWeibo(sinaAccessToken);
			}
		});
		sina.start();
		
		// 发腾讯微博
		final String tencentAccessToken = args[1];
		Thread tencent = new Thread(new Runnable() {
			@Override
			public void run() {
				tencentWeibo.updateTencentWeibo(tencentAccessToken);
			}
		});
		tencent.start();
		
		// 奖励金币，并发私信告知
		for (int i = COUNT-1; i >= 0; i--) {
			String userId = weiboContent.getUserId(i);
			String opus = weiboContent.getWord(i);
			
			awardService.chargeAwardCoins(userId, i);
			awardService.sendAwardMessage(CUSTOMER_SERVICE_UID, userId, opus, i+1);
		}
	}
}
