package com.ychen.see.models.event.func.impl;

import com.ychen.see.common.enums.IntervalEnum;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.event.constant.EventConstant;
import com.ychen.see.models.event.domain.ChangeEventInfo;
import com.ychen.see.models.event.func.ChangeEventFunc;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author yyy
 */
@Slf4j
public class OpenPosChangeEventConfiguration {

	private static final String dataType = DataTypeConstant.openInterest;

	private static final String period = IntervalEnum.d3.name();

	private static final String[] locationArr = {"高位", "低位"};


	public static List<ChangeEventFunc> listOpenPosEvent() {
		return Arrays.asList(day3Event());
	}


	private static ChangeEventFunc day3Event() {
		return (curVal, statisticM) -> {
			BigDecimal day3LowV = statisticM.getDay3LowV();
			BigDecimal day3HighV = statisticM.getDay3HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day3LowV.multiply(BigDecimal.valueOf(1 + EventConstant.VAL__DEFAULT_OFFSET_RANGE))) <= 0) {
				location = locationArr[1];
			} else if (curVal.compareTo(day3HighV.multiply(BigDecimal.valueOf(1 - EventConstant.VAL__DEFAULT_OFFSET_RANGE))) >= 0) {
				location = locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder()
								  .symbol(statisticM.getSymbol())
								  .dataType(dataType)
								  .period(period)
								  .location(location)
								  .build();
		};
	}

}
