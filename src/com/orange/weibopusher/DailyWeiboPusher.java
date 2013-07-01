package com.orange.weibopusher;

import com.orange.weiboservice.Award;
import com.orange.weiboservice.Award.AwardType;
import com.orange.weiboservice.WeiboApp.App;
import com.orange.weiboservice.DailyWeiboContent;
import com.orange.weiboservice.SinaWeibo;
import com.orange.weiboservice.TencentWeibo;

public class DailyWeiboPusher {
	
	// 发前三名微博
	private final static int WEIBO_TOP_COUNT = 3; 
	// 奖励前二十名
	private final static int AWARD_TOP_COUNT = 20; 
	// 客服的User ID
	private final static String CUSTOMER_SERVICE_UID = "888888888888888888888888";
	
	// 可以用于控制只发某个微博
	private final static int OPTION_SINA    = 1<<2; // 二进制 100, 发新浪微博 
	private final static int OPTION_TENCENT = 1<<1; // 二进制 010, 发腾讯微博 
	private final static int OPTION_AWARD   = 1;    // 二进制 001, 送奖励并发信息告知 
	
	private static DailyWeiboContent weiboContent = new DailyWeiboContent(AWARD_TOP_COUNT);
	private final static SinaWeibo sinaWeibo = new SinaWeibo(weiboContent);
	
	
	private final static String drawTencentClientId = App.Draw.getTencentClientId();
	private final static String drawTencentClientSecret = App.Draw.getTencentClientSecret();
	private final static String drawTencentOpenID = App.Draw.getTencentOpenID();
	private final static TencentWeibo drawTencentWeibo = 
			new TencentWeibo(weiboContent, drawTencentClientId, drawTencentClientSecret, drawTencentOpenID);
	
	
	private final static String xiaojiTencentClientId = App.Xiaoji.getTencentClientId();
	private final static String xiaojiTencentClientSecret = App.Xiaoji.getTencentClientSecret();
	private final static String xiaojiTencentOpenID = App.Xiaoji.getTencentOpenID();
	private final static TencentWeibo xiaojiTencentWeibo = 
			new TencentWeibo(weiboContent, xiaojiTencentClientId, xiaojiTencentClientSecret,xiaojiTencentOpenID);
	

	
	public static void sendDailyWeibo(String...args) {

		int option = 7; // 111
		String para = System.getProperty("daily_option");
		if ( para != null && !para.isEmpty() ) {
			option = Integer.parseInt(para, 2);
		}
		
		
		// 发新浪微博
		if ( (option & OPTION_SINA) == OPTION_SINA) {
			final String drawAccessToken = args[0];
			final String xiaojiAccessToken = args[2];
			Thread sina = new Thread(new Runnable() {
				@Override
				public void run() {
					sinaWeibo.sendDailySinaWeibo(App.Draw, drawAccessToken, WEIBO_TOP_COUNT);
					sinaWeibo.sendDailySinaWeibo(App.Xiaoji, xiaojiAccessToken, WEIBO_TOP_COUNT);
				}
			});
			sina.start();
		}
		
		// 发腾讯微博
		if ( (option & OPTION_TENCENT) == OPTION_TENCENT) {
			final String drawQQAccessToken = args[1];
			final String xiaojiQQAccessToken = args[3];
			Thread tencent = new Thread(new Runnable() {
				public void run() {
					drawTencentWeibo.sendDailyTencentWeibo(drawQQAccessToken, WEIBO_TOP_COUNT, App.Draw);
					xiaojiTencentWeibo.sendDailyTencentWeibo(xiaojiQQAccessToken, WEIBO_TOP_COUNT, App.Xiaoji);
				}
			});
			tencent.start();
		}
		
		// 附加业务，奖励金币，并发私信告知
		if ( (option & OPTION_AWARD) == OPTION_AWARD) {
			Award awardService = Award.getInstance();
			for (int i = AWARD_TOP_COUNT-1; i >= 0; i--) {
				String userId = weiboContent.getUserId(i);
				String opus = weiboContent.getWord(i);
			
				awardService.chargeAwardCoins(userId, i, AwardType.DAILY);
				awardService.sendDailyAwardMessage(CUSTOMER_SERVICE_UID, userId, opus, i+1);
				if ( i < WEIBO_TOP_COUNT ) {
					// 前三名才在数据库中插入记录
					awardService.insertRankToDB(userId, i+1);
				}
			}
		}
		
		
	}
}