package com.ggeit.pay.utils;

import java.text.SimpleDateFormat;

public class HisUtil {
	public static String aligetTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(System.currentTimeMillis());
	}
//	public static void main(String[] args) {
//		aligetTime();
//		System.out.println(aligetTime());
//	}
}
