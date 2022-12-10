package com.ychen.see.models.statistic.func.impl;/**
 *
 */

import com.binance.client.model.market.CommonLongShortRatio;
import com.ychen.see.common.CallResult;
import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.statistic.domain.ContractStatisticDataM;
import com.ychen.see.models.statistic.domain.SymbolKlineStatisticM;
import com.ychen.see.models.statistic.func.ContractDataStatisticFunc;

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
@Component(ContractDataStatisticFunc.beanPrefix+ DataTypeConstant.kline)
public class ContractKlineStatisticFuncImpl implements ContractDataStatisticFunc {
	@Override
	public CallResult<String> doStatistic(String dataType, ContractStatisticDataM statisticDataM,
										  ContractOriginalDataDomain contractOriginalDataDomain) {
		SymbolKlineStatisticM klineStatisticM = (SymbolKlineStatisticM) statisticDataM.get(dataType);
		String symbol = klineStatisticM.getSymbol();

		log.info("分析{}的{}类型数据", symbol, dataType);
		// 最近七天的多空比数据
		List<BigDecimal> day7LongShortRatioDataList =
				contractOriginalDataDomain.<CommonLongShortRatio>listLastContractData(symbol, dataType, 7).getData().stream().map(CommonLongShortRatio::getLongShortRatio).collect(Collectors.toList());

		// todo 震荡分析法获取 七天和三天内的数值。。。
		return CallResult.success(day7LongShortRatioDataList.toString());
	}
}