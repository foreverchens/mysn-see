package com.ychen.see.models.schedule.server;

import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.statistic.StatisticDataDomain;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author yyy
 */
@Slf4j
@Component
public class ContractDataStatisticAndAnalyzeService {

	@Resource
	private ContractOriginalDataDomain originalDataDomain;
	@Resource
	private StatisticDataDomain statisticDataDomain;


	public void exe() {
		List<String> symbolList = originalDataDomain.getSymbolList();
		// 先更新数据源
		for (String dataType : DataTypeConstant.typeList) {
			for (String symbol : symbolList) {
				originalDataDomain.updateContractDataSource(symbol, dataType);
			}
			// 在更新一阶数据
			statisticDataDomain.statistic(dataType);
		}
	}
}
