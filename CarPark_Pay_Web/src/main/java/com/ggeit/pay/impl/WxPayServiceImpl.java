package com.ggeit.pay.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ggeit.pay.communication.WXPayRequest;
import com.ggeit.pay.config.WXPayConstants;
import com.ggeit.pay.controller.DataController;
import com.ggeit.pay.inf.WxPayServiceInf;
import com.ggeit.pay.utils.WXPayUtil;

@Service
public class WxPayServiceImpl implements WxPayServiceInf {
	
	private final static Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);
	
//	@Value("${para.serverip}")
//	private String serverip;
//	@Value("${para.serverport}")
//    private Integer serverport;
	@Value("${para.wxappid}")
	private String appid;
	@Value("${para.wxsecret}")
	private String secret;
	
    @Autowired
    private WXPayRequest wxrequest;

	/**
	 * 统一支付下单
	 */
	@Override
	public Map<String, Object> UnifiedOrder(String amount,String openid) throws Exception {
		// TODO Auto-generated method stub
//    	Map<String, String> attachmap = new HashMap<String,String>();
//    	attachmap.put("tradeParam", "交易参数");
    	
//		Map<String, Object> tradeparammap = new HashMap<String,Object>();
    	Map<String, String> tradeparammap = new HashMap<String,String>();
    	tradeparammap.put("body", "当日挂号");
    	tradeparammap.put("detail", "挂号费2.0元");
    	tradeparammap.put("attach", "{\"router\": \"scan\"}");
    	tradeparammap.put("total_fee", WXPayUtil.wxchangeY2F(amount).substring(0, WXPayUtil.wxchangeY2F(amount).indexOf(".")));
    	tradeparammap.put("spbill_create_ip", "192.168.1.200");
    	tradeparammap.put("openid", openid);
    	tradeparammap.put("out_trade_no", WXPayUtil.wxcreateOrderID());
    	tradeparammap.put("trade_type", "JSAPI");
    	tradeparammap.put("limit_pay", "no_credit");
    	tradeparammap.put("product_id", "");
    	tradeparammap.put("nonce_str", WXPayUtil.wxgetRandomPlus(20));
    	
    	Map<String, Object> requestmap = new HashMap<String,Object>();
    	requestmap.put("tradeType", "UnifiedPay");
    	requestmap.put("tradeParam", tradeparammap);
    	requestmap.put("tradeRemark", "remark");
    	requestmap.put("sign", WXPayUtil.wxgenerateSignature(tradeparammap, WXPayConstants.MD5KEY));
    	
    	Map<String, Object> resp = wxrequest.request(WXPayConstants.URL, WXPayUtil.wxMapToJson(requestmap));
    	logger.info("UnifiedOrder resp = "+resp);
        return resp;
	}

    /**
     * 从支付网关获取MD5签名
     */
	@Override
	public Map<String, Object> GenerateMD5(String prepay_id,String appid,String timeStamp,String randomplus) throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> tradeparammap = new HashMap<String,String>();
		tradeparammap.put("appId", this.appid);
		//tradeparammap.put("timeStamp", Long.toString(new Date().getTime()/1000));
		tradeparammap.put("timeStamp", timeStamp);
		//tradeparammap.put("nonceStr", WXPayUtil.wxgetRandomPlus(20));
		tradeparammap.put("nonceStr", randomplus);
		tradeparammap.put("package", "prepay_id="+prepay_id);
		tradeparammap.put("signType", "MD5");
		
		Map<String, Object> requestmap = new HashMap<String,Object>();
    	requestmap.put("tradeType", "MD5Sign");
    	requestmap.put("tradeParam", tradeparammap);
    	requestmap.put("tradeRemark", "");
    	requestmap.put("sign", WXPayUtil.wxgenerateSignature(tradeparammap, WXPayConstants.MD5KEY));
    	Map<String, Object> resp = wxrequest.request(WXPayConstants.URL, WXPayUtil.wxMapToJson(requestmap));
    	logger.info("GenerateMD5 resp = "+resp);
        return resp;
	}

	@Override
	public String Authorize() throws Exception {
		// TODO Auto-generated method stub
		StringBuffer url = new StringBuffer();
		url.append("http://open.weixin.qq.com/connect/oauth2/authorize?");
		url.append("appid="+this.appid);
		//?后参数需经过加密后进行传值
		url.append("&redirect_uri="+URLEncoder.encode("http://www.ggzzrj.cn/")+URLEncoder.encode("aggpay/lay/wxcallback"));
		url.append("&response_type=code&scope=snsapi_base&state=");//state参数是给传值的
	    url.append("#wechat_redirect");
	    return  url.toString();
	}

	@Override
	public String GetOpenid(String code) throws Exception {
		// TODO Auto-generated method stub
		Map params = new HashMap();
		params.put("secret", this.secret);
		params.put("appid", this.appid);
		params.put("grant_type", "authorization_code");
		params.put("code", code);
		String result = wxrequest.httpRequestToString(
				"https://api.weixin.qq.com/sns/oauth2/access_token", params);
		
		logger.info("GetOpenid result = "+result);
		Map<String, Object> map = WXPayUtil.wxJsonToMapObj(result);
		
		logger.info("GetOpenid map = "+map);
		
		return (String) map.get("openid");
	}

	/**
	 * 一码付后台生成MD签名
	 */
	@Override
	public Map<String, Object> InsideGenerateMD5(String prepay_id, String appid, String timeStamp, String randomplus)
			throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> tradeparammap = new HashMap<String,String>();
		tradeparammap.put("appId", this.appid);
		tradeparammap.put("timeStamp", timeStamp);
		tradeparammap.put("nonceStr", randomplus);
		tradeparammap.put("package", "prepay_id="+prepay_id);
		tradeparammap.put("signType", "MD5");
		

		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, Object> responsedata = new HashMap<String,Object>();
		responsedata.put("returnCode", "0000");
		responsedata.put("returnInfo", "正常");
		responsedata.put("data", WXPayUtil.wxgenerateSignature(tradeparammap, WXPayConstants.WXMD5KEY));

		resp.put("statuscode", "200");
		resp.put("conResult", WXPayUtil.wxMapToJson(responsedata));
		
		logger.info("InsideGenerateMD5 resp = " + resp);
        return resp;
	}

	@Override
	public Map<String, Object> Wx_Refund(String amount,String out_trade_no ) throws Exception {
		// TODO Auto-generated method stub
		String price = WXPayUtil.wxchangeY2F(amount).substring(0, WXPayUtil.wxchangeY2F(amount).indexOf("."));
		Map<String, String> refundparammap = new HashMap<String,String>();
		refundparammap.put("out_trade_no", out_trade_no);
		refundparammap.put("out_refund_no", WXPayUtil.wxcreateOrderID());
		refundparammap.put("total_fee", price);
		refundparammap.put("refund_fee", price);
		
    	
    	Map<String, Object> requestmap = new HashMap<String,Object>();
    	requestmap.put("tradeType", "OrderRefund");
    	requestmap.put("tradeParam", refundparammap);
    	requestmap.put("tradeRemark", "remark");
    	requestmap.put("sign", WXPayUtil.wxgenerateSignature(refundparammap, WXPayConstants.MD5KEY));//对tradeParam进行MD5加密
    	
    	Map<String, Object> resp = wxrequest.request(WXPayConstants.URL, WXPayUtil.wxMapToJson(requestmap));
    	logger.info("Wx_Refund resp = "+resp);
        return resp;
		

	}

}
