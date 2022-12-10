package com.ychen.see.models.event;

import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.event.domain.ChangeEventInfo;
import com.ychen.see.models.event.func.ChangeEventFunc;
import com.ychen.see.models.event.func.impl.KlineChangeEventConfiguration;
import com.ychen.see.models.event.func.impl.LoShChangeEventConfiguration;
import com.ychen.see.models.event.func.impl.OpenPosChangeEventConfiguration;
import com.ychen.see.models.statistic.StatisticDataDomain;
import com.ychen.see.models.statistic.domain.SymbolBaseStatisticM;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollectionUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yyy
 */
@Slf4j
@Component
public class EventDataDomain {

	private final String eventKeyPrefix = "event-";
	@Value("${see.event.timeout:24}")
	private long eventTimeoutHours;

	private Map<String, List<ChangeEventFunc>> dataTypeAndEventListMap;

	private Map<String, TimedCache<String, ChangeEventInfo>> symbolAndDynamicEventListMap;

	@Resource
	private StatisticDataDomain statisticDataDomain;
	@Resource
	private ContractOriginalDataDomain originalDataDomain;

	public EventDataDomain() {
		dataTypeAndEventListMap = new HashMap<>();
		dataTypeAndEventListMap.put(DataTypeConstant.openInterest, OpenPosChangeEventConfiguration.listOpenPosEvent());
		dataTypeAndEventListMap.put(DataTypeConstant.kline, KlineChangeEventConfiguration.listKlineEvent());
		dataTypeAndEventListMap.put(DataTypeConstant.topPositionRatio, LoShChangeEventConfiguration.listLoShEvent());
		dataTypeAndEventListMap.put(DataTypeConstant.accRatio, LoShChangeEventConfiguration.listLoShEvent());

		symbolAndDynamicEventListMap = new HashMap<>();
	}

	public void changeEvent(String symbol, String dataType) {
		// 根据币对和数据类型获取一阶统计数据
		SymbolBaseStatisticM statisticM = statisticDataDomain.getStatisticInfo(symbol, dataType);
		// 获取该数据类型定义的数据异动事件列表
		List<ChangeEventFunc> eventFuncList = dataTypeAndEventListMap.get(dataType);

		// 碰撞收集新产生的事件列表
		List<ChangeEventInfo> newEventInfoList = new ArrayList<>();
		for (ChangeEventFunc func : eventFuncList) {
			ChangeEventInfo eventInfo = func.changeEvent(originalDataDomain.getCurVal(symbol,dataType), statisticM);
			if (!Objects.isNull(eventInfo)) {
				newEventInfoList.add(eventInfo);
			}
		}
		if (CollectionUtil.isEmpty(newEventInfoList)) {
			return;
		}
		// 更新事件列表
		TimedCache<String, ChangeEventInfo> dynamicEventList = symbolAndDynamicEventListMap.get(symbol);
		if (Objects.isNull(dynamicEventList)) {
			// 24h超时
			dynamicEventList = CacheUtil.newTimedCache(eventTimeoutHours * 60 * 60 * 1000);
			symbolAndDynamicEventListMap.put(symbol, dynamicEventList);
		}
		for (ChangeEventInfo eventInfo : newEventInfoList) {
			String key =
					new StringBuilder(eventKeyPrefix).append(eventInfo.getSymbol()).append("-").append(eventInfo.getPeriod()).append("-").append(eventInfo.getDataType()).append("-").append(eventInfo.getLocation()).toString();
			dynamicEventList.put(key, eventInfo);
		}
	}

	public List<ChangeEventInfo> listEventInfo(String symbol) {
		TimedCache<String, ChangeEventInfo> eventInfos = symbolAndDynamicEventListMap.get(symbol);
		List<ChangeEventInfo> rlt = new ArrayList<>();
		if (Objects.isNull(eventInfos)) {
			return rlt;
		}
		Iterator<ChangeEventInfo> iterator = eventInfos.iterator();
		while (iterator.hasNext()) {
			rlt.add(iterator.next());
		}
		return rlt;
	}

}
