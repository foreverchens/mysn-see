package com.ychen.see.models.binance;

import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.models.binance.model.SymbolDataM;

import cn.hutool.core.date.DateUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存储原始数据、并保持其最新
 *
 * @author yyy
 * @wx ychen5325
 * @email yangyouyuhd@163.com
 */
@Slf4j
@Component
public class DataDomain {

    /**
     * 最多存储几天的数据
     */
    @Value("${see.cz.storeDay:7}")
    private int storeDay;

    /**
     * symbol -> symbolDataM
     */
    private Map<String, SymbolDataM> symbolAndDataMap;

    private Thread updateThread;

    @PostConstruct
    public void init() {
        // 初始化容器和客户端
        int size = storeDay * 24 * 12;
        List<String> symbolList = CzClient.listSymbol();
        log.info("[init] symbolList.size = {}", symbolList.size());
        symbolList = Arrays.asList("AXSUSDT", "BTCUSDT");
        symbolAndDataMap = new HashMap<>(symbolList.size());
        for (String symbol : symbolList) {
            SymbolDataM symbolDataM = SymbolDataM.builder().symbol(symbol).openInterestStatList(new ArrayList<>(size)).build();
            symbolAndDataMap.put(symbol, symbolDataM);
        }

        // 初始数据填充
        this.initOpenInterestStatList();

        // 启动数据更新线程
        updateThread = new Thread(new UpdateTask());
        updateThread.start();
    }

    private void initOpenInterestStatList() {
        long startTime = DateUtil.offsetDay(new Date(), storeDay * -1).getTime();
        for (String symbol : symbolAndDataMap.keySet()) {
            SymbolDataM symbolDataM = symbolAndDataMap.get(symbol);
            List<OpenInterestStat> openInterestStatList = CzClient.listOpenInterest(symbol, startTime, null);
            symbolDataM.getOpenInterestStatList().addAll(openInterestStatList);
        }
    }

    private static class UpdateTask implements Runnable {

        @Override
        public void run() {

        }
    }

    public static void main(String[] args) {
        DataDomain data = new DataDomain();
        data.storeDay = 3;
        data.init();
        SymbolDataM axsusdt = data.symbolAndDataMap.get("AXSUSDT");
        List<BigDecimal> collect = axsusdt.getOpenInterestStatList().stream().map(OpenInterestStat::getSumOpenInterestValue).collect(Collectors.toList());
        System.out.println("size=" + collect.size());
    }
}
