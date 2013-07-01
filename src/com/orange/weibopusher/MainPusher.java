package com.orange.weibopusher;

import com.orange.common.log.ServerLog;

public class MainPusher {

	public static void main(String[] args) throws Exception {
		
		String weiboType = System.getProperty("weibo_type");
		if ( weiboType != null && weiboType.equalsIgnoreCase("daily")) {
			DailyWeiboPusher.sendDailyWeibo(args);
		} 
		else if ( weiboType != null &&  weiboType.equalsIgnoreCase("contest")) {
			ContestWeiboPusher.sendContestWeibo(args);
		}
		else {
			ServerLog.info(0, "You must specify a vaild type of weibo to send !!!"
					+ " Use -Dweibo.type=daily or -Dweibo.type=contest ");
		}
	}
	
}
