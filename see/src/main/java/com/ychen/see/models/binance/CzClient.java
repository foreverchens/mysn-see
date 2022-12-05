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

import org.apache.commons.lang3.StringUtils;

import java.util.List;
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

    private static final SyncRequestClient czClient = BinanceApiInternalFactory.getInstance().createSyncRequestClient("_", "_", new RequestOptions());

    /**
     * 获取可供交易的合约币对列表
     *
     * @return
     */
    public static List<String> listSymbol() {
        return czClient.getExchangeInformation().getSymbols().stream().filter(ele -> StringUtils.equals("TRADING", ele.getStatus()) && !StringUtils.contains(ele.getSymbol(), "_")).map(ExchangeInfoEntry::getSymbol).collect(Collectors.toList());
    }

    /**
     * 获取持仓量列表
     * 若无 startime 和 endtime 限制， 则默认返回当前时间往前的limit值
     * 仅支持最近30天的数据
     *
     * @param limit default 30, max 500
     * @return
     */
    public static List<OpenInterestStat> listOpenInterest(String symbol, Long startTime, Long endTime, Integer limit) {
        return czClient.getOpenInterestStat(symbol, PeriodType._5m, startTime, endTime, limit);
    }

    /**
     * 获取账户多空比数据
     * 若无 startime 和 endtime 限制， 则默认返回当前时间往前的limit值
     * 仅支持最近30天的数据
     *
     * @param limit default 30, max 500
     * @return
     */
    public static List<CommonLongShortRatio> listAccRatio(String symbol, Long startTime, Long endTime, Integer limit) {
        return czClient.getGlobalAccountRatio(symbol, PeriodType._5m, startTime, endTime, limit);
    }


    /**
     * 获取大户持仓量多空比
     * 若无 startime 和 endtime 限制， 则默认返回当前时间往前的limit值
     * 仅支持最近30天的数据
     *
     * @param limit default 30, max 500
     * @return
     */
    public static List<CommonLongShortRatio> listTopPositionRatio(String symbol, Long startTime, Long endTime, Integer limit) {
        return czClient.getTopTraderPositionRatio(symbol, PeriodType._5m, startTime, endTime, limit);
    }

    /**
     * 获取价格列表
     *
     * @param limit 默认值:500 最大值:1500.
     * @return
     */
    public static List<Candlestick> listKline(String symbol, Long startTime, Long endTime, Integer limit) {
        return czClient.getCandlestick(symbol, CandlestickInterval.FIVE_MINUTES, startTime, endTime, limit);
    }
//
//
//    public static void main(String[] args) {
//        listSymbol().forEach(System.out::println);
//
//        listAccRatio("AXSUSDT", null, null, 10).forEach(System.out::println);
//
//        listOpenInterest("AXSUSDT", null, null, 10).forEach(System.out::println);
//
//        listTopPositionRatio("AXSUSDT", null, null, 10).forEach(System.out::println);
//
//        listKline("AXSUSDT", null, null, 10).forEach(System.out::println);
//    }
}
