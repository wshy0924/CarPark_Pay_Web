package com.ggeit.pay.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ggeit.pay.config.WXPayConstants;
import com.ggeit.pay.config.bz_SysConstants;
import com.ggeit.pay.impl.AliPayServiceImpl;
import com.ggeit.pay.impl.OrderService;
import com.ggeit.pay.impl.WxPayServiceImpl;
import com.ggeit.pay.impl.bz_HisServiceImpl;
import com.ggeit.pay.utils.GGitUtil;

import net.sf.json.JSONArray;


@Controller
@RequestMapping("/lay")

// 支持返回页面只能使用@Controller、不能使用@RestController
public class LayerController {

	private final static Logger logger = LoggerFactory.getLogger(LayerController.class);

	@Autowired
	private WxPayServiceImpl wxpayimpl;
	
	@Autowired
	private AliPayServiceImpl alipayimpl;
	
	@Autowired
	private bz_HisServiceImpl bz_hisserviceimpl;
	
	@Autowired
	private OrderService orderservice;
	

	/**
	 * 试试使用链接
	 * http://test16781.ggzzrj.cn/aggpay/lay/index?peice=1
	 * 更换支付宝appid后，需更新application.xml、aliPayserviceImpl中的回调ip地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response,
			 Map<String, Object> map) throws Exception {
		//判断扫描二维码的app
		logger.info("----------layer index----------");
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		logger.info("index userAgent= " + userAgent);
		
		//userAgent = "|alipayclient";

		if (userAgent.indexOf("micromessenger") > 0) {
			logger.info("----------微信支付----------");
			String url = wxpayimpl.Authorize();
			logger.info("url = " + url);
			return "redirect:" + url;
		} else if (userAgent.indexOf("alipayclient") > 0) {
			logger.info("----------阿里支付----------");
			String url = alipayimpl.Authorize();
			logger.info("url = " + url);
			return "redirect:" + url;
		} else {
			map.put("tips", "未匹配到APP类型");
			return "failure";// 用了html不能使用反斜杠如"/tips"
		}
	}

	@RequestMapping(value = "/wxcallback", method = RequestMethod.GET)
	public String doWxCallBack(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) throws Exception {
		logger.info("----------layer wxcallback----------");
		//------------网页授权认证回调获取code---------------------
		String code = request.getParameter("code");
		String openid = wxpayimpl.GetOpenid(code);
		logger.info("wxcallback code = " + code);
		logger.info("wxcallback openid= " + openid);
		String car_num = "";
		Map<String,Object> selectmap = new HashMap<String,Object>();
		selectmap.put("openid", openid);
		//根据openid查询carnum_info数据库
		 List<Map> carnumlist = orderservice.selectOrder("tc_car.selectByOpenid",selectmap);
	        logger.info("carnumlist = " + carnumlist);	
	        for (int i = 0; i < carnumlist.size(); i++) {
					HashMap temp = (HashMap) carnumlist.get(i);
					logger.info("temp = "+ temp);
					car_num = temp.get("car_num").toString();
					logger.info("carnum = " + car_num);
		        }
	        if(car_num != "") {	
	        	//map.put("tip", "请确认车牌号");
				map.put("car_num", car_num);
				map.put("openid", openid);
				return "WXshow_carnum";
			}else {
				//map.put("tip", "请输入车牌号");
				map.put("openid", openid);
				return "WXcar_num";
			}   
	}
	
	@RequestMapping(value = "/alicallback", method = RequestMethod.GET)
	public String doAliCallBack(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) throws Exception {
		logger.info("----------layer alicallback----------");
		//------------网页授权认证回调获取code---------------------
		String auth_code = request.getParameter("auth_code"); 
		String userid = alipayimpl.GetUserid(auth_code);
		logger.info("alicallback userid= " + userid);

		String car_num = "";
		Map<String,Object> selectmap = new HashMap<String,Object>();
		selectmap.put("openid", userid);
		List<Map> carnumlist = orderservice.selectOrder("tc_car.selectByOpenid",selectmap);
		logger.info("carnumlist = " + carnumlist);	
        for (int i = 0; i < carnumlist.size(); i++) {
				HashMap temp = (HashMap) carnumlist.get(i);
				logger.info("temp = "+ temp);
				car_num = temp.get("car_num").toString();
				logger.info("carnum = " + car_num);
	        }
		 if(car_num != "") {	
	        	//map.put("tip", "请确认车牌号");
				map.put("car_num", car_num);
				map.put("openid", userid);
				return "Alishow_carnum";
			}else {
				//map.put("tip", "请输入车牌号");
				map.put("openid", userid);
				logger.info("map=" + map);
				return "Alicar_num";
			}   
	}

}
