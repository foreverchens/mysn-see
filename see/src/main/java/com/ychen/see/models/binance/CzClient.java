package com.ychen.see.models.binance;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.impl.BinanceApiInternalFactory;
import com.binance.client.model.enums.CandlestickInterval;
import com.binance.client.model.enums.PeriodType;
import com.binance.client.model.market.Candlestick;
import com.binance.client.model.market.CommonLongShortRatio;
import com.binance.client.model.market.ExchangeInfoEntry;
import com.binance.client.model.market.OpenInterestStat;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 把几个常用的api归纳下、统一时间周期为5m
 * 1. 获取开放交易的合约币对列表 getExchangeInformation
 * 2. 获取多空持仓人数比 getGlobalAccountRatio
 * 3. 获取合约持仓量 getOpenInterestStat
 * 4. 获取大户持仓量多空比 getTopTraderPositionRatio
 * 5. 获取价格数据 getCandlestick
 *
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
public class CzClient {

    private static int maxLimit = 500;
    private static int unit = 1000 * 60 * 5;

    private static final SyncRequestClient czClient = BinanceApiInternalFactory.getInstance().createSyncRequestClient("_", "_", new RequestOptions());

    /**
     * 获取可供交易的合约币对列表
     */
    public static List<String> listSymbol() {
        return czClient.getExchangeInformation().getSymbols().stream().filter(ele -> StringUtils.equals("TRADING", ele.getStatus()) && !StringUtils.contains(ele.getSymbol(), "_")).map(ExchangeInfoEntry::getSymbol).collect(Collectors.toList());
    }

    /**
     * 获取持仓量列表
     * 若无 startime 和 endtime 限制， 则默认返回当前时间往前的limit值
     * 仅支持最近30天的数据
     */
    public static List<OpenInterestStat> listOpenInterest(String symbol, Long startTime, Long endTime) {
        if (Objects.isNull(endTime)) {
            endTime = DateTime.now().getTime();
        }
        if (Objects.isNull(startTime)) {
            // 不限制时间、一次查询
            return czClient.getOpenInterestStat(symbol, PeriodType._5m, null, endTime, maxLimit);
        }
        int dataCount = (int) ((endTime - startTime) / unit);
        if (dataCount < maxLimit) {
            // 时间范围内数据条数小于maxLimit 一次查询即可
            return czClient.getOpenInterestStat(symbol, PeriodType._5m, startTime, endTime, maxLimit);
        }

        List<OpenInterestStat> rlt = new ArrayList<>(dataCount);
        long tmpEndTime = startTime + maxLimit * unit;
        while (tmpEndTime <= endTime) {
            rlt.addAll(czClient.getOpenInterestStat(symbol, PeriodType._5m, startTime, tmpEndTime, maxLimit));
            startTime = tmpEndTime;
            tmpEndTime = tmpEndTime + maxLimit * unit;
        }
        rlt.addAll(czClient.getOpenInterestStat(symbol, PeriodType._5m, startTime, endTime, maxLimit));
        return rlt;
    }

    /**
     * 获取账户多空比数据
     * 若无 startime 和 endtime 限制， 则默认返回当前时间往前的limit值
     * 仅支持最近30天的数据
     *
     * @return
     */
    public static List<CommonLongShortRatio> listAccRatio(String symbol, Long startTime, Long endTime) {
        if (Objects.isNull(endTime)) {
            endTime = DateTime.now().getTime();
        }
        if (Objects.isNull(startTime)) {
            // 不限制时间、一次查询
            return czClient.getGlobalAccountRatio(symbol, PeriodType._5m, null, endTime, maxLimit);
        }
        int dataCount = (int) ((endTime - startTime) / unit);
        if (dataCount < maxLimit) {
            // 时间范围内数据条数小于maxLimit 一次查询即可
            return czClient.getGlobalAccountRatio(symbol, PeriodType._5m, startTime, endTime, maxLimit);
        }

        List<CommonLongShortRatio> rlt = new ArrayList<>(dataCount);
        long tmpEndTime = startTime + maxLimit * unit;
        while (tmpEndTime <= endTime) {
            rlt.addAll(czClient.getGlobalAccountRatio(symbol, PeriodType._5m, startTime, tmpEndTime, maxLimit));
            startTime = tmpEndTime;
            tmpEndTime = tmpEndTime + maxLimit * unit;
        }
        rlt.addAll(czClient.getGlobalAccountRatio(symbol, PeriodType._5m, startTime, endTime, maxLimit));
        return rlt;
    }


