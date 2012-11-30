package com.orange.weibopusher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.orange.weiboservice.WeiboContent;

import weibo4j.Timeline;
import weibo4j.http.ImageItem;
import weibo4j.model.Status;

public class SinaWeibo {

	private final static int DEFAULT_COUNT = 3; // top 3 drawings.

   private final  WeiboContent weiboContent;
   private final  int topCount;
	
	SinaWeibo(WeiboContent content) {
		this.weiboContent = content;
		this.topCount = DEFAULT_COUNT;
	}
	
	SinaWeibo(WeiboContent content, int topCount) {
		this.weiboContent = content;
		this.topCount = topCount;
	}  
   
	public void updateSinaWeibo(String accessToken) {

		for (int i = topCount-1; i >= 0; i--) {
			String drawingPath = weiboContent.getdrawing(i);
			String sinaId = weiboContent.getSinaNickName(i);
			String word = weiboContent.getWord(i);
			
			if (sinaId == null) {
				sinaId = "玩家" + weiboContent.getNickName(i);
			} else {
				sinaId = "@"+sinaId;
			}
			String text = "今日#猜猜画画作品榜#第" + (i + 1) + "名：" + sinaId + " 的【" + word
			+ "】。欣赏每日精彩涂鸦, 获取猜猜画画最新动态，敬请关注@猜猜画画手机版 。";

			sendWeibo(accessToken, drawingPath, text);
			
			// 不能频繁发微博
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendWeibo(String access_token, String drawingPath,
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
