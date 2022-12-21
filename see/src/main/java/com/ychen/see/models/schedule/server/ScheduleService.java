package com.ychen.see.models.schedule.server;

import com.ychen.see.common.config.SwitchConfig;
import com.ychen.see.common.util.CommonUtil;
import com.ychen.see.models.analyze.AnalyzeService;
import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.event.EventDataDomain;
import com.ychen.see.models.event.domain.ChangeEventInfo;
import com.ychen.see.models.statistic.StatisticDataDomain;

import cn.hutool.core.collection.CollectionUtil;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author yyy
 */
@Slf4j
@Component
public class ScheduleService {

	@Resource
	private SwitchConfig switchConfig;
	@Resource
	private ContractOriginalDataDomain originalDataDomain;
	@Resource
	private StatisticDataDomain statisticDataDomain;
	@Resource
	private EventDataDomain eventDataDomain;
	@Resource
	private AnalyzeService analyzeService;


	public void exe() {
		List<String> symbolList = originalDataDomain.getSymbolList();
		if (switchConfig.getOi()) {
			exe(symbolList, DataTypeConstant.oi);
		}
		if (switchConfig.getKline()) {
			exe(symbolList, DataTypeConstant.kline);
		}
		if (switchConfig.getTopOiRatio()) {
			exe(symbolList, DataTypeConstant.topOiRatio);
		}
		if (switchConfig.getAccRatio()) {
			exe(symbolList, DataTypeConstant.accRatio);
		}

		for (String symbol : symbolList) {
			List<String> analyzeRlt = analyzeService.analyze(symbol);
			if (CollectionUtil.isNotEmpty(analyzeRlt)) {
				log.info("rlt:" + analyzeRlt);
			}
			List<ChangeEventInfo> eventInfoList = eventDataDomain.listEventInfo(symbol);
			if (!CollectionUtil.isEmpty(eventInfoList)) {
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("币对%s存在%s个事件\n", symbol, eventInfoList.size()));
				for (ChangeEventInfo data : eventInfoList) {
					sb.append(String.format("%s 在 %S内的数值范围:[%s,%s],当前持仓量%s处于%s \n", data.getDataType(), data.getPeriod()
							, CommonUtil.numConvert(data.getLowV()), CommonUtil.numConvert(data.getHighV()),
							CommonUtil.numConvert(data.getCurV()), data.getLocation()));
				}
				System.out.println(sb);
			}
		}
	}

	private void exe(List<String> symbolList, String dataType) {
		for (String symbol : symbolList) {
			// 先更新数据源
			originalDataDomain.updateContractDataSource(symbol, dataType);
			// 在更新一阶数据
			statisticDataDomain.statistic(symbol, dataType);
			// 更新二阶事件数据
			eventDataDomain.changeEvent(symbol, dataType);
		}
	}
}
