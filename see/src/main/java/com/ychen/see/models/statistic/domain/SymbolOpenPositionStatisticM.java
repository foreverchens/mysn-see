package com.ychen.see.models.statistic.domain;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yyy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymbolOpenPositionStatisticM extends SymbolBaseStatisticM {

	private BigDecimal day30HighV;

	private BigDecimal day30LowV;

	public SymbolOpenPositionStatisticM(String symbol) {
		super(symbol);
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
