package com.ychen.see.models.statistic.func;


import com.ychen.see.common.CallResult;
import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.statistic.domain.ContractStatisticDataM;
import com.ychen.see.models.statistic.domain.SymbolBaseStatisticM;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author yyy
 * {@code @wx} ychen5325
 * {@code @email} q1416349095@gmail.com
 * {@code @date} 2022/12/9 17:35
 */
@FunctionalInterface
public interface ContractDataStatisticFunc {

	String beanPrefix = "statistic-";

	/**
	 * 统计一个类型的数据、如持仓量数据
	 */
	CallResult<String> doStatistic(String dataType, ContractStatisticDataM statisticDataM,
								   ContractOriginalDataDomain contractOriginalDataDomain);

	default void statisticDay3AndDay7(SymbolBaseStatisticM statisticM, List<BigDecimal> day7DataList) {
		// 最近三天的多空比数据
		int day4Index = day7DataList.size() * 4 / 7;
		List<BigDecimal> day3DataList = day7DataList.subList(day4Index,
				day7DataList.size());

		BigDecimal day7HighV = BigDecimal.ZERO;
		BigDecimal day7LowV = BigDecimal.valueOf(Long.MAX_VALUE);
		BigDecimal day7SumV = BigDecimal.ZERO;
		for (BigDecimal val : day7DataList) {
			day7HighV = day7HighV.compareTo(val) > 0 ? day7HighV : val;
			day7LowV = day7LowV.compareTo(val) > 0 ? val : day7LowV;
			day7SumV = day7SumV.add(val);
		}
		statisticM.setDay7AvgV(day7SumV.divide(BigDecimal.valueOf(day7DataList.size()),
				RoundingMode.DOWN));
		statisticM.setDay7HighV(day7HighV);
		statisticM.setDay7LowV(day7LowV);
		statisticM.setDay7Amplitude((day7HighV.subtract(day7LowV).divide(day7LowV, RoundingMode.DOWN)));

		// 统计三天内数据
		BigDecimal day3HighV = BigDecimal.ZERO;
		BigDecimal day3LowV = BigDecimal.valueOf(Long.MAX_VALUE);
		BigDecimal day3SumV = BigDecimal.ZERO;
		for (BigDecimal val : day3DataList) {
			day3HighV = day3HighV.compareTo(val) > 0 ? day3HighV : val;
			day3LowV = day3LowV.compareTo(val) > 0 ? val : day3LowV;
			day3SumV = day3SumV.add(val);
		}
		statisticM.setDay3AvgV(day3SumV.divide(BigDecimal.valueOf(day3DataList.size()),
				RoundingMode.DOWN));
		statisticM.setDay3HighV(day3HighV);
		statisticM.setDay3LowV(day3LowV);
		statisticM.setDay3Amplitude((day3HighV.subtract(day3LowV).divide(day3LowV, RoundingMode.DOWN)));
	}
}

