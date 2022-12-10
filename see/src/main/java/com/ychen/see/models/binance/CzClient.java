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
import com.ychen.see.common.enums.IntervalEnum;
import com.ychen.see.models.binance.util.CzUtil;

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

    private static final int klineMaxLimit = 1500;
    private static final int maxLimit = 500;

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
        endTime = endTime / IntervalEnum.m5.time * IntervalEnum.m5.time + IntervalEnum.m5.time;
        if (Objects.isNull(startTime)) {
            // 不限制时间、一次查询
            return czClient.getOpenInterestStat(symbol, PeriodType._5m, null, endTime, maxLimit);
        }
        startTime = startTime / IntervalEnum.m5.time * IntervalEnum.m5.time;
        int dataCount = (int) ((endTime - startTime) / IntervalEnum.m5.time);
        if (dataCount < maxLimit) {
            // 时间范围内数据条数小于maxLimit 一次查询即可
            return czClient.getOpenInterestStat(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, endTime, maxLimit);
        }

        List<OpenInterestStat> rlt = new ArrayList<>(dataCount);
        long tmpEndTime = startTime + maxLimit * IntervalEnum.m5.time;
        while (tmpEndTime <= endTime) {
            rlt.addAll(czClient.getOpenInterestStat(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, tmpEndTime, maxLimit));
            startTime = tmpEndTime;
            tmpEndTime = tmpEndTime + maxLimit * IntervalEnum.m5.time;
        }
        rlt.addAll(czClient.getOpenInterestStat(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, endTime, maxLimit));
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
        endTime = endTime / IntervalEnum.m5.time * IntervalEnum.m5.time + IntervalEnum.m5.time;
        if (Objects.isNull(startTime)) {
            // 不限制时间、一次查询
            return czClient.getGlobalAccountRatio(symbol, PeriodType._5m, null, endTime, maxLimit);
        }
        startTime = startTime / IntervalEnum.m5.time * IntervalEnum.m5.time;
        int dataCount = (int) ((endTime - startTime) / IntervalEnum.m5.time);
        if (dataCount < maxLimit) {
            // 时间范围内数据条数小于maxLimit 一次查询即可
            return czClient.getGlobalAccountRatio(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, endTime, maxLimit);
        }

        List<CommonLongShortRatio> rlt = new ArrayList<>(dataCount);
        long tmpEndTime = startTime + maxLimit * IntervalEnum.m5.time;
        while (tmpEndTime <= endTime) {
            rlt.addAll(czClient.getGlobalAccountRatio(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, tmpEndTime, maxLimit));
            startTime = tmpEndTime;
            tmpEndTime = tmpEndTime + maxLimit * IntervalEnum.m5.time;
        }
        rlt.addAll(czClient.getGlobalAccountRatio(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, endTime, maxLimit));
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
        endTime = endTime / IntervalEnum.m5.time * IntervalEnum.m5.time + IntervalEnum.m5.time;
        if (Objects.isNull(startTime)) {
            // 不限制时间、一次查询
            return czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, null, endTime, maxLimit);
        }
        startTime = startTime / IntervalEnum.m5.time * IntervalEnum.m5.time;
        int dataCount = (int) ((endTime - startTime) / IntervalEnum.m5.time);
        if (dataCount < maxLimit) {
            // 时间范围内数据条数小于maxLimit 一次查询即可
            return czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, endTime, maxLimit);
        }

        List<CommonLongShortRatio> rlt = new ArrayList<>(dataCount);
        long tmpEndTime = startTime + maxLimit * IntervalEnum.m5.time;
        while (tmpEndTime <= endTime) {
            rlt.addAll(czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, tmpEndTime, maxLimit));
            startTime = tmpEndTime;
            tmpEndTime = tmpEndTime + maxLimit * IntervalEnum.m5.time;
        }
        rlt.addAll(czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, startTime + IntervalEnum.m5.time, endTime, maxLimit));
        return rlt;
    }

    /**
     * 获取价格列表
     * 价格数据采用m1周期、便于震荡池
     * 默认值:500 最大值:1500.
     */
    public static List<Candlestick> listKline(String symbol, Long startTime, Long endTime) {
        if (Objects.isNull(endTime)) {
            endTime = DateTime.now().getTime();
        }
        endTime = endTime / IntervalEnum.m1.time * IntervalEnum.m1.time + IntervalEnum.m1.time;
        if (Objects.isNull(startTime)) {
            // 不限制时间、一次查询
            return czClient.getCandlestick(symbol, CandlestickInterval.ONE_MINUTE, null, endTime, klineMaxLimit);
        }
        startTime = startTime / IntervalEnum.m1.time * IntervalEnum.m1.time;
        int dataCount = (int) ((endTime - startTime) / IntervalEnum.m1.time);
        if (dataCount < klineMaxLimit) {
            // 时间范围内数据条数小于maxLimit 一次查询即可
            return czClient.getCandlestick(symbol, CandlestickInterval.ONE_MINUTE, startTime + IntervalEnum.m1.time, endTime, klineMaxLimit);
        }

        List<Candlestick> rlt = new ArrayList<>(dataCount);
        long tmpEndTime = startTime + klineMaxLimit * IntervalEnum.m1.time;
        while (tmpEndTime <= endTime) {
            rlt.addAll(czClient.getCandlestick(symbol, CandlestickInterval.ONE_MINUTE, startTime + IntervalEnum.m1.time, tmpEndTime, klineMaxLimit));
            startTime = tmpEndTime;
            tmpEndTime = tmpEndTime + klineMaxLimit * IntervalEnum.m1.time;
        }
        rlt.addAll(czClient.getCandlestick(symbol, CandlestickInterval.ONE_MINUTE, startTime + IntervalEnum.m1.time, endTime, klineMaxLimit));
        return rlt;
    }
}
