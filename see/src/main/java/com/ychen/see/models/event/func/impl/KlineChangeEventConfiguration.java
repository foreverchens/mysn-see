package com.ychen.see.models.event.func.impl;

import com.ychen.see.common.enums.IntervalEnum;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.event.constant.EventConstant;
import com.ychen.see.models.event.domain.ChangeEventInfo;
import com.ychen.see.models.event.func.ChangeEventFunc;

import cn.hutool.core.date.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author yyy
 */
@Slf4j
public class KlineChangeEventConfiguration {

	private static final String dataType = DataTypeConstant.kline;

	private static final String[] locationArr = {"高位", "低位"};

	public static List<ChangeEventFunc> listKlineEvent() {
		return Arrays.asList(day3Event(), day7Event(), day15Event());
	}


	private static ChangeEventFunc day3Event() {
		return (curVal, statisticM) -> {
			BigDecimal day3LowV = statisticM.getDay3LowV();
			BigDecimal day3HighV = statisticM.getDay3HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day3LowV.multiply(BigDecimal.valueOf(1 + EventConstant.VAL_DEFAULT_OFFSET_RANGE))) <= 0) {
				location = locationArr[1];
			} else if (curVal.compareTo(day3HighV.multiply(BigDecimal.valueOf(1 - EventConstant.VAL_DEFAULT_OFFSET_RANGE))) >= 0) {
				location = locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder()
								  .symbol(statisticM.getSymbol())
								  .dataType(dataType)
								  .period(IntervalEnum.d3.name())
								  .location(location)
								  .curV(curVal)
								  .lowV(day3LowV)
								  .highV(day3HighV)
								  .eventTime(DateUtil.formatDateTime(new Date()))
								  .build();
		};
	}

	private static ChangeEventFunc day7Event() {
		return (curVal, statisticM) -> {
			BigDecimal day7LowV = statisticM.getDay7LowV();
			BigDecimal day7HighV = statisticM.getDay7HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day7LowV.multiply(BigDecimal.valueOf(1 + EventConstant.VAL_DEFAULT_OFFSET_RANGE))) <= 0) {
				location = locationArr[1];
			} else if (curVal.compareTo(day7HighV.multiply(BigDecimal.valueOf(1 - EventConstant.VAL_DEFAULT_OFFSET_RANGE))) >= 0) {
				location = locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder()
								  .symbol(statisticM.getSymbol())
								  .dataType(dataType)
								  .period(IntervalEnum.w1.name())
								  .location(location)
								  .curV(curVal)
								  .lowV(day7LowV)
								  .highV(day7HighV)
								  .eventTime(DateUtil.formatDateTime(new Date()))
								  .build();
		};
	}

	private static ChangeEventFunc day15Event() {
		return (curVal, statisticM) -> {
			BigDecimal day15LowV = statisticM.getDay15LowV();
			BigDecimal day15HighV = statisticM.getDay15HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day15LowV.multiply(BigDecimal.valueOf(1 + EventConstant.VAL_DEFAULT_OFFSET_RANGE))) <= 0) {
				location = locationArr[1];
			} else if (curVal.compareTo(day15HighV.multiply(BigDecimal.valueOf(1 - EventConstant.VAL_DEFAULT_OFFSET_RANGE))) >= 0) {
				location = locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder()
								  .symbol(statisticM.getSymbol())
								  .dataType(dataType)
								  .period(IntervalEnum.w2.name())
								  .location(location)
								  .curV(curVal)
								  .lowV(day15LowV)
								  .highV(day15HighV)
								  .eventTime(DateUtil.formatDateTime(new Date()))
								  .build();
		};
	}
}
