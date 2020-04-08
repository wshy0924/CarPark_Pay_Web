package com.ggeit.pay.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ggeit.pay.impl.OrderService;
import com.ggeit.pay.utils.GGitUtil;
import com.ggeit.pay.utils.PayUtil;

@Controller
@RequestMapping("/lay")           //微信后台配置的地址为http://wwww.ggzzrj.cn/aggpay/lay

// 支持返回页面只能使用@Controller、不能使用@RestController
public class CarPayController {

	private final static Logger logger = LoggerFactory.getLogger(CarPayController.class);
	
	@Autowired
	private OrderService orderservice;
	@Autowired
	private GGitUtil ggitutil;
	
	@RequestMapping(value = "/wxshowcarfee", method = RequestMethod.POST)
	  public String wxshowcarfee(HttpServletRequest request, HttpServletResponse response,
			  @RequestParam("car_num") String car_num, @RequestParam("openid") String openid,Map<String, Object> map) {
		logger.info("-------------------接收wxcarnum--------------------");
		logger.info("carnum = " + car_num);
		logger.info("openid = " + openid);
		String price ="";
		
		//插入openid和car_num至数据库，若数据库中已存在，则更新数据库，达到一个openid只保存一个车牌号
		Map<String,Object> insertmap = new HashMap<String,Object>(); 
		insertmap.put("openid", openid); 
		insertmap.put("car_num", car_num); 
		boolean insertstatus = orderservice.insert("tc_car.insertCarnum", insertmap);
		logger.info("car_num 插入数据库状态：" + insertstatus);
		//根据car_num和end_time查询车辆管理系统的订单金额
		
		//模拟从数据库中查询数据
		Map<String,Object> selectmap = new HashMap<String,Object>();
		String endtime = PayUtil.aligetTime();
		 logger.info("end_time = " + endtime);
		 
		 selectmap.put("car_num", car_num);
		// selectmap.put("end_time", endtime);
		List<Map> datalist = orderservice.selectOrder("tc_carfee.selectBycarnumAndtime",selectmap);
	     logger.info("datalist = " + datalist);	
	     for (int i = 0; i < datalist.size(); i++) {
				HashMap temp = (HashMap) datalist.get(i);
				logger.info("temp = "+ temp);
			    price = temp.get("price").toString();
	        }
	     if(price != "") {
	    	 map.put("price", price);
			 map.put("openid", openid);
			return "mmpayconfirm";
	     }else {
	    	 map.put("tips", "未查到车牌号费用信息，请检查车牌号是否正确！");
	    	 return "failure";
	     }

  }
	
	@RequestMapping(value = "/Alishowcarfee", method = RequestMethod.POST)
	  public String alishowcarfee(HttpServletRequest request, HttpServletResponse response,
			  @RequestParam("car_num") String car_num, @RequestParam("openid") String openid,Map<String, Object> map) {
		logger.info("-------------------接收alicarnum--------------------");
		logger.info("carnum = " + car_num);
		logger.info("openid = " + openid);
		String price ="";
		
		//插入openid和car_num至数据库，若数据库中已存在，则更新数据库，达到一个openid只保存一个车牌号
		Map<String,Object> insertmap = new HashMap<String,Object>(); 
		insertmap.put("openid", openid); 
		insertmap.put("car_num", car_num); 
		boolean insertstatus = orderservice.insert("tc_car.insertCarnum", insertmap);
		logger.info("car_num 插入数据库状态：" + insertstatus);
		//根据car_num和end_time查询车辆管理系统的订单金额
		//对接管理系统接口，根据API接口查询车辆订单金额
		
		
		//模拟从数据库中查询数据
		Map<String,Object> selectmap = new HashMap<String,Object>();
		String endtime = PayUtil.aligetTime();
		 logger.info("end_time = " + endtime);
		 
		 selectmap.put("car_num", car_num);
		// selectmap.put("end_time", endtime);
		List<Map> datalist = orderservice.selectOrder("tc_carfee.selectBycarnumAndtime",selectmap);
	     logger.info("datalist = " + datalist);	
	     for (int i = 0; i < datalist.size(); i++) {
				HashMap temp = (HashMap) datalist.get(i);
				logger.info("temp = "+ temp);
			    price = temp.get("price").toString();
	        }
	     if(price != "") {
	    	 map.put("price", price);
			 map.put("userid", openid);
			return "alipayconfirm";
	     }else {
	    	 map.put("tips", "未查到车牌号费用信息，请检查车牌号是否正确！");
	    	 return "failure";
	     }

}
	
	
	@RequestMapping(value = "/wxShowtoWrite", method = RequestMethod.POST)
	  public String show2write(HttpServletRequest request, HttpServletResponse response,@RequestParam("openid") String openid,
			  Map<String, Object> map ,Model model) {
		logger.info("-------------------接收wxcarnum--------------------");
//		map.put("tip", "请输入车牌号");
		map.put("openid", openid);
		return "WXcar_num";
//		Map<String,Object> selectmap = new HashMap<String,Object>();
//		selectmap.put("openid", openid);
//		 List<Map> carnumlist = orderservice.selectOrder("tc_car.selectByOpenid",selectmap);
//	        logger.info("carnumlist = " + carnumlist);
//	        model.addAttribute("datamap", carnumlist);
//		return "bangding";
	}
	
	@RequestMapping(value = "/AliShowtoWrite", method = RequestMethod.POST)
	  public String alishow2write(HttpServletRequest request, HttpServletResponse response,@RequestParam("openid") String openid,
			  Map<String, Object> map ,Model model) {
		logger.info("-------------------接收alicarnum--------------------");
//		map.put("tip", "请输入车牌号");
		map.put("openid", openid);
		return "Alicar_num";
	}
}
    
