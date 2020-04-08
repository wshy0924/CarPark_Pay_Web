package com.ggeit.pay.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ggeit.pay.impl.AliPayServiceImpl;
import com.ggeit.pay.impl.OrderService;
import com.ggeit.pay.impl.WxPayServiceImpl;
import com.ggeit.pay.impl.bz_HisServiceImpl;
import com.ggeit.pay.utils.HisUtil;
import com.ggeit.pay.utils.WXPayUtil;

@Controller
@RequestMapping("/bz_data")
public class bz_HisController {
	private final static Logger logger = LoggerFactory.getLogger(bz_HisController.class);

	@Autowired
	private WxPayServiceImpl wxpayimpl;
	@Autowired
	private AliPayServiceImpl alipayimpl;

	@Autowired
	private bz_HisServiceImpl bz_hisserviceimpl;
	
	@Autowired
	private OrderService orderservice;
	
	/**
	 * sucess支付成功
	 * @param map
	 * @param tips
	 * @return
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success(Map<String, Object> map, String tips,String zffs,String park_info) {
		logger.info("----------layer success----------");

		 try {
			tips = new String(tips.getBytes("ISO-8859-1"),"UTF-8");
			zffs = new String(zffs.getBytes("ISO-8859-1"),"UTF-8");
			park_info = new String(park_info.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("tips", tips);
		map.put("zffs", zffs);	//支付方式
		map.put("park_info", park_info);
		map.put("now_time", HisUtil.aligetTime());	//支付当前时间
		return "success";
	}
	/**
	 * failure支付失败
	 * @param map
	 * @param tips
	 * @return
	 */
	@RequestMapping(value = "/failure", method = RequestMethod.GET)
	public String failure(Map<String, Object> map, String tips) {
		logger.info("----------layer failure----------");
		 try {
				tips = new String(tips.getBytes("ISO-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		map.put("tips", tips);
		return "failure";
	}
}
