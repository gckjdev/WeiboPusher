package com.orange.weibopusher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


import com.orange.common.log.ServerLog;
import com.orange.weiboservice.Award;
import com.orange.weiboservice.Award.AwardType;
import com.orange.weiboservice.ContestWeiboContent;
import com.orange.weiboservice.SinaWeibo;
import com.orange.weiboservice.TencentWeibo;
import com.orange.weiboservice.ContestWeiboContent.WeiboType;
import com.orange.weiboservice.CommonWeiboContent;

public class ContestWeiboPusher {

	// 奖励前十名
	private static final int TOP_COUNT_AWARD = 10;
	// 发前三名微博
	private static final int TOP_COUNT_WEIBO = 3;
	// 客服的User ID
	private final static String CUSTOMER_SERVICE_UID = "888888888888888888888888";
	
	private static ContestWeiboContent weiboContent;
	private static SinaWeibo sinaWeibo;
	private static TencentWeibo tencentWeibo;
	
//	public static void main(String[] args) {
	public static void sendContestWeibo(String...args) {

		final String sinaAccessToken = args[0]; 
		final String tencentAccessToken = args[1];
		final String tencentOpenKey = args[2];
		
		String type = System.getProperty("contest_weibo_type");
		if ( type == null ) {
			ServerLog.info(0, " You must specify which contest type of weibo, like -Dcontest_weibo_tye=start or " +
					" -Dcontest_weibo_tye=end .");
			return;
		}
		
		String contestId = System.getProperty("contest_id");
		if ( contestId == null ) {
			ServerLog.info(0, " You must specify contest ID, like -Dcontest_id=CONTEST_ID");
			return;
		}
		
		if (type.equals("start")){ 
			sendContestStartWeibo(sinaAccessToken, tencentAccessToken,
					tencentOpenKey, contestId);
		} 
		else {
			sendContestEndingWeibo(sinaAccessToken, tencentAccessToken,
					tencentOpenKey, contestId);
		}
		
		// 附加业务，奖励金币，并发私信告知
		Award awardService = Award.getInstance();
		for (int i = TOP_COUNT_AWARD -1; i >= 0; i--) {
			String userId = weiboContent.getUserId(i);
			String contestSubject = weiboContent.getContestSubject();
			int participatorCount = weiboContent.getParticipatorCount();
		
			awardService.chargeAwardCoins(userId, i, AwardType.CONTEST);
			awardService.sendContestAwardMessage(CUSTOMER_SERVICE_UID, userId, contestSubject, i+1, participatorCount);
		}
		
		// 附加业务， 第4-第10名的ID, 作品url输出到一个文件，以便合成图片后人工发微博
		writeOtherAwarderInfoToFile("/root/weibo_pusher/awarder_info_4-10", weiboContent, 3, 9);
//		writeOtherAwarderInfoToFile("/home/larmbr/test", weiboContent, 3, 9);
	}


	private static void sendContestStartWeibo(final String sinaAccessToken,
			final String tencentAccessToken, final String tencentOpenKey, final String contestId) {
		
		weiboContent = new ContestWeiboContent(WeiboType.CONTEST_START, TOP_COUNT_AWARD, contestId);
		String contestSubject = weiboContent.getContestSubject();
		String startDate = weiboContent.getStartDateString();
		String endingDate = weiboContent.getEndingDateString();
		sinaWeibo = new SinaWeibo(weiboContent);
		tencentWeibo = new TencentWeibo(weiboContent);
		
		final String posterUrl = weiboContent.getPosterUrl(); // "/home/larmbr/Downloads/dog.jpg";//
		final String text = "#"+contestSubject+"# 开始啦！ 本次画画大赛时间从"+startDate+"到"+endingDate+
				"截止, 进入游戏了解更多比赛详情。 快快来参加比赛一展才华吧！ 期待你的参与哦！";
		
		// 发新浪微博
		Thread sina = new Thread(new Runnable() {
			@Override
			public void run() {
				sinaWeibo.sendOneSinaWeibo(sinaAccessToken, posterUrl, text);
			}
		});
		sina.start();
		
		// 发腾讯微博
		Thread tencent = new Thread(new Runnable() {
			@Override
			public void run() {
				tencentWeibo.sendOneTencentWeibo(tencentAccessToken, tencentOpenKey, posterUrl, text);
			}
		});
		tencent.start();
	}
	

	private static void sendContestEndingWeibo(final String sinaAccessToken,
			final String tencentAccessToken, final String tencentOpenKey, final String contestId) {
		
		weiboContent = new ContestWeiboContent(WeiboType.CONTEST_ENDING, TOP_COUNT_AWARD, contestId);
		sinaWeibo = new SinaWeibo(weiboContent);
		tencentWeibo = new TencentWeibo(weiboContent);

		// 发新浪微博
		Thread sina = new Thread(new Runnable() {
			@Override
			public void run() {
				sinaWeibo.sendContestSinaWeibo(sinaAccessToken, TOP_COUNT_WEIBO);
			}
		});
		sina.start();
		
		// 发腾讯微博
		Thread tencent = new Thread(new Runnable() {
			@Override
			public void run() {
				tencentWeibo.sendContestTencentWeibo(tencentAccessToken, tencentOpenKey, TOP_COUNT_WEIBO);
			}
		});
		tencent.start();
	}


	private static void writeOtherAwarderInfoToFile(String filePath, CommonWeiboContent weiboContent,
			int startIndex, int endIndex) {
		
		boolean success = true;
		ServerLog.info(0, "Generating awarder info file....");
		String content = generateContent(weiboContent, startIndex, endIndex);
		if ( content == null ) {
			ServerLog.info(0, "Generating info fails. Please check !");
			return;
		}
	   try {
	       File file = new File(filePath);
	       if (file.exists()) {
	     	    file.delete();
	       } else {
	          ServerLog.info(0, "Creating file :" + filePath +" ...");
	          if ( ! file.createNewFile()) {
	        	    ServerLog.info(0, "Creating file " + filePath + " fails ... ");
	        	    success = false; 
	           	  }
	         }	   
	       BufferedWriter output = new BufferedWriter(new FileWriter(file));
	       output.write(content);
	       output.close();
	   } catch (Exception e) {
		   ServerLog.info(0, "Generating file failed due to " + e.toString() +
		   "\nOutput to console : \n" +  content);
		   success = false;
	   } 
	  
	   if (success) {
		   ServerLog.info(0, "Generating awarder info done !");
	    }
	}


	private static String generateContent(CommonWeiboContent weiboContent, int startIndex, int endIndex) {
		
		StringBuilder result = new StringBuilder("");
		for ( int i = startIndex; i <= endIndex; i++ ) {
			String sinaId = weiboContent.getSinaNickName(i);
			String QQId = weiboContent.getQQId(i);
			String nickName = weiboContent.getNickName(i);
			String drawingPath = weiboContent.getdrawing(i);
			String drawingUrl = "http://58.215.184.18:8080"+drawingPath.substring(5); // skip the part /data
			
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
