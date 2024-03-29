package com.ychen.see.models.statistic.func.impl;/**
 *
 */

import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.common.CallResult;
import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.statistic.domain.ContractStatisticDataM;
import com.ychen.see.models.statistic.domain.SymbolOpenPositionStatisticM;
import com.ychen.see.models.statistic.func.ContractDataStatisticFunc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yyy
 * {@code @wx} ychen5325
 * {@code @email} q1416349095@gmail.com
 * {@code @date} 2022/12/9 17:35
 */
@Slf4j
@Component(ContractDataStatisticFunc.beanPrefix+ DataTypeConstant.oi)
public class ContractOiDataStatisticFuncImpl implements ContractDataStatisticFunc {

	@Value("${see.cz.storeDay:15}")
	private int storeDay;


	@Override
	public CallResult<String> doStatistic(String dataType, ContractStatisticDataM statisticDataM,
										  ContractOriginalDataDomain contractOriginalDataDomain) {
		SymbolOpenPositionStatisticM oiStatisticM = (SymbolOpenPositionStatisticM) statisticDataM.get(dataType);
		String symbol = statisticDataM.getSymbol();

		log.info("分析{}的{}类型数据", symbol, dataType);
		// 最近七天的持仓量数据
		List<BigDecimal> day15OiValDataList =
				contractOriginalDataDomain.<OpenInterestStat>listLastContractData(symbol, dataType, storeDay).getData().stream().map(OpenInterestStat::getSumOpenInterest).collect(Collectors.toList());

		// 统计三天和七天内数据
		statisticDay3To15(oiStatisticM, day15OiValDataList);

		// todo 15日30日的数据待定。。。
		return CallResult.success();
	}
}
