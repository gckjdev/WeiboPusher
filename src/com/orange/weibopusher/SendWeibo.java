package com.orange.weibopusher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.orange.weiboservice.Award;
import com.orange.weiboservice.WeiboContent;

import weibo4j.Timeline;
import weibo4j.http.ImageItem;
import weibo4j.model.Status;

public class SendWeibo {

	private final static int COUNT = 3; // top 3 drawings.

   private final static WeiboContent weiboContent = new WeiboContent();
   private final static Award awardService = Award.getInstance();
     
   private final static String CUSTOMER_SERVICE_UID = "888888888888888888888888"; 
	public static void main(String args[]) {

		for (int i = COUNT-1; i >= 0; i--) {
			String drawingPath = weiboContent.getdrawing(i);
			String sinaId = weiboContent.getSinaNickName(i);
			String word = weiboContent.getWord(i);
			String userId = weiboContent.getUserId(i);
			
			if (sinaId == null) {
				sinaId = "玩家" + weiboContent.getNickName(i);
			} else {
				sinaId = "@"+sinaId;
			}
			String text = "今日#猜猜画画作品榜#第" + (i + 1) + "名：" + sinaId + " 的【" + word
			+ "】。欣赏更多精彩涂鸦，敬请关注@猜猜画画手机版 。";

			doSendWeibo(args[0], drawingPath, text);
			awardService.chargeAwardCoins(userId, i);
			awardService.sendAwardMessage(CUSTOMER_SERVICE_UID, userId, word, i+1);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void doSendWeibo(String access_token, String drawingPath,
			String text) {
		try {
			try {
				byte[] content = readFileImage(drawingPath);
				ImageItem pic = new ImageItem("pic", content);
				String s = java.net.URLEncoder.encode(text, "utf-8");
				Timeline tl = new Timeline();
				tl.setToken(access_token);// access_token
				Status status = tl.UploadStatus(s, pic);
				System.out.println("Successfully upload the status to ["
						+ status.getText() + "].");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception ioe) {
			System.out.println("Failed to read the system input.");
		}
	}
	
	@SuppressWarnings("resource")
	private static byte[] readFileImage(String filename) throws IOException {
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
