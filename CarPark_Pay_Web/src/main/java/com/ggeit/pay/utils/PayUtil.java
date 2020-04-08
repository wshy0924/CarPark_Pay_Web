package com.ggeit.pay.utils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PayUtil {

	private final static Logger logger = LoggerFactory.getLogger(PayUtil.class);

	
	
	public static String alicreateOrderID() {
		return aligetTime() + aligetRandom(6);
	}
	
	public static String aligetTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		return df.format(System.currentTimeMillis());
	}
	
	public static String aligetRandom(int len) {
		StringBuffer flag = new StringBuffer();
		String sources = "0123456789";
		Random rand = new Random();
		for (int j = 0; j < len; j++) {
			flag.append(sources.charAt(rand.nextInt(9)) + "");
		}
		return flag.toString();
	}

}
