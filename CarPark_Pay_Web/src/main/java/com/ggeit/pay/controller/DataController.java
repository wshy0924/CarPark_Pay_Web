package com.ggeit.pay.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.response.AlipayTradeCreateResponse;
import com.ggeit.pay.communication.WXPayRequest;
import com.ggeit.pay.impl.AliPayServiceImpl;
import com.ggeit.pay.impl.WxPayServiceImpl;
import com.ggeit.pay.impl.bz_HisServiceImpl;
import com.ggeit.pay.utils.WXPayUtil;

@RestController
@RequestMapping("/data")
public class DataController {
	
	private final static Logger logger = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private WxPayServiceImpl wxpayimpl;
	@Autowired
	private AliPayServiceImpl alipayimpl;
	@Autowired
	private bz_HisServiceImpl bz_hisserviceimpl;

	/**
	 * 微信 这里return是返回ajax是否成功，通过data具体返回交易逻辑处理是否成功 逻辑处理完后，下一步动作可以返回给原始JSP去处理
	 * 
	 * @param remarkinfo
	 * @param amount
	 * @throws Exception
	 */
	@RequestMapping(value = "/dowxpay", method = RequestMethod.POST)
	public Map<String, Object> doWxPay(@RequestParam("amount") String amount,@RequestParam("openid") String openid) throws Exception {
		logger.info("----------data dowxpay----------");
		logger.info("amount = " + amount);
		logger.info("openid = " +openid);

		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> unifiedordermap = wxpayimpl.UnifiedOrder(amount,openid);
		if (unifiedordermap.get("statuscode").equals("200")) {
			String conResult = (String) unifiedordermap.get("conResult");
			Map<String, Object> conResultmap = WXPayUtil.wxJsonToMapObj(conResult);
			if (conResultmap.get("returnCode").equals("0000")) {
				Map<String, Object> datamap = (Map<String, Object>) conResultmap.get("data");
				String timeStamp = Long.toString(new Date().getTime()/1000);
				String randomplus = WXPayUtil.wxgetRandomPlus(20);
				//Map<String, Object> generatemd5map = wxpayimpl.GenerateMD5((String) datamap.get("prepay_id"),(String) datamap.get("appid"),timeStamp,randomplus);
				Map<String, Object> generatemd5map = wxpayimpl.InsideGenerateMD5((String) datamap.get("prepay_id"),(String) datamap.get("appid"),timeStamp,randomplus);
				if (generatemd5map.get("statuscode").equals("200")) {
					conResult = (String) generatemd5map.get("conResult");
					conResultmap = WXPayUtil.wxJsonToMapObj(conResult);
					if (conResultmap.get("returnCode").equals("0000")) {
						map.put("retcode", "0000");
						map.put("msg", "支付成功!");
						map.put("md5", conResultmap.get("data"));
						map.put("appid", datamap.get("appid"));
						map.put("mch_id", datamap.get("mch_id"));
						map.put("nonce_str", randomplus);
						map.put("prepay_id", "prepay_id="+datamap.get("prepay_id"));  
						map.put("timeStamp", timeStamp); 
						map.put("pay_orderid",unifiedordermap.get("pay_orderid"));
					}else {
						map.put("retcode", "1111");
						map.put("msg", "支付失败!");
					}
				} else {
					map.put("retcode", "1111");
					map.put("msg", "支付失败!");
				}
			} else {
				map.put("retcode", "1111");
				map.put("msg", "支付失败!");
			}
		} else {
			map.put("retcode", "1111");
			map.put("msg", "支付失败!");
		}
		logger.info("ajax return map = "+map);
		return map;
	}

	/**
	 * 阿里 这里return是返回ajax是否成功，通过data具体返回交易逻辑处理是否成功 逻辑处理完后，下一步动作可以返回给原始JSP去处理
	 * 
	 * @param remarkinfo
	 * @param amount
	 * @throws Exception 
	 */
	@RequestMapping(value = "/doalipay", method = RequestMethod.POST)
	public Map<String, String> doAliPay(@RequestParam("amount") String amount,@RequestParam("userid") String userid){
		logger.info("----------data dopay----------");
		//logger.info("remarkinfo = " + remarkinfo);
		Map<String, String> map = new HashMap<String, String>(); //返回数据
		try {
			AlipayTradeCreateResponse unifiedorderresp = alipayimpl.UnifiedOrder(amount, userid);
			logger.info("doalipay unifiedorderresp = " +unifiedorderresp);
			if(unifiedorderresp.isSuccess()){
				logger.info("----------支付宝统一下单成功----------");
				logger.info("doalipay TradeNo = "+unifiedorderresp.getTradeNo());//支付宝交易号
				logger.info("doalipay OutTradeNo = "+unifiedorderresp.getOutTradeNo());//商户订单号
				logger.info("doalipay Body = "+unifiedorderresp.getBody());
				map.put("tradeNO", unifiedorderresp.getTradeNo());
				map.put("pay_orderid", unifiedorderresp.getOutTradeNo());
				//map.put("retcode", "0000");
				//map.put("msg", "支付成功!");
			}else {
				map.put("retcode", "1111");
				map.put("msg", "支付失败!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map.put("retcode", "1111");
			map.put("msg", "支付失败!");
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 阿里 这里return是返回ajax是否成功，通过data具体返回交易逻辑处理是否成功 逻辑处理完后，下一步动作可以返回给原始JSP去处理
	 * 
	 * @param remarkinfo
	 * @param amount
	 */
	@RequestMapping(value = "/ali_notify", method = RequestMethod.POST)
	public Map<String, String> alinotify(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String,Object> paramMap) {
		logger.info("ali 异步通知paramMap = " + paramMap);
		logger.info("--------------alipay 接收异步通知 消息a------------------------------");
		//对接收数据验签处理
				
		
		return null;
		
	}
}
