package com.orange.weibopusher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


import com.orange.common.log.ServerLog;
import com.orange.weiboservice.Award;
import com.orange.weiboservice.ContestWeiboContent;
import com.orange.weiboservice.SinaWeibo;
import com.orange.weiboservice.TencentWeibo;
import com.orange.weiboservice.ContestWeiboContent.WeiboType;
import com.orange.weiboservice.WeiboApp.App;
import com.orange.weiboservice.CommonWeiboContent;

public class ContestWeiboPusher {

	// 发前三名微博(剩下7名手工合成一张图片再发)
	private static final int WEIBO_TOP_COUNT = 3;
	// 奖励前十名
	private static final int AWARD_TOP_COUNT = 10;
	// 客服的User ID
	private final static String CUSTOMER_SERVICE_UID = "888888888888888888888888";
	private static String TRAFIC_API_SERVER_URL = "http://58.215.184.18:8080";
	
	private static ContestWeiboContent weiboContent;
	
	public static void sendContestWeibo(String...args) {

		final String drawSinaAccessToken = args[0];
		final String drawTencentAccessToken = args[1];
		final String xiaojiSinaAccessToken = args[2];
		final String xiaojiTencentAccessToken = args[3];
		
		String type = System.getProperty("contest_weibo_type");
		if ( type == null ) {
			ServerLog.info(0, " You must specify which contest type of weibo, like -Dcontest_weibo_tye=start or " +
					" -Dcontest_weibo_type=end or -Dcontest_weibo_type=ending_info .");
			return;
		}
		
		String contestId = System.getProperty("contest_id");
		if ( contestId == null ) {
			ServerLog.info(0, " You must specify contest ID, like -Dcontest_id=CONTEST_ID");
			return;
		}
		
		
		if (type.equalsIgnoreCase("start")) {
				// 比赛开始时发微博
				sendContestStartWeibo(App.Draw, drawSinaAccessToken, drawTencentAccessToken, contestId);
				sendContestStartWeibo(App.Xiaoji, xiaojiSinaAccessToken, xiaojiTencentAccessToken, contestId);
				return;
		}
		else if (type.equalsIgnoreCase("ending_info")) {
				// 比赛结束后生成4-10名玩家信息文件, 即
				// 第4-第10名的ID, 作品url输出到一个文件，以便合成图片后人工发微博
				writeOtherAwarderInfoToFile("/root/weibo_pusher/awarder_info_4-10", contestId, 3, 9);
				return;
		}
		else if (type.equalsIgnoreCase("ending")) {
				// 发前三名微博
				sendContestEndingWeibo(App.Draw, drawSinaAccessToken, drawTencentAccessToken, contestId);
				sendContestEndingWeibo(App.Xiaoji, xiaojiSinaAccessToken, xiaojiTencentAccessToken, contestId);
				
				// 附加业务，奖励金币，并发私信告知
				Award awardService = Award.getInstance();
				for (int i = AWARD_TOP_COUNT -1; i >= 0; i--) {
					String userId = weiboContent.getUserId(i);
					String contestSubject = weiboContent.getContestSubject();
					int participatorCount = weiboContent.getParticipatorCount();
				
//					awardService.chargeAwardCoins(userId, i, AwardType.CONTEST);
					awardService.sendContestAwardMessage(CUSTOMER_SERVICE_UID, userId, contestSubject, i+1, participatorCount);
				}
				return;
		} 
		else {
				ServerLog.info(0, "Unsupprt contest weibo type !!!");
				return;
		} 
	}


