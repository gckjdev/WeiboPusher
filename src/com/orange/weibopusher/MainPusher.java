package com.orange.weibopusher;

public class MainPusher {

	public static void main(String[] args) {
		
		String weiboType = System.getProperty("weibo_type");
		if ( weiboType != null && weiboType.equals("daily")) {
			DailyWeiboPusher.sendDailyWeibo(args);
		} 
		else if ( weiboType != null &&  weiboType.equals("contest")) {
			ContestWeiboPusher.sendContestWeibo(args);
		}
		else {
			System.out.println("You must specify a vaild type of weibo to send !!!"
					+ " Use -Dweibo.type=daily or -Dweibo.type=contest ");
		}
	}
	
}
