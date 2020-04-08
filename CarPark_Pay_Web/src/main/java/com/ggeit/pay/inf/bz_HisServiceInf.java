package com.ggeit.pay.inf;

import java.util.Map;

import net.sf.json.JSONArray;

public interface bz_HisServiceInf {

	Map<String, Object> Queryinfo(Map<String, Object> reqmap);//his查询接口
	
//	int craculate_price(JSONArray jsonarray);//计算订单总额
	
	Map<String,Object> ConfirmPayStatus(Map<String,Object> map);//his查询订单支付状态
}