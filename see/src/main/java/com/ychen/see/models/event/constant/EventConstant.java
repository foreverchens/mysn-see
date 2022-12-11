package com.ychen.see.models.event.constant;

import java.math.BigDecimal;

/**
 * 数据数值偏移范围常量
 *
 * @author yyy
 */
public interface EventConstant {

	double VAL_DEFAULT_OFFSET_RANGE = 0.05;


	/**
	 * 大户持仓量的多空比振幅需要大于20个点才予以分析
	 * 高低位点·判断范围限制在5个点之内
	 */
	BigDecimal LO_SH_TOP_AMPLITUDE_THRESHOLD = BigDecimal.valueOf(0.2);
	double LO_SH_TOP_OFFSET_RANGE = 0.05;

	/**
	 * 持仓量振幅需要大于40个点才予以分析
	 * 高低位点·判断范围限制在10个点之内
	 */
	BigDecimal OI_AMPLITUDE_THRESHOLD = BigDecimal.valueOf(0.4);
	double OI_OFFSET_RANGE = 0.1;

}
