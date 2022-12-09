package com.ychen.see.models.statistic;

import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.statistic.domain.ContractStatisticDataM;
import com.ychen.see.models.statistic.domain.SymbolBaseStatisticM;
import com.ychen.see.models.statistic.func.ContractDataStatisticFunc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yyy
 * {@code @wx} ychen5325
 * {@code @email} q1416349095@gmail.com
 * {@code @date} 2022/12/9 17:35
 */
@Slf4j
@Component
public class StatisticDataDomain {

	private final List<String> symbolList;
	private final Map<String, ContractStatisticDataM> symbolAndStatisticMap;

	private final ContractOriginalDataDomain contractOriginalDataDomain;

	@Resource
	private Map<String, ContractDataStatisticFunc> statisticFuncMap;

	public StatisticDataDomain(@Autowired ContractOriginalDataDomain contractOriginalDataDomain) {
		this.symbolList = contractOriginalDataDomain.getSymbolList();
		this.symbolAndStatisticMap = new HashMap<>(symbolList.size());
		this.contractOriginalDataDomain = contractOriginalDataDomain;
		for (String symbol : symbolList) {
			symbolAndStatisticMap.put(symbol, new ContractStatisticDataM(symbol));
		}
	}

	/**
	 * 针对一个类型的所有币对的数据进行分析
	 */
	public void statistic(String dataType) {
		for (String symbol : symbolList) {
			ContractStatisticDataM statisticDataM = symbolAndStatisticMap.get(symbol);
			statisticFuncMap.get(dataType).doStatistic(dataType, statisticDataM, contractOriginalDataDomain);
		}
	}

	public SymbolBaseStatisticM getStatisticInfo(String symbol, String dataType) {
		return symbolAndStatisticMap.get(symbol).get(dataType);
	}
}
