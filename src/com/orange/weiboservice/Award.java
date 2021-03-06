package com.orange.weiboservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.types.ObjectId;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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
			public int[] coins() { 
				// 前20名的金币奖励
				int[] results = {1000, 900, 800, 700, 600,
								  500,  388,  388,  388,   388,
							      388,  388,  388,  388,   388,
					    	      388,  388,  388,  388,   388,
							    };
				return results;
			}
		},
		CONTEST {
			public int[] coins() { 
				// 前20名的金币奖励
				int[] results = {20000, 15000, 10000, 5000, 5000,
						         5000,   5000,  5000, 5000, 5000, 
						         5000,   5000,  5000, 5000, 5000, 
						         5000,   5000,  5000, 5000, 5000, };
				return results;
			}
		};
		
		public abstract int[] coins();
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
		String message = "你的作品["+opusName+"］荣登今日画榜第"+ rank+"名，获得"+ AwardType.DAILY.coins()[rank-1]+"金币。" +
				"期望再接再厉，也欢迎加入小吉画画玩家QQ群[228119679]和玩家们一起交流, 关注新浪微博[@小吉画画]或" +
				"腾讯微博[@drawlively]获取每天新鲜画榜!";
		
		MessageManager.creatMessage(dbClient, Message.MessageTypeText, userId, toUserId, 
				null, message, 0.0, 0.0, null, 0, null, null);
	}
	
	public void sendContestAwardMessage(String userId, String toUserId, String contestSubject, int rank, int participatorCount) {
		// 传入的下标从1开始的
		String message = "恭喜你在参加#"+contestSubject+"#的"+ participatorCount +"名玩家中脱颖而出，荣获第"+ rank+"名！ 获得"+AwardType.CONTEST.coins()[rank-1]+"金币。" +
				"期望再接再厉，也欢迎加入小吉画画画画玩家QQ群[228119679]和玩家们一起交流, 关注新浪微博[@小吉画画]或" +
				"腾讯微博[@drawlively]获取每天新鲜画榜!";
		
		MessageManager.creatMessage(dbClient, Message.MessageTypeText, userId, toUserId, 
				null, message, 0.0, 0.0, null, 0, null, null);
	}
	
	public void insertRankToDB(String userId, int rank) {
		
		Date date = new Date(System.currentTimeMillis()-7200000);// 因为在12点后发的微博，所以减去2小时以表示前一天
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		DBObject query = new BasicDBObject(DBConstants.F_USERID, new ObjectId(userId));
		
		DBObject dailyRankValue = new BasicDBObject();
		dailyRankValue.put("rank", rank);
		dailyRankValue.put("date", dateFormat.format(date));
		
		DBObject dailyRank = new BasicDBObject();
		dailyRank.put("daily_rank", dailyRankValue);
		
		DBObject update = new BasicDBObject();
		update.put("$push", dailyRank);
		
		dbClient.updateOne(DBConstants.T_USER, query, update);
	
	}
	
	public static void main(String[] args) {
		Award.getInstance().insertRankToDB("51037eb844aecd7c06c0e5a8", 1);
		Award.getInstance().insertRankToDB("51037eb844aecd7c06c0e5a8", 2);
	}
}
