package com.ggeit.pay.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ggeit.pay.communication.bz_HisServiceRequest;
import com.ggeit.pay.config.bz_SysConstants;
import com.ggeit.pay.inf.bz_HisServiceInf;
import com.ggeit.pay.utils.GGitUtil;

import net.sf.json.JSONArray;
@Service
public class bz_HisServiceImpl implements bz_HisServiceInf {

	private final static Logger logger = LoggerFactory.getLogger(bz_HisServiceImpl.class);
	
	@Value("${para.hisappid}")
	private String appid;
	
	@Autowired
	private bz_HisServiceRequest hisrequest;
	/**
	 * 查询代缴费信息
	 */
	@Override
	public Map<String, Object> Queryinfo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
//			String yllb = bz_SysConstants.yllbmap.get("CF");//处方
			
			Map<String, Object> reqmap = new HashMap<String, Object>();
			reqmap.put("TransCode", "F5001");
			reqmap.put("appid", this.appid); //appid，由his生成
//			reqmap.put("cfsb", map.get("cfsb"));
			reqmap.put("brid", map.get("brid"));
//			reqmap.put("kdrq", map.get("kdrq"));
//			reqmap.put("name", map.get("name"));	
//			reqmap.put("yllb", "CF"); //医疗类表，CF处方  YJ 检验检查
			String jsonstring = GGitUtil.MapToJson(reqmap); //请求map转成json string
			logger.info("向his发送的post请求消息：" + jsonstring);
			
			//接收his返回map
			resp = hisrequest.dopost(bz_SysConstants.req_url, jsonstring);
			
			
//			//挡板测试返回map
//			Map<String,Object> datamap = new HashMap<String,Object>();//data中list数据
//			datamap.put("cfsb", "2653501");
//			datamap.put("ysdm", "504");
//			datamap.put("ysmc", "");
//			datamap.put("ksdm", "499");
//			datamap.put("ksmc", "内科");
//			datamap.put("kdrq", "2019-09-16 16:07:26");
//			datamap.put("yllb", "CF");
//			datamap.put("hjje", "2.65");
//			
//			resp.put("ResultCode", "0");
//			resp.put("ErrorMsg", "");
//			resp.put("data", datamap);
//			logger.info("测试返回的resp = " + resp);
		
			
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
//	/**
//	 * 计算处方总金额
//	 * 各个金额相加
//	 */
//	@Override
//	public int craculate_price(JSONArray jsonarray) {
//		// TODO Auto-generated method stub
//		 int total_price = 0;
//	        for(int i=0;i<jsonarray.size();i++) {
//	        	int price = Integer.parseInt((String) jsonarray.getJSONObject(i).get("je"));
//	        	total_price = total_price + price; 
//	        }
//		return total_price;
//	}
	/**
	 * 确认支付状态
	 */
	@Override
	public Map<String, Object> ConfirmPayStatus(Map<String, Object> map) {
		// TODO Auto-generated method stub
		Map<String, Object> resp = new HashMap<String, Object>(); 
		try {
			
			Map<String, Object> reqmap = new HashMap<String, Object>();
			reqmap.put("TransCode", "F5004");
			reqmap.put("brid", map.get("brid"));
			reqmap.put("appid", this.appid); //appid，由his生成
			reqmap.put("brxz", "1"); // 病人性质，1自费
			reqmap.put("zffs", map.get("zffs"));
			reqmap.put("zflsh", map.get("zflsh"));
			reqmap.put("hjje", map.get("hjje"));
			reqmap.put("receiptList", map.get("receiptList"));	

			String jsonstring = GGitUtil.MapToJson(reqmap); //请求map转成jsonstring
			logger.info("Datacontroller向his发送的post请求消息：" + jsonstring);
			
			//接收his返回map
			resp = hisrequest.dopost(bz_SysConstants.req_url_querystatus, jsonstring);
		
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	
	
}

