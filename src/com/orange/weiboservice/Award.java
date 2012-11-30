package com.orange.weiboservice;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.game.constants.DBConstants;
import com.orange.game.model.dao.Message;
import com.orange.game.model.manager.MessageManager;
import com.orange.game.model.manager.UserManager;
import com.orange.game.traffic.service.GameDBService;


public class Award {

	private GameDBService dbService = GameDBService.getInstance();
	private MongoDBClient dbClient = dbService.getMongoDBClient(0);
	
	private final static int[] AWARD_COINS = {600, 500, 400};
	 
	private Award() {}
	private static Award award = new Award();
	public static Award getInstance() {
		return award;
	}
			
	public void chargeAwardCoins(final String userId, final int rank) {
		dbService.executeDBRequest(0, new Runnable() {
			@Override
			public void run() {
				UserManager.chargeAccount(dbClient, userId, AWARD_COINS[rank], DBConstants.C_CHARGE_SOURCE_DRAW_TOP, null, null);
			}
		});
	}
	
	public void sendAwardMessage(String userId, String toUserId, String opusName, int rank) {
		
		String message = "您的画作["+opusName+"］荣登今日画榜第"+ rank+"名，获得"+AWARD_COINS[rank-1]+"金币。" +
				"期望再接再厉，也欢迎加入猜猜画画玩家QQ群[228119679]和玩家们一起交流, 关注新浪微博[@猜猜画画手机版]或" +
				"腾讯微博[猜猜画画手机版]获取每天新鲜画榜!";
		
		MessageManager.creatMessage(dbClient, Message.MessageTypeText, userId, toUserId, 
				null, message, 0.0, 0.0, null, 0);
	}
}
