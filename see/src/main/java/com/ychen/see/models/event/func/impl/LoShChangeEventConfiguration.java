package com.ychen.see.models.event.func.impl;

import com.ychen.see.common.enums.IntervalEnum;
import com.ychen.see.models.binance.constant.DataTypeConstant;
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

import static com.ychen.see.models.event.constant.EventConstant.LO_SH_TOP_AMPLITUDE_THRESHOLD;
import static com.ychen.see.models.event.constant.EventConstant.LO_SH_TOP_OFFSET_RANGE;

/**
 * @author yyy
 */
@Slf4j
@Configuration
public class LoShChangeEventConfiguration {

	private static final String dataTypeOfTop = DataTypeConstant.topOiRatio;
	private static final String dataTypeOfAcc = DataTypeConstant.accRatio;

	private static final String[] locationArr = {"高位", "低位"};

	@Bean(DataTypeConstant.topOiRatio + "-day7")
	public ChangeEventFunc topDay7Event() {
		return day7EventBuilder(dataTypeOfTop);
	}

	@Bean(DataTypeConstant.topOiRatio + "-day15")
	public ChangeEventFunc topDay15Event() {
		return day15EventBuilder(dataTypeOfTop);
	}

	@Bean(DataTypeConstant.accRatio + "-day7")
	public ChangeEventFunc accDay7Event() {
		return day7EventBuilder(dataTypeOfAcc);
	}

	@Bean(DataTypeConstant.accRatio + "-day15")
	public ChangeEventFunc accDay15Event() {
		return day15EventBuilder(dataTypeOfAcc);
	}

	private static ChangeEventFunc day7EventBuilder(String dataType) {
		return (curVal, statisticM) -> {
			BigDecimal day7Amplitude = statisticM.getDay7Amplitude();
			// 波动率过小不予处理
			if (LO_SH_TOP_AMPLITUDE_THRESHOLD.compareTo(day7Amplitude) > 0) {
				return null;
			}
			BigDecimal day7LowV = statisticM.getDay7LowV();
			BigDecimal day7HighV = statisticM.getDay7HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day7LowV.multiply(BigDecimal.valueOf(1 + LO_SH_TOP_OFFSET_RANGE))) <= 0) {
				location = locationArr[1];
			} else if (curVal.compareTo(day7HighV.multiply(BigDecimal.valueOf(1 - LO_SH_TOP_OFFSET_RANGE))) >= 0) {
				location = locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder().symbol(statisticM.getSymbol()).dataType(dataType).period(IntervalEnum.w1.name()).location(location).curV(curVal).lowV(day7LowV).highV(day7HighV).eventTime(DateUtil.formatDateTime(new Date())).build();
		};
	}

	private static ChangeEventFunc day15EventBuilder(String dataType) {
		return (curVal, statisticM) -> {
			BigDecimal day15Amplitude = statisticM.getDay15Amplitude();
			// 波动率过小不予处理
			if (LO_SH_TOP_AMPLITUDE_THRESHOLD.compareTo(day15Amplitude) > 0) {
				return null;
			}
			BigDecimal day15LowV = statisticM.getDay15LowV();
			BigDecimal day15HighV = statisticM.getDay15HighV();
			String location = Strings.EMPTY;
			if (curVal.compareTo(day15LowV.multiply(BigDecimal.valueOf(1 + LO_SH_TOP_OFFSET_RANGE))) <= 0) {
				location = locationArr[1];
			} else if (curVal.compareTo(day15HighV.multiply(BigDecimal.valueOf(1 - LO_SH_TOP_OFFSET_RANGE))) >= 0) {
				location = locationArr[0];
			}
			if (StringUtils.isBlank(location)) {
				return null;
			}
			return ChangeEventInfo.builder().symbol(statisticM.getSymbol()).dataType(dataType).period(IntervalEnum.w2.name()).location(location).curV(curVal).lowV(day15LowV).highV(day15HighV).eventTime(DateUtil.formatDateTime(new Date())).build();
		};
	}
}
