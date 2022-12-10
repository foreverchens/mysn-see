package com.ychen.see.statistic;

import com.ychen.see.cz.ContractOriginalDataDomainTest;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.statistic.StatisticDataDomain;
import com.ychen.see.models.statistic.domain.SymbolBaseStatisticM;
import com.ychen.see.models.statistic.func.ContractDataStatisticFunc;
import com.ychen.see.models.statistic.func.impl.ContractOpenPosDataStatisticFuncImpl;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yyy
 */
public class StatisticDataDomainTest {

	public ContractOriginalDataDomainTest contractOriginalDataDomainTest = new ContractOriginalDataDomainTest();

	public StatisticDataDomain statisticDataDomain;

	@Before
	public void before() throws Exception {
		contractOriginalDataDomainTest.initTest();
		statisticDataDomain = new StatisticDataDomain(contractOriginalDataDomainTest.contractOriginalDataDomain);

		Field statisticFuncMapField = StatisticDataDomain.class.getDeclaredField("statisticFuncMap");
		statisticFuncMapField.setAccessible(true);
		Map<String, ContractDataStatisticFunc> statisticFuncMap = new HashMap<>();
		statisticFuncMap.put(DataTypeConstant.openInterest, new ContractOpenPosDataStatisticFuncImpl());
		statisticFuncMapField.set(statisticDataDomain, statisticFuncMap);

	}

	@Test
	public void test() {
		statisticDataDomain.statistic("AXSUSDT",DataTypeConstant.openInterest);
		SymbolBaseStatisticM statisticM = statisticDataDomain.getStatisticInfo("AXSUSDT",
				DataTypeConstant.openInterest);
		System.out.println(statisticM);
	}
}
