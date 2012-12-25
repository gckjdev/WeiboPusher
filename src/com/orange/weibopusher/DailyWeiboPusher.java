package com.orange.weibopusher;

import com.orange.weiboservice.Award;
import com.orange.weiboservice.Award.AwardType;
import com.orange.weiboservice.DailyWeiboContent;
import com.orange.weiboservice.SinaWeibo;
import com.orange.weiboservice.TencentWeibo;

public class DailyWeiboPusher {
	
	// 只发前三名
	private final static int TOP_COUNT = 3; 
	// 客服的User ID
	private final static String CUSTOMER_SERVICE_UID = "888888888888888888888888";
	
	// 可以用于控制只发某个微博
	private final static int OPTION_SINA    = 1<<2; // 二进制 100, 发新浪微博 
	private final static int OPTION_TENCENT = 1<<1; // 二进制 010, 发腾讯微博 
	private final static int OPTION_AWARD   = 1;    // 二进制 001, 送奖励并发信息告知 
	
	private static DailyWeiboContent weiboContent = new DailyWeiboContent(TOP_COUNT);
	private final static SinaWeibo sinaWeibo = new SinaWeibo(weiboContent);
	private final static TencentWeibo tencentWeibo = new TencentWeibo(weiboContent);
	
	
//	public static void main(String[] args) {
	public static void sendDailyWeibo(String...args) {

		int option = 7; // 111
		String para = System.getProperty("daily_option");
		if ( para != null && !para.isEmpty() ) {
			option = Integer.parseInt(para, 2);
		}
		
		
		// 发新浪微博
		if ( (option & OPTION_SINA) == OPTION_SINA) {
			final String sinaAccessToken = args[0]; 
			Thread sina = new Thread(new Runnable() {
				@Override
				public void run() {
					sinaWeibo.sendDailySinaWeibo(sinaAccessToken, TOP_COUNT);
				}
			});
			sina.start();
		}
		
		// 发腾讯微博
		if ( (option & OPTION_TENCENT) == OPTION_TENCENT) {
			final String tencentAccessToken = args[1];
			final String tencentOpenKey = args[2];
			Thread tencent = new Thread(new Runnable() {
				@Override
				public void run() {
					tencentWeibo.sendDailyTencentWeibo(tencentAccessToken, tencentOpenKey, TOP_COUNT);
				}
			});
			tencent.start();
		}
		
		// 附加业务，奖励金币，并发私信告知
		if ( (option & OPTION_AWARD) == OPTION_AWARD) {
			Award awardService = Award.getInstance();
			for (int i = TOP_COUNT-1; i >= 0; i--) {
				String userId = weiboContent.getUserId(i);
				String opus = weiboContent.getWord(i);
			
				awardService.chargeAwardCoins(userId, i, AwardType.DAILY);
				awardService.sendDailyAwardMessage(CUSTOMER_SERVICE_UID, userId, opus, i+1);
			}
		}
	}
}
