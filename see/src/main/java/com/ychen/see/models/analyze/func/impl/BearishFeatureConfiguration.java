package com.ychen.see.models.analyze.func.impl;

import com.ychen.see.common.CallResult;
import com.ychen.see.models.analyze.constant.AnalyzeConstant;
import com.ychen.see.models.analyze.func.MarketFeatureFunc;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.event.constant.EventConstant;
import com.ychen.see.models.event.domain.ChangeEventInfo;

import cn.hutool.core.collection.CollectionUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yyy
 */
@Slf4j
@Configuration
public class BearishFeatureConfiguration {


	/**
	 * 出货行情
	 * 价格持仓量双高位、
	 * 多空比可能低位
	 */
	@Bean
	public MarketFeatureFunc highOi() {
		return (symbol, eventInfoList) -> {
			Map<String, List<ChangeEventInfo>> dataTypeToListMap =
					eventInfoList.stream().collect(Collectors.groupingBy(ChangeEventInfo::getDataType));
			// 持仓量事件列表
			List<ChangeEventInfo> eventOiList = dataTypeToListMap.get(DataTypeConstant.oi);
			if (CollectionUtil.isEmpty(eventOiList)) {
				return CallResult.failure("不存在持仓量事件");
			}
			List<String> highOiEventList = eventOiList.stream().filter(ele -> StringUtils.equals(ele.getLocation(),
					EventConstant.locationArr[0])).map(ChangeEventInfo::getPeriod).collect(Collectors.toList());
			if (CollectionUtil.isEmpty(highOiEventList)) {
				return CallResult.failure("不存在高持仓量事件");
			}
			// 看跌指数、越高越好
			int rlt = 0;
			rlt += sumVeVal(highOiEventList);

			// 价格事件列表
			List<ChangeEventInfo> eventKlineList = dataTypeToListMap.get(DataTypeConstant.kline);
			if (CollectionUtil.isNotEmpty(eventKlineList)) {
				List<String> highPEventList =
						eventKlineList.stream().filter(ele -> StringUtils.equals(ele.getLocation(),
						EventConstant.locationArr[0])).map(ChangeEventInfo::getPeriod).collect(Collectors.toList());
				if (CollectionUtil.isNotEmpty(highPEventList)) {
					rlt += sumVeVal(highPEventList);
				}
			}
			return CallResult.success(symbol + "-持仓量高位符合、看跌指数:" + rlt);
		};
	}

	/**
	 * 波动率足够高的震荡行情至价格高位
	 */
	// @Bean
	public MarketFeatureFunc highP() {
		return (symbol, eventInfoList) -> {
			Map<String, List<ChangeEventInfo>> dataTypeToListMap =
					eventInfoList.stream().collect(Collectors.groupingBy(ChangeEventInfo::getDataType));
			int rlt = 0;
			// TODO: 2022/12/22
			return CallResult.success("震荡高位符合、看跌指数:" + rlt);
		};
	}

	private static int sumVeVal(List<String> highOiEventList) {
		int rlt = 0;
		for (String period : highOiEventList) {
			Integer veVal = AnalyzeConstant.vePeriodMap.get(period);
			if (Objects.isNull(veVal)) {
				log.error("该周期权重未定义:" + period);
				continue;
			}
			rlt += veVal;
		}
		return rlt;
	}

}
