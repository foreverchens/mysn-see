package com.ychen.see.cz;

import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.models.binance.DataDomain;
import com.ychen.see.models.binance.model.SymbolDataM;

import cn.hutool.core.date.DateTime;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yyy
 * @wx ychen5325
 * @email yangyouyuhd@163.com
 */
@Slf4j
public class DataDomainTest {

    private DataDomain dataDomain = new DataDomain();

    private Map<String, SymbolDataM> symbolAndDataMap;

    @Test
    public void initTest() throws Exception {
        Field storeDay = DataDomain.class.getDeclaredField("storeDay");
        storeDay.setAccessible(true);
        storeDay.set(dataDomain, 3);
        Method init = DataDomain.class.getDeclaredMethod("init");
        init.setAccessible(true);
        init.invoke(dataDomain);
        Field symbolAndDataMapField = DataDomain.class.getDeclaredField("symbolAndDataMap");
        symbolAndDataMapField.setAccessible(true);
        symbolAndDataMap = (Map) symbolAndDataMapField.get(dataDomain);
        log.info("近三日的数据初始化结束");
        for (String symbol : symbolAndDataMap.keySet()) {
            SymbolDataM dataM = symbolAndDataMap.get(symbol);
            ArrayDeque<OpenInterestStat> dataList = dataM.getOpenInterestStatList();
            OpenInterestStat first = dataList.peekFirst();
            OpenInterestStat last = dataList.peekLast();
            log.info("{} 初始化数据量:{},数据范围:{} - {}", symbol, dataList.size(), DateTime.of(first.getTimestamp()), DateTime.of(last.getTimestamp()));
        }
    }

//    @Test
    public void updateTaskLongTimeTest() throws Exception {
        this.initTest();
        // 观察后台线程情况
        TimeUnit.MINUTES.sleep(15);
    }

}
