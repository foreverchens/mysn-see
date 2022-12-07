package com.ychen.see.models.statistic.domain;

import com.ychen.see.models.binance.constant.DataTypeConstant;

import cn.hutool.core.map.MapUtil;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yyy
 */
public class ContractStatisticDataM {

	@Getter
	private String symbol;

	private Map<String, BaseStatisticM> map;

	public ContractStatisticDataM(String symbol) {
		this.symbol = symbol;
		this.map = new HashMap<>();
		this.map =MapUtil.<String, BaseStatisticM>builder()
					   .put(DataTypeConstant.accRatio, new BaseStatisticM())
					   .put(DataTypeConstant.topPositionRatio, new BaseStatisticM())
					   .put(DataTypeConstant.openInterest, new OpenPositionStatisticM())
					   .put(DataTypeConstant.kline, new KlineStatisticM())
						 .build();
	}

	public BaseStatisticM get(String dataType) {
		return map.get(dataType);
	}
}
