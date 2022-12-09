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
public class SymbolKlineStatisticM extends SymbolBaseStatisticM {

	private BigDecimal shakeVal;


	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