    /**
     * 获取大户持仓量多空比
     * 若无 startime 和 endtime 限制， 则默认返回当前时间往前的limit值
     * 仅支持最近30天的数据
     * <p>
     * default 30, max 500
     */
    public static List<CommonLongShortRatio> listTopPositionRatio(String symbol, Long startTime, Long endTime) {
        if (Objects.isNull(endTime)) {
            endTime = DateTime.now().getTime();
        }
        if (Objects.isNull(startTime)) {
            // 不限制时间、一次查询
            return czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, null, endTime, maxLimit);
        }
        int dataCount = (int) ((endTime - startTime) / unit);
        if (dataCount < maxLimit) {
            // 时间范围内数据条数小于maxLimit 一次查询即可
            return czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, startTime, endTime, maxLimit);
        }

        List<CommonLongShortRatio> rlt = new ArrayList<>(dataCount);
        long tmpEndTime = startTime + maxLimit * unit;
        while (tmpEndTime <= endTime) {
            rlt.addAll(czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, startTime, tmpEndTime, maxLimit));
            startTime = tmpEndTime;
            tmpEndTime = tmpEndTime + maxLimit * unit;
        }
        rlt.addAll(czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, startTime, endTime, maxLimit));
        return rlt;
    }

    /**
     * 获取价格列表
     * 默认值:500 最大值:1500.
     */
    public static List<Candlestick> listKline(String symbol, Long startTime, Long endTime) {
        if (Objects.isNull(endTime)) {
            endTime = DateTime.now().getTime();
        }
        if (Objects.isNull(startTime)) {
            // 不限制时间、一次查询
            return czClient.getCandlestick(symbol, CandlestickInterval.FIVE_MINUTES, null, endTime, maxLimit);
        }
        int dataCount = (int) ((endTime - startTime) / unit);
        if (dataCount < maxLimit) {
            // 时间范围内数据条数小于maxLimit 一次查询即可
            return czClient.getCandlestick(symbol, CandlestickInterval.FIVE_MINUTES, startTime, endTime, maxLimit);
        }

        List<Candlestick> rlt = new ArrayList<>(dataCount);
        long tmpEndTime = startTime + maxLimit * unit;
        while (tmpEndTime <= endTime) {
            rlt.addAll(czClient.getCandlestick(symbol, CandlestickInterval.FIVE_MINUTES, startTime, tmpEndTime, maxLimit));
            startTime = tmpEndTime;
            tmpEndTime = tmpEndTime + maxLimit * unit;
        }
        rlt.addAll(czClient.getCandlestick(symbol, CandlestickInterval.FIVE_MINUTES, startTime, endTime, maxLimit));
        return rlt;
    }

    public static void main(String[] args) {
//        listSymbol().forEach(System.out::println);
//
//        listAccRatio("AXSUSDT", null, null, 10).forEach(System.out::println);
//
        // 一个小时 12条、
        Date curDate = new Date();
        long startTime = DateUtil.offsetDay(new Date(), -3).getTime();
        long endTime = curDate.getTime();
        List<OpenInterestStat> data = listOpenInterest("AXSUSDT", startTime, endTime);
        System.out.println("数据量:" + data.size());
        System.out.println("查询初始时间：" + DateTime.of(data.get(0).getTimestamp()));
        System.out.println("查询结束时间：" + DateTime.of(data.get(data.size() - 1).getTimestamp()));
        List<DateTime> collect = data.stream().map(OpenInterestStat::getTimestamp).map(DateTime::of).collect(Collectors.toList());
        for (int i = 0; i < collect.size() - 1; i++) {
            if (collect.get(i + 1).getTime() - collect.get(i).getTime() != unit) {
                System.out.println("数据异常");
                return;
            }
        }
        System.out.println("数据正常");
//        listTopPositionRatio("AXSUSDT", null, null, 10).forEach(System.out::println);
//        listKline("AXSUSDT", null, null, 10).forEach(System.out::println);
    }
}
