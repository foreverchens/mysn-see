package com.ychen.see.models.event.func.impl;

import com.ychen.see.common.enums.IntervalEnum;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.event.constant.EventConstant;
import com.ychen.see.models.event.domain.ChangeEventInfo;
import com.ychen.see.models.event.func.ChangeEventFunc;

import cn.hutool.core.date.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

import static com.ychen.see.models.event.constant.EventConstant.OI_AMPLITUDE_THRESHOLD;
import static com.ychen.see.models.event.constant.EventConstant.OI_OFFSET_RANGE;

/**
 * @author yyy
 */
@Slf4j
@Configuration
public class OiChangeEventConfiguration {

	private static final String dataType = DataTypeConstant.oi;


	@Bean(DataTypeConstant.oi + "-day3")
	public ChangeEventFunc day3Event() {
		return (curVal, statisticM) -> {
			BigDecimal day3Amplitude = statisticM.getDay3Amplitude();
			// 波动率过小不予处理
			if (OI_AMPLITUDE_THRESHOLD.compareTo(day3Amplitude) > 0) {
				return null;
			}
			BigDecimal day3LowV = statisticM.getDay3LowV();
			BigDecimal day3HighV = statisticM.getDay3HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day3LowV.multiply(BigDecimal.valueOf(1 + OI_OFFSET_RANGE))) <= 0) {
				location = EventConstant.locationArr[1];
			} else if (curVal.compareTo(day3HighV.multiply(BigDecimal.valueOf(1 - OI_OFFSET_RANGE))) >= 0) {
				location = EventConstant.locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder().symbol(statisticM.getSymbol()).dataType(dataType).period(IntervalEnum.d3.name()).location(location).curV(curVal).lowV(day3LowV).highV(day3HighV).eventTime(DateUtil.formatDateTime(new Date())).build();
		};
	}

	@Bean(DataTypeConstant.oi + "-day7")
	public ChangeEventFunc day7Event() {
		return (curVal, statisticM) -> {
			BigDecimal day7Amplitude = statisticM.getDay7Amplitude();
			// 波动率过小不予处理
			if (OI_AMPLITUDE_THRESHOLD.compareTo(day7Amplitude) > 0) {
				return null;
			}
			BigDecimal day7LowV = statisticM.getDay7LowV();
			BigDecimal day7HighV = statisticM.getDay7HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day7LowV.multiply(BigDecimal.valueOf(1 + OI_OFFSET_RANGE))) <= 0) {
				location = EventConstant.locationArr[1];
			} else if (curVal.compareTo(day7HighV.multiply(BigDecimal.valueOf(1 - OI_OFFSET_RANGE))) >= 0) {
				location = EventConstant.locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder().symbol(statisticM.getSymbol()).dataType(dataType).period(IntervalEnum.w1.name()).location(location).curV(curVal).lowV(day7LowV).highV(day7HighV).eventTime(DateUtil.formatDateTime(new Date())).build();
		};
	}

	@Bean(DataTypeConstant.oi + "-day15")
	public ChangeEventFunc day15Event() {
		return (curVal, statisticM) -> {
			BigDecimal day15Amplitude = statisticM.getDay15Amplitude();
			// 波动率过小不予处理
			if (OI_AMPLITUDE_THRESHOLD.compareTo(day15Amplitude) > 0) {
				return null;
			}
			BigDecimal day15LowV = statisticM.getDay15LowV();
			BigDecimal day15HighV = statisticM.getDay15HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day15LowV.multiply(BigDecimal.valueOf(1 + OI_OFFSET_RANGE))) <= 0) {
				location = EventConstant.locationArr[1];
			} else if (curVal.compareTo(day15HighV.multiply(BigDecimal.valueOf(1 - OI_OFFSET_RANGE))) >= 0) {
				location = EventConstant.locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder().symbol(statisticM.getSymbol()).dataType(dataType).period(IntervalEnum.w2.name()).location(location).curV(curVal).lowV(day15LowV).highV(day15HighV).eventTime(DateUtil.formatDateTime(new Date())).build();
		};
	}
}
