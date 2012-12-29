package com.orange.weiboservice;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.game.constants.DBConstants;
import com.orange.game.model.dao.Message;
import com.orange.game.model.manager.MessageManager;
import com.orange.game.model.manager.UserManager;
import com.orange.game.traffic.service.GameDBService;


public class Award {

	private final GameDBService dbService = GameDBService.getInstance();
	private final MongoDBClient dbClient = dbService.getMongoDBClient(0);
	
	public enum AwardType {
		DAILY {
			int[] coins() { 
				// 前3名的金币奖励
				int[] results = {600, 500, 400};
				return results;
			}
		},
		CONTEST {
			int[] coins() { 
				// 前10名的金币奖励
				int[] results = {20000, 10000, 5000, 2000, 2000,
						               2000,    2000,    2000,  2000,  2000, };
				return results;
			}
		};
		
		abstract int[] coins();
	}
	 
	private Award() {}
	private static Award award = new Award();
	public static Award getInstance() {
		return award;
	}
			
	public void chargeAwardCoins(final String userId, final int rank,
			   final AwardType  awardType) {
		// 传入的rank从0开始的
		dbService.executeDBRequest(0, new Runnable() {
			@Override
			public void run() {
				UserManager.chargeAccount(dbClient, userId, awardType.coins()[rank], DBConstants.C_CHARGE_SOURCE_DRAW_TOP, null, null);
			}
		});
	}
	
	public void sendDailyAwardMessage(String userId, String toUserId, String opusName, int rank) {
		// 传入的下标从1开始的
		String message = "你的画作["+opusName+"］荣登今日画榜第"+ rank+"名，获得"+ AwardType.DAILY.coins()[rank-1]+"金币。" +
				"期望再接再厉，也欢迎加入猜猜画画玩家QQ群[228119679]和玩家们一起交流, 关注新浪微博[@猜猜画画手机版]或" +
				"腾讯微博[@drawlively]获取每天新鲜画榜!";
		
		MessageManager.creatMessage(dbClient, Message.MessageTypeText, userId, toUserId, 
				null, message, 0.0, 0.0, null, 0);
	}
	
	public void sendContestAwardMessage(String userId, String toUserId, String contestSubject, int rank, int participatorCount) {
		// 传入的下标从1开始的
		String message = "恭喜你在参加#"+contestSubject+"#的"+ participatorCount +"名玩家中脱颖而出，荣获第"+ rank+"名！ 获得"+AwardType.CONTEST.coins()[rank-1]+"金币。" +
				"期望再接再厉，也欢迎加入猜猜画画玩家QQ群[228119679]和玩家们一起交流, 关注新浪微博[@猜猜画画手机版]或" +
				"腾讯微博[@drawlively]获取每天新鲜画榜!";
		
		MessageManager.creatMessage(dbClient, Message.MessageTypeText, userId, toUserId, 
				null, message, 0.0, 0.0, null, 0);
	}
}