	private static void sendContestStartWeibo(App app, final String sinaAccessToken,
			final String tencentAccessToken, final String contestId) {
		
		weiboContent = new ContestWeiboContent(WeiboType.CONTEST_START, AWARD_TOP_COUNT, contestId);
		String contestSubject = weiboContent.getContestSubject();
		String startDate = weiboContent.getStartDateString();
		String endingDate = weiboContent.getEndingDateString();
		
		final SinaWeibo sinaWeibo = new SinaWeibo(weiboContent);
		final TencentWeibo tencentWeibo = new TencentWeibo(weiboContent, app.getTencentClientId(), 
					 app.getTencentClientSecret(), app.getTencentOpenID());
		
		final String posterUrl = weiboContent.getPosterUrl(); 
		final String posterPath = "/data"+posterUrl.substring(25); // 跳过"http://58.215.184.18:8080",一共25个字符
		final String text = "新一次画画大赛开始啦！ 本次画画大赛主题是#"+contestSubject+"#， 时间从"+startDate+"到"+endingDate+
				", 请进入游戏了解更多比赛详情。 快快来参加比赛一展才华吧！ 期待你的参与哦！";
		
		// 发新浪微博
		Thread sina = new Thread(new Runnable() {
			@Override
			public void run() {
				sinaWeibo.sendOneSinaWeibo(sinaAccessToken, posterPath, text);
			}
		});
		sina.start();
		
		// 发腾讯微博
		Thread tencent = new Thread(new Runnable() {
			@Override
			public void run() {
				tencentWeibo.sendOneTencentWeibo(tencentAccessToken, posterPath, text);
			}
		});
		tencent.start();
	}
	

	private static void sendContestEndingWeibo(App app, final String sinaAccessToken,
			final String tencentAccessToken,  final String contestId) {
		
		weiboContent = new ContestWeiboContent(WeiboType.CONTEST_ENDING, AWARD_TOP_COUNT, contestId);
		
		final SinaWeibo sinaWeibo = new SinaWeibo(weiboContent);
		final TencentWeibo tencentWeibo = new TencentWeibo(weiboContent, app.getTencentClientId(), 
				app.getTencentClientSecret(),app.getTencentOpenID());

		// 发新浪微博
		Thread sina = new Thread(new Runnable() {
			@Override
			public void run() {
				sinaWeibo.sendContestSinaWeibo(App.Draw, sinaAccessToken, WEIBO_TOP_COUNT);
			}
		});
		sina.start();
		
		// 发腾讯微博
		Thread tencent = new Thread(new Runnable() {
			@Override
			public void run() {
				tencentWeibo.sendContestTencentWeibo(tencentAccessToken, WEIBO_TOP_COUNT);
			}
		});
		tencent.start();
	}


	private static void writeOtherAwarderInfoToFile(String filePath, String contestId, 
			int startIndex, int endIndex) {
		
		weiboContent = new ContestWeiboContent(WeiboType.CONTEST_ENDING, AWARD_TOP_COUNT, contestId);
		if ( weiboContent == null ) {
			System.out.println("! Get weibo content fails");
			return;
		}
		
		boolean success = true;
		System.out.println("* Generating awarder info file....");
		String content = generateContent(weiboContent, startIndex, endIndex);
		if ( content == null ) {
			System.out.println("! Generating info fails. Please check !");
			return;
		}
	   try {
	       File file = new File(filePath);
	       if (file.exists()) {
	     	    file.delete();
	       } else {
	    	   System.out.println("*  Creating file :" + filePath +" ...");
	          if ( ! file.createNewFile()) {
	        	  System.out.println("*  Creating file " + filePath + " fails ... ");
	        	    success = false; 
	           	  }
	         }	   
	       BufferedWriter output = new BufferedWriter(new FileWriter(file));
	       output.write(content);
	       output.close();
	   } catch (Exception e) {
		   System.out.println("! Generating file failed due to " + e.toString() +
		   "\nOutput to console : \n" +  content);
		   success = false;
	   } 
	  
	   if (success) {
		   System.out.println("* Generating awarder info done !");
	    }
	}


	private static String generateContent(CommonWeiboContent weiboContent, int startIndex, int endIndex) {
		
		StringBuilder result = new StringBuilder("");
		for ( int i = startIndex; i <= endIndex; i++ ) {
			String sinaId = weiboContent.getSinaNickName(i);
			String QQId = weiboContent.getQQId(i);
			String nickName = weiboContent.getNickName(i);
			String drawingPath = weiboContent.getdrawing(i);
			String drawingUrl = TRAFIC_API_SERVER_URL+drawingPath.substring(5); // skip the part /data
			
			result.append("=========== No." +(i+1) +" ==========\n");
			result.append("SinaId: " + sinaId);
			result.append("    QQId: " + QQId);
			result.append("\nNickName: " + nickName);
			result.append("\nURL: " + drawingUrl);
			result.append("\n\n");
		}
		return result.toString();
	}
}
