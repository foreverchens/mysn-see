package com.ychen.see.models.binance;

import com.binance.client.model.market.Candlestick;
import com.binance.client.model.market.CommonLongShortRatio;
import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.binance.model.ContractDataM;
import com.ychen.see.models.binance.model.ContractDataTo;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private int queueSize;

    /**
     * symbol -> symbolDataM
     */
    private Map<String, ContractDataM> symbolAndDataMap;

    private Thread updateThread;

    @PostConstruct
    private void init() {
        // 初始化容器和客户端
        queueSize = storeDay * 24 * 12;
        List<String> symbolList = CzClient.listSymbol();
        log.info("[init] symbolList.size = {}", symbolList.size());

        // todo 测试用
        symbolList = Arrays.asList("AXSUSDT", "BTCUSDT");

        symbolAndDataMap = new HashMap<>(symbolList.size());
        for (String symbol : symbolList) {
            ContractDataM contractDataM = new ContractDataM(symbol);
            symbolAndDataMap.put(symbol, contractDataM);
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
            ContractDataM contractDataM = symbolAndDataMap.get(symbol);
            // 初始化持仓量数据。。。
            List<OpenInterestStat> openInterestStatList = CzClient.listOpenInterest(symbol, startTime, null);
            contractDataM.fill(DataTypeConstant.openInterest, openInterestStatList);
            // 初始化大户持仓量多空比数据
            List<CommonLongShortRatio> topPositionRatioList = CzClient.listTopPositionRatio(symbol, startTime, null);
            contractDataM.fill(DataTypeConstant.topPositionRatio, topPositionRatioList);
            // 初始化k线数据。。。
            List<Candlestick> klineList = CzClient.listKline(symbol, startTime, null);
            contractDataM.fill(DataTypeConstant.kline, klineList);
            // 初始化账户多空比数据
            List<CommonLongShortRatio> accRatioList = CzClient.listAccRatio(symbol, startTime, null);
            contractDataM.fill(DataTypeConstant.accRatio, accRatioList);
        }
    }

    /**
     * 获取最近n天数据
     * 持仓量、多空比数据都是5分钟一条、使用该方法
     */
    public <T> ContractDataTo<T> listLastContractData(String symbol, int day, String dataType) {
        int rltTotal = day * 24 * 12;
        return listLastContractData(symbol,dataType, rltTotal);
    }

    /**
     * 获取最近n天kline数据
     */
    public <T> ContractDataTo<T> listLstContractKlineData(String symbol, int day) {
        int rltTotal = day * 24 * 60;
        return listLastContractData(symbol, DataTypeConstant.kline, rltTotal);
    }

    private <T> ContractDataTo<T> listLastContractData(String symbol, String type, int rltTotal) {
        ContractDataM contractDataM = symbolAndDataMap.get(symbol);
        ArrayDeque<T> dataQueue = contractDataM.<T>get(type);
        List<T> rlt = new ArrayList<>(rltTotal);
        Iterator<T> iterator = dataQueue.descendingIterator();
        while (iterator.hasNext()) {
            rlt.add(iterator.next());
            if (rlt.size() == rltTotal) {
                break;
            }
        }
        Collections.reverse(rlt);
        return new ContractDataTo<>(rlt);
    }

    private final class UpdateTask implements Runnable {

        @Override
        public void run() {
            Thread.currentThread().setName("see.cz.dataDomain.UpdateTaskThread");
            log.info("[init] start suc .....");
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                }
                log.info("update start ......");
                for (String symbol : symbolAndDataMap.keySet()) {
                    ContractDataM contractDataM = symbolAndDataMap.get(symbol);
                    ArrayDeque<OpenInterestStat> dataQueue = contractDataM.get(DataTypeConstant.openInterest);
                    OpenInterestStat first = dataQueue.peekLast();
                    List<OpenInterestStat> openInterestStatList = CzClient.listOpenInterest(symbol, first.getTimestamp(), null);
                    log.info("{} 开始更新持仓量数据,新到数据{}条", symbol, openInterestStatList.size());
                    log.info("{} 更新前数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getTimestamp()), DateTime.of(dataQueue.peekLast().getTimestamp()));
                    for (OpenInterestStat data : openInterestStatList) {
                        if (dataQueue.size() == queueSize) {
                            dataQueue.removeFirst();
                        }
                        dataQueue.addLast(data);
                    }
                    log.info("{} 更新后数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getTimestamp()), DateTime.of(dataQueue.peekLast().getTimestamp()));
                }
                log.info("update end ......");
            }
        }
    }
}
