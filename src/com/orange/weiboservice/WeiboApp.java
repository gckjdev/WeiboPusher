package com.orange.weiboservice;

public class WeiboApp {

	public String tencentOpenID;

	public enum App {
		Draw("猜猜画画", "猜猜画画手机版", "drawlively", "801123669", 
				"30169d80923b984109ee24ade9914a5c", "3002527FED5211195D60F934E5AF75AD"),
		Xiaoji("小吉画画", "小吉画画", "xiaojihuahua", "801357429", 
				"143204e7e6b048a046ac418436a7a4e5", "EA7C4C17F409F40A6821E27BED3D52E3");
		
		private final String appName;
		private final String sinaNick;
		private final String tencentNick;
		private final String tencentClientID;
		private final String tencentClientSecret;
		private final String tencentOpenID;
		
		private App(String appName, String sinaNick, String tencentNick,
				    String tencentClientID, String tencentClientSecret, String tencentOpenID) {
			this.appName = appName;
			this.sinaNick = sinaNick;
			this.tencentNick = tencentNick;
			this.tencentClientID = tencentClientID;
			this.tencentClientSecret = tencentClientSecret;
			this.tencentOpenID = tencentOpenID;
		}

		public String getTencentClientID() {
			return tencentClientID;
		}

		public String getTencentClientId() {
			return tencentClientID;
		}

		public String getTencentClientSecret() {
			return tencentClientSecret;
		}

		public String getAppName() {
			return appName;
		}

		public String getSinaNick() {
			return sinaNick;
		}

		public String getTencentNick() {
			return tencentNick;
		}

		public String getTencentOpenID() {
			return tencentOpenID;
		}
		
		
		
	}
}
