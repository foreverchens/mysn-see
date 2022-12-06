package com.ychen.see.models.binance;

import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.models.binance.model.SymbolDataM;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Map<String, SymbolDataM> symbolAndDataMap;

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
            SymbolDataM symbolDataM = SymbolDataM.builder().symbol(symbol).openInterestStatList(new ArrayDeque<>(queueSize)).build();
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

    /**
     * 获取持仓量数据
     *
     * @param symbol 币对
     * @param day    最近几天
     */
    public List<OpenInterestStat> listOpenInterestStat(String symbol, int day) {
        SymbolDataM symbolDataM = symbolAndDataMap.get(symbol);
        ArrayDeque<OpenInterestStat> dataQueue = symbolDataM.getOpenInterestStatList();
        List<OpenInterestStat> rlt = new ArrayList<>(day * 24 * 12);
        Iterator<OpenInterestStat> iterator = dataQueue.iterator();
        long startTime = DateUtil.offsetDay(new Date(), day).getTime();
        while (iterator.hasNext()) {
            OpenInterestStat next = iterator.next();
            if (next.getTimestamp() < startTime) {
                continue;
            }
            rlt.add(next);
            while (iterator.hasNext()) {
                rlt.add(iterator.next());
            }
        }
        return rlt;
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
                    SymbolDataM symbolDataM = symbolAndDataMap.get(symbol);
                    ArrayDeque<OpenInterestStat> dataQueue = symbolDataM.getOpenInterestStatList();
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

    public static void main(String[] args) {
        int size = 5;
        ArrayDeque<Integer> queue = new ArrayDeque<>(size);
        List<Integer> c = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (Integer integer : c) {
            if (queue.size() == size) {
                queue.removeFirst();
            }
            queue.addLast(integer);
        }
        log.info("元素列表:{}", queue);
        log.info("末尾元素:{}", queue.peekLast());

        Iterator<Integer> iterator = queue.iterator();
        List<Integer> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if (next < 7) {
                continue;
            }
            list.add(next);
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        }
        log.info("大于7的元素列表:{}", list);
    }
}
