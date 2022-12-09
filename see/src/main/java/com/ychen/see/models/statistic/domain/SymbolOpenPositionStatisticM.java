package com.ychen.see.models.statistic.domain;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author yyy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymbolOpenPositionStatisticM extends SymbolBaseStatisticM {


	private BigDecimal day15HighV;

	private BigDecimal day15LowV;


	private BigDecimal day30HighV;

	private BigDecimal day30LowV;

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
