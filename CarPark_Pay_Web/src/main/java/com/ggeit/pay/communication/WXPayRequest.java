package com.ggeit.pay.communication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ggeit.pay.utils.WXPayUtil;



@Service
public class WXPayRequest {
	
	private final static Logger logger = LoggerFactory.getLogger(WXPayRequest.class);
	
	/**
	 * http请求,与微信网关通信
	 * @param url
	 * @param data
	 * @return
	 */
	public Map<String, Object> request(String url, String data) {
		
		logger.info("post请求数据data = " + data);
		//请求数据json转map，进而取出pay_orderid
		Map<String,Object> datamap_out = new HashMap<String,Object>();
		datamap_out=WXPayUtil.wxJsonToMapObj(data);
		logger.info("返回外层tradeParam = " + datamap_out.get("tradeParam"));
		
		//获取内层数据
		Map<String,Object> datamap_in = new HashMap<String,Object>();
		datamap_in = (Map<String, Object>) datamap_out.get("tradeParam");
		logger.info("返回内层的out_trade_no = " + datamap_in.get("out_trade_no"));
		
		
		HttpPost httppost = new HttpPost(url);
		Map<String, Object> strResult = new HashMap<String, Object>();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		StringEntity s;
		String statuscode = "";
		try {
			s = new StringEntity(data,"utf-8");//确保字符编码正确
			s.setContentEncoding("utf-8");
			s.setContentType("application/json");
			httppost.setEntity(s);
			httppost.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
			CloseableHttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				statuscode = response.getStatusLine().getStatusCode() + "";
				String conResult = EntityUtils.toString(response.getEntity());
				strResult.put("statuscode", statuscode);
				strResult.put("conResult", conResult);
				strResult.put("pay_orderid",datamap_in.get("out_trade_no"));
			} else {
				statuscode = response.getStatusLine().getStatusCode() + "";
				strResult.put("statuscode", statuscode);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			statuscode = "999";
			strResult.put("statuscode", statuscode);
			e.printStackTrace();
		}
		logger.info("WXPayRequest request strResult = " + strResult);
		return strResult;
	}
	
	/**
	 * http get 获取openid
	 * @param url
	 * @param params
	 * @return
	 */
	public String httpRequestToString(String url, Map<String, String> params) {
		String result = null;
		try {
			InputStream is = httpRequestToStream(url,  params);
			BufferedReader in = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			result = buffer.toString();
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	
	private static InputStream httpRequestToStream(String url, Map<String, String> params) {
		InputStream is = null;
		try {
			String parameters = "";
			boolean hasParams = false;
			for (String key : params.keySet()) {
				String value = URLEncoder.encode(params.get(key), "UTF-8");
				parameters += key + "=" + value + "&";
				hasParams = true;
			}
			if (hasParams) {
				parameters = parameters.substring(0, parameters.length() - 1);
			}
			url += "?" + parameters;
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.setConnectTimeout(50000);
			conn.setReadTimeout(50000);
			conn.setDoInput(true);
			// 设置请求方式，默认为GET
			conn.setRequestMethod("GET");
			is = conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

}
