package com.orange.weiboservice;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.orange.common.log.ServerLog;
import com.orange.weiboservice.WeiboApp.App;


import weibo4j.Timeline;
import weibo4j.http.ImageItem;
import weibo4j.model.Status;

public class SinaWeibo {

   private final  CommonWeiboContent weiboContent;
	
	public SinaWeibo(CommonWeiboContent content) {
		this.weiboContent = content;
	}

	
	public void sendDailySinaWeibo(App app, String accessToken, int topCount) {

		for (int i = topCount-1; i >= 0; i--) {
			String drawingPath = weiboContent.getdrawing(i);
			String sinaId = weiboContent.getSinaNickName(i);
			String word = weiboContent.getWord(i);
			
			if (sinaId == null) {
				sinaId = "玩家" + weiboContent.getNickName(i);
			} else {
				sinaId = "@"+sinaId;
			}
			String text = "今日#" + app.getAppName() + "作品榜#第" + (i + 1) + "名：" + sinaId + " 的【" + word
			+ "】。欣赏每日精彩涂鸦, 获取"+ app.getAppName()+"最新动态，敬请关注@" + app.getSinaNick() +"。";

			sendOneSinaWeibo(accessToken, drawingPath, text);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	

	public void sendContestSinaWeibo(App app, String accessToken, int topCount) {

		for (int i = topCount-1; i >= 0; i--) {
			String drawingPath = weiboContent.getdrawing(i);
			String sinaId = weiboContent.getSinaNickName(i);
			String contestSubject = ((ContestWeiboContent)weiboContent).getContestSubject();
			int participatorCount = ((ContestWeiboContent)weiboContent).getParticipatorCount();
			
			if (sinaId == null) {
				sinaId = "玩家" + weiboContent.getNickName(i);
			} else {
				sinaId = "@"+sinaId;
			}
			String text = "画画大赛#"+contestSubject+"#结束啦！  恭喜" + sinaId + " 在参赛的"+participatorCount+"名玩家中脱颖而出， " 
			      +"荣获第"+(i+1)+"名。 让我们期待下一次比赛吧，敬请关注@猜猜画画手机版 。";

			sendOneSinaWeibo(accessToken, drawingPath, text);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	
	public void sendOneSinaWeibo(String accessToken, String drawingPath, String text) {
		try {
			byte[] content = readFileImage(drawingPath);
			ImageItem pic = new ImageItem("pic", content);
			String s = java.net.URLEncoder.encode(text, "utf-8");
			Timeline tl = new Timeline();
			tl.setToken(accessToken);// access_token
			Status status = tl.UploadStatus(s, pic);
			ServerLog.info(0,"Successfully upload the status to [" + status.getText() + "].");
		} catch (Exception e) {
			ServerLog.info(0, "Failed to read the system input.");
		}
	}
	
	@SuppressWarnings("resource")
	private byte[] readFileImage(String filename) throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(filename));
		int len = bufferedInputStream.available();
		byte[] bytes = new byte[len];
		int r = bufferedInputStream.read(bytes);
		if (len != r) {
			bytes = null;
			throw new IOException("Read image file faild!!!");
		}
		bufferedInputStream.close();
		return bytes;
	}

}
