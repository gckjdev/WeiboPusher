package com.orange.weibopusher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.text.AbstractDocument.Content;

import com.orange.weiboservice.WeiboContent;

import weibo4j.Timeline;
import weibo4j.http.HttpClient;
import weibo4j.http.ImageItem;
import weibo4j.model.Status;

public class SendWeibo {
	
	private final static int COUNT = 3; // top 3 drawings.
	
	private final static WeiboContent weiboContent = new WeiboContent();
	
	public static void main(String args[]) {
		
		for (int i = 0; i < COUNT ; i++) {
			String drawingPath =  weiboContent.getdrawing(i);
			String sinaId = weiboContent.getSinaNickName(i);
			String word = weiboContent.getWord(i);
			String text = "今日#猜猜画画作品榜#第"+ (i+1) + "名：@"+ sinaId+" 的【"
					+ word+ "】。欣赏更多精彩涂鸦，敬请关注@猜猜画画手机版 。";
			try {
				try {
					byte[] content = readFileImage(drawingPath);
					System.out.println("content length:" + content.length);
					ImageItem pic = new ImageItem("pic", content);
					String s = java.net.URLEncoder.encode(text, "utf-8");
					Timeline tl = new Timeline();
					tl.client.setToken(args[0]);// access_token
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
	}

	public static byte[] readFileImage(String filename) throws IOException {
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
