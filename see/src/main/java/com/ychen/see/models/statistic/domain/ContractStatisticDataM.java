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

	private Map<String, SymbolBaseStatisticM> map;

	public ContractStatisticDataM(String symbol) {
		this.symbol = symbol;
		this.map = new HashMap<>();
		this.map =MapUtil.<String, SymbolBaseStatisticM>builder()
					   .put(DataTypeConstant.accRatio, new SymbolBaseStatisticM())
					   .put(DataTypeConstant.topPositionRatio, new SymbolBaseStatisticM())
					   .put(DataTypeConstant.openInterest, new SymbolOpenPositionStatisticM())
					   .put(DataTypeConstant.kline, new SymbolKlineStatisticM())
						 .build();
	}

	public SymbolBaseStatisticM get(String dataType) {
		return map.get(dataType);
	}
}
