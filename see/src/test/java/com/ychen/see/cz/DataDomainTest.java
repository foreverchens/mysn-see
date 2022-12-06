package com.ychen.see.cz;

import com.binance.client.model.market.CommonLongShortRatio;
import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.models.binance.DataDomain;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.binance.model.ContractDataM;
import com.ychen.see.models.binance.util.CzUtil;

import cn.hutool.core.date.DateTime;

import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
@Slf4j
public class DataDomainTest {

    private int storeDayVal = 7;

    private DataDomain dataDomain = new DataDomain();

    private Map<String, ContractDataM> symbolAndDataMap;

    @Before
    public void initTest() throws Exception {
        Field storeDay = DataDomain.class.getDeclaredField("storeDay");
        storeDay.setAccessible(true);
        storeDay.set(dataDomain, storeDayVal);
        Method init = DataDomain.class.getDeclaredMethod("init");
        init.setAccessible(true);
        init.invoke(dataDomain);
        Field symbolAndDataMapField = DataDomain.class.getDeclaredField("symbolAndDataMap");
        symbolAndDataMapField.setAccessible(true);
        symbolAndDataMap = (Map) symbolAndDataMapField.get(dataDomain);
        log.info("近七日的数据初始化结束");
        for (String symbol : symbolAndDataMap.keySet()) {
            ContractDataM dataM = symbolAndDataMap.get(symbol);
            ArrayDeque<OpenInterestStat> dataList = dataM.get(DataTypeConstant.openInterest);
            OpenInterestStat first = dataList.peekFirst();
            OpenInterestStat last = dataList.peekLast();
            log.info("{} 初始化数据量:{},数据范围:{} - {}", symbol, dataList.size(), DateTime.of(first.getTimestamp()), DateTime.of(last.getTimestamp()));
        }
    }

//    @Test
    public void updateTaskLongTimeTest() throws Exception {
        // 观察后台线程情况
        TimeUnit.MINUTES.sleep(15);
    }

    @Test
    public void listLastContractDataTest() {
        List<OpenInterestStat> openInterestStatList = dataDomain.<OpenInterestStat>listLastContractData("BTCUSDT", 3, DataTypeConstant.openInterest).getData();
        CzUtil.dataTimeOrdered(openInterestStatList);
        log.info("近三天持仓量数据范围:{} - {}", DateTime.of(openInterestStatList.get(0).getTimestamp()), DateTime.of(openInterestStatList.get(openInterestStatList.size() - 1).getTimestamp()));

        List<CommonLongShortRatio> commonLongShortRatioList = dataDomain.<CommonLongShortRatio>listLastContractData("BTCUSDT", 3, DataTypeConstant.accRatio).getData();
        CzUtil.dataTimeOrdered(commonLongShortRatioList);
        log.info("近三天账户多空比数据范围:{} - {}", DateTime.of(commonLongShortRatioList.get(0).getTimestamp()), DateTime.of(commonLongShortRatioList.get(commonLongShortRatioList.size() - 1).getTimestamp()));

        commonLongShortRatioList = dataDomain.<CommonLongShortRatio>listLastContractData("BTCUSDT", 3, DataTypeConstant.topPositionRatio).getData();
        CzUtil.dataTimeOrdered(commonLongShortRatioList);
        log.info("近三天大户持仓量多空比数据范围:{} - {}", DateTime.of(commonLongShortRatioList.get(0).getTimestamp()), DateTime.of(commonLongShortRatioList.get(commonLongShortRatioList.size() - 1).getTimestamp()));

    }
}
