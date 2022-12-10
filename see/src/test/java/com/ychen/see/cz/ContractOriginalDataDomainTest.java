package com.ychen.see.cz;

import com.binance.client.model.market.CommonLongShortRatio;
import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.models.binance.ContractOriginalDataDomain;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.binance.util.CzUtil;

import cn.hutool.core.date.DateTime;

import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.List;

/**
 * @author yyy
 */
@Slf4j
public class ContractOriginalDataDomainTest {

    public int storeDayVal = 7;

    public ContractOriginalDataDomain contractOriginalDataDomain = new ContractOriginalDataDomain();


    @Before
    public void initTest() throws Exception {
        Field storeDay = ContractOriginalDataDomain.class.getDeclaredField("storeDay");
        storeDay.setAccessible(true);
        storeDay.set(contractOriginalDataDomain, storeDayVal);
        Method init = ContractOriginalDataDomain.class.getDeclaredMethod("init");
        init.setAccessible(true);
        init.invoke(contractOriginalDataDomain);
        Field symbolListField = ContractOriginalDataDomain.class.getDeclaredField("symbolList");
        symbolListField.setAccessible(true);
        List<String> symbolList = (List) symbolListField.get(contractOriginalDataDomain);
        log.info("近七日的数据初始化结束");
        for (String symbol : symbolList) {
            ArrayDeque<OpenInterestStat> dataList =
                    contractOriginalDataDomain.getSymbolAndOpenPositionMap().get(symbol);
            OpenInterestStat first = dataList.peekFirst();
            OpenInterestStat last = dataList.peekLast();
            log.info("{} 初始化数据量:{},数据范围:{} - {}", symbol, dataList.size(), DateTime.of(first.getTimestamp()),
                    DateTime.of(last.getTimestamp()));
        }
    }


    @Test
    public void listLastContractDataTest() {
        List<OpenInterestStat> openInterestStatList =
                contractOriginalDataDomain.<OpenInterestStat>listLastContractData("BTCUSDT",
                        DataTypeConstant.openInterest, 3).getData();
        CzUtil.dataTimeOrdered(openInterestStatList);
        log.info("近三天持仓量数据范围:{} - {}", DateTime.of(openInterestStatList.get(0).getTimestamp()),
                DateTime.of(openInterestStatList.get(openInterestStatList.size() - 1).getTimestamp()));

        List<CommonLongShortRatio> commonLongShortRatioList =
                contractOriginalDataDomain.<CommonLongShortRatio>listLastContractData("BTCUSDT",
                        DataTypeConstant.accRatio, 3).getData();
        CzUtil.dataTimeOrdered(commonLongShortRatioList);
        log.info("近三天账户多空比数据范围:{} - {}", DateTime.of(commonLongShortRatioList.get(0).getTimestamp()),
                DateTime.of(commonLongShortRatioList.get(commonLongShortRatioList.size() - 1).getTimestamp()));

        commonLongShortRatioList = contractOriginalDataDomain.<CommonLongShortRatio>listLastContractData("BTCUSDT",
                DataTypeConstant.topPositionRatio, 3).getData();
        CzUtil.dataTimeOrdered(commonLongShortRatioList);
        log.info("近三天大户持仓量多空比数据范围:{} - {}", DateTime.of(commonLongShortRatioList.get(0).getTimestamp()),
                DateTime.of(commonLongShortRatioList.get(commonLongShortRatioList.size() - 1).getTimestamp()));

    }


    @Test
    public void listLongTimeContractDataTest() {
        List<OpenInterestStat> openInterestStatList =
                contractOriginalDataDomain.<OpenInterestStat>listLastContractData("BTCUSDT",
                        DataTypeConstant.openInterest, 15).getData();
        CzUtil.dataTimeOrdered(openInterestStatList);
        log.info("近十五天持仓量数据范围:{} - {}", DateTime.of(openInterestStatList.get(0).getTimestamp()),
                DateTime.of(openInterestStatList.get(openInterestStatList.size() - 1).getTimestamp()));
    }
}
