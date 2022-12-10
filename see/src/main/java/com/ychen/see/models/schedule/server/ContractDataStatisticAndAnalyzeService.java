package com.ychen.see.models.schedule.server;

import com.ychen.see.common.config.SwitchConfig;
import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.event.EventDataDomain;
import com.ychen.see.models.event.domain.ChangeEventInfo;
import com.ychen.see.models.statistic.StatisticDataDomain;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.List;
import java.util.TreeMap;

/**
 * @author yyy
 */
@Slf4j
@Component
public class ContractDataStatisticAndAnalyzeService {

	@Resource
	private SwitchConfig switchConfig;
	@Resource
	private ContractOriginalDataDomain originalDataDomain;
	@Resource
	private StatisticDataDomain statisticDataDomain;
	@Resource
	private EventDataDomain eventDataDomain;


	public void exe() {
		List<String> symbolList = originalDataDomain.getSymbolList();
		for (String dataType : DataTypeConstant.typeList) {
			if (!DataTypeConstant.openInterest.equals(dataType)) {
				continue;
			}
			for (String symbol : symbolList) {
				// 先更新数据源
				originalDataDomain.updateContractDataSource(symbol, dataType);
				// 在更新一阶数据
				statisticDataDomain.statistic(symbol, dataType);
				// 更新二阶事件数据
				eventDataDomain.changeEvent(symbol, dataType);
			}
		}
		TreeMap<Integer, String> map = new TreeMap<>((a, b) -> b - a);
		for (String symbol : symbolList) {
			List<ChangeEventInfo> eventInfoList = eventDataDomain.listEventInfo(symbol);
			map.put(eventInfoList.size(), symbol);
		}
		System.out.println(map.firstEntry());
		System.out.println(map.lastEntry());
	}
}
