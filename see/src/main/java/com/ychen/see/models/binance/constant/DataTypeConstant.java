package com.ychen.see.models.binance.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 合约数据类型常量
 *
 * @author yyy
 */
public interface DataTypeConstant {

	String oi = "持仓量";
	String accRatio = "账户数多空比";
	String topOiRatio = "大户持仓量多空比";

	String kline = "k线价格";

	List<String> typeList = Arrays.asList(oi, accRatio, topOiRatio, kline);
}
