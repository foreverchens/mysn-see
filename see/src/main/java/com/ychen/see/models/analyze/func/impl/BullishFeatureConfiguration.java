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
public class BullishFeatureConfiguration {

	/**
	 * 1、波动率足够高的震荡行情至价格低位
	 * 2、看涨事件较多
	 * 看涨事件列表
	 * 价格处于相对低位、必须
	 * 持仓量处于相对高位、 可选
	 * 多空比处于相对高位、可选
	 */
	@Bean
	public MarketFeatureFunc lowP() {
		return (symbol, eventInfoList) -> {
			Map<String, List<ChangeEventInfo>> dataTypeToListMap =
					eventInfoList.stream().collect(Collectors.groupingBy(ChangeEventInfo::getDataType));
			// 价格事件列表
			List<ChangeEventInfo> eventKlineList = dataTypeToListMap.get(DataTypeConstant.kline);
			if (CollectionUtil.isEmpty(eventKlineList)) {
				return CallResult.failure("不存在价格事件");
			}
			List<String> lowPEventList = eventKlineList.stream().filter(ele -> StringUtils.equals(ele.getLocation(),
					EventConstant.locationArr[1])).map(ChangeEventInfo::getPeriod).collect(Collectors.toList());
			if (CollectionUtil.isEmpty(lowPEventList)) {
				return CallResult.failure("不存在低价事件");
			}
			// TODO: 2022/12/22  震荡法分析价格低位、如果波动率足够高、直接返回看涨

			// 看涨指数、越高越好
			int rlt = 0;
			rlt += sumVeVal(lowPEventList);

			// 持仓量事件列表
			List<ChangeEventInfo> eventOiList = dataTypeToListMap.get(DataTypeConstant.oi);
			if (CollectionUtil.isNotEmpty(eventOiList)) {
				List<String> highOiEventList = eventOiList.stream().filter(ele -> StringUtils.equals(ele.getLocation(),
						EventConstant.locationArr[0])).map(ChangeEventInfo::getPeriod).collect(Collectors.toList());
				if (CollectionUtil.isNotEmpty(highOiEventList)) {
					rlt += sumVeVal(highOiEventList);
				}
			}
			// 大户持仓量多空比事件列表
			List<ChangeEventInfo> eventTopList = dataTypeToListMap.get(DataTypeConstant.topOiRatio);
			rlt += sumLoShVeVal(eventTopList);

			// 账户多空比数据列表
			List<ChangeEventInfo> eventAccList = dataTypeToListMap.get(DataTypeConstant.accRatio);
			rlt += sumLoShVeVal(eventAccList);

			return CallResult.success(symbol + "-价格低位符合、看涨指数:" + rlt);
		};
	}

	private static int sumLoShVeVal(List<ChangeEventInfo> eventList) {
		int rlt = 0;
		if (CollectionUtil.isNotEmpty(eventList)) {
			for (ChangeEventInfo eventInfo : eventList) {
				String location = eventInfo.getLocation();
				String period = eventInfo.getPeriod();
				Integer veVal = AnalyzeConstant.vePeriodMap.get(period);
				if (Objects.isNull(veVal)) {
					log.error("该周期权重未定义:" + period);
					continue;
				}
				// 如果是高多空比、加权重、否之减去
				rlt += StringUtils.equals(location, EventConstant.locationArr[0]) ? veVal : -veVal;
			}
		}
		return rlt;
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
