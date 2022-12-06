package com.ychen.see.cz;

import com.binance.client.model.market.Candlestick;
import com.binance.client.model.market.CommonLongShortRatio;
import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.models.binance.CzClient;
import com.ychen.see.models.binance.util.CzUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yyy
 * @wx ychen5325
 * @email yangyouyuhd@163.com
 */
@Slf4j
public class CzClientTest {

    @Test
    public void listSymbolTest() {
        List<String> symbolList = CzClient.listSymbol();
        // 184
        log.info("total:{}", symbolList.size());
        // [BTCUSDT, ETHUSDT, BCHUSDT, XRPUSDT, EOSUSDT, LTCUSDT, TRXUSDT, ETCUSDT, LINKUSDT, XLMUSDT, ADAUSDT, XMRUSDT, DASHUSDT, ZECUSDT, XTZUSDT, BNBUSDT, ATOMUSDT, ONTUSDT, IOTAUSDT, BATUSDT, VETUSDT, NEOUSDT, QTUMUSDT, IOSTUSDT, THETAUSDT, ALGOUSDT, ZILUSDT, KNCUSDT, ZRXUSDT, COMPUSDT, OMGUSDT, DOGEUSDT, SXPUSDT, KAVAUSDT, BANDUSDT, RLCUSDT, WAVESUSDT, MKRUSDT, SNXUSDT, DOTUSDT, DEFIUSDT, YFIUSDT, BALUSDT, CRVUSDT, TRBUSDT, RUNEUSDT, SUSHIUSDT, EGLDUSDT, SOLUSDT, ICXUSDT, STORJUSDT, BLZUSDT, UNIUSDT, AVAXUSDT, FTMUSDT, HNTUSDT, ENJUSDT, FLMUSDT, TOMOUSDT, RENUSDT, KSMUSDT, NEARUSDT, AAVEUSDT, FILUSDT, RSRUSDT, LRCUSDT, MATICUSDT, OCEANUSDT, BELUSDT, CTKUSDT, AXSUSDT, ALPHAUSDT, ZENUSDT, SKLUSDT, GRTUSDT, 1INCHUSDT, BTCBUSD, CHZUSDT, SANDUSDT, ANKRUSDT, LITUSDT, UNFIUSDT, REEFUSDT, RVNUSDT, SFPUSDT, XEMUSDT, COTIUSDT, CHRUSDT, MANAUSDT, ALICEUSDT, HBARUSDT, ONEUSDT, LINAUSDT, STMXUSDT, DENTUSDT, CELRUSDT, HOTUSDT, MTLUSDT, OGNUSDT, NKNUSDT, DGBUSDT, 1000SHIBUSDT, BAKEUSDT, GTCUSDT, ETHBUSD, BTCDOMUSDT, BNBBUSD, ADABUSD, XRPBUSD, IOTXUSDT, DOGEBUSD, AUDIOUSDT, C98USDT, MASKUSDT, ATAUSDT, SOLBUSD, DYDXUSDT, 1000XECUSDT, GALAUSDT, CELOUSDT, ARUSDT, KLAYUSDT, ARPAUSDT, CTSIUSDT, LPTUSDT, ENSUSDT, PEOPLEUSDT, ANTUSDT, ROSEUSDT, DUSKUSDT, FLOWUSDT, IMXUSDT, API3USDT, GMTUSDT, APEUSDT, BNXUSDT, WOOUSDT, JASMYUSDT, DARUSDT, GALUSDT, AVAXBUSD, NEARBUSD, GMTBUSD, APEBUSD, GALBUSD, FTMBUSD, DODOBUSD, ANCBUSD, GALABUSD, TRXBUSD, 1000LUNCBUSD, LUNA2BUSD, OPUSDT, DOTBUSD, TLMBUSD, ICPBUSD, WAVESBUSD, LINKBUSD, SANDBUSD, LTCBUSD, MATICBUSD, CVXBUSD, FILBUSD, 1000SHIBBUSD, LEVERBUSD, ETCBUSD, LDOBUSD, UNIBUSD, AUCTIONBUSD, INJUSDT, STGUSDT, FOOTBALLUSDT, SPELLUSDT, 1000LUNCUSDT, LUNA2USDT, AMBBUSD, PHBBUSD, LDOUSDT, CVXUSDT, ICPUSDT, APTUSDT, QNTUSDT, APTBUSD, BLUEBIRDUSDT]
        log.info("list:\n{}", symbolList);
    }

    @Test
    public void listOpenInterest3DayTest() {
        String symbol = "BTCUSDT";

        // 获取最近三天的数据、并检查连续性
        long startTime = DateUtil.offsetDay(new Date(), -3).getTime();
        List<OpenInterestStat> dataList3Day = CzClient.listOpenInterest(symbol, startTime, null);
        log.info("三天前时间:{}", DateTime.of(startTime));
        log.info("第一条数据时间:{}", DateTime.of(dataList3Day.get(0).getTimestamp()));
        log.info("最后一条数据时间:{}", DateTime.of(dataList3Day.get(dataList3Day.size() - 1).getTimestamp()));
        log.info("三天理论数据总数:{},实际数据总数:{}", (3 * 24 * 12), dataList3Day.size());
        RuntimeException ex = null;
        try {
            CzUtil.dataTimeOrdered(dataList3Day);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }

    @Test
    public void listOpenInterest7DayTest() {
        String symbol = "BTCUSDT";

        // 获取10天前到3天前的数据、并检查连续性
        Date curTime = new Date();
        long startTime = DateUtil.offsetDay(curTime, -10).getTime();
        long endTime = DateUtil.offsetDay(curTime, -3).getTime();
        List<OpenInterestStat> dataList7Day = CzClient.listOpenInterest(symbol, startTime, endTime);
        log.info("十天前时间:{}", DateTime.of(startTime));
        log.info("三天前时间:{}", DateTime.of(endTime));
        log.info("第一条数据时间:{}", DateTime.of(dataList7Day.get(0).getTimestamp()));
        log.info("最后一条数据时间:{}", DateTime.of(dataList7Day.get(dataList7Day.size() - 1).getTimestamp()));
        log.info("七天理论数据总数:{},实际数据总数:{}", (7 * 24 * 12), dataList7Day.size());
        RuntimeException ex = null;
        try {
            CzUtil.dataTimeOrdered(dataList7Day);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }

    @Test
    public void listOpenInterest5MinTest() {
        String symbol = "BTCUSDT";

        // 获取最近五分钟的数据、并检查连续性
        long startTime = DateUtil.offsetMinute(new Date(), -5).getTime();
        List<OpenInterestStat> dataList3Day = CzClient.listOpenInterest(symbol, startTime, null);
        log.info("五分钟前时间:{}", DateTime.of(startTime));
        log.info("第一条数据时间:{}", DateTime.of(dataList3Day.get(0).getTimestamp()));
        log.info("五分钟理论数据总数:{},实际数据总数:{}", 1, dataList3Day.size());
        RuntimeException ex = null;
        try {
            CzUtil.dataTimeOrdered(dataList3Day);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }


    @Test
    public void listAccRatioTest3DayTest() {
        String symbol = "BTCUSDT";
        long startTime = DateUtil.offsetDay(new Date(), -3).getTime();
        List<CommonLongShortRatio> dataList3Day = CzClient.listAccRatio(symbol, startTime, null);

        log.info("三天前时间:{}", DateTime.of(startTime));
        log.info("第一条数据时间:{}", DateTime.of(dataList3Day.get(0).getTimestamp()));
        log.info("三天理论数据总数:{},实际数据总数:{}", (3 * 24 * 12), dataList3Day.size());
        log.info("数据连续性检查。。。。");
        RuntimeException ex = null;
        try {
            CzUtil.dataTimeOrdered(dataList3Day);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }

    @Test
    public void listTopPositionRatio3DayTest() {
        String symbol = "BTCUSDT";
        long startTime = DateUtil.offsetDay(new Date(), -3).getTime();
        List<CommonLongShortRatio> dataList3Day = CzClient.listTopPositionRatio(symbol, startTime, null);

        log.info("三天前时间:{}", DateTime.of(startTime));
        log.info("第一条数据时间:{}", DateTime.of(dataList3Day.get(0).getTimestamp()));
        log.info("三天理论数据总数:{},实际数据总数:{}", (3 * 24 * 12), dataList3Day.size());
        log.info("数据连续性检查。。。。");
        RuntimeException ex = null;
        try {
            CzUtil.dataTimeOrdered(dataList3Day);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }

    @Test
    public void listKline7DayTest() {
        String symbol = "BTCUSDT";
        // 获取10天前到3天前的数据、并检查连续性
        Date curTime = new Date();
        long startTime = DateUtil.offsetDay(curTime, -7).getTime();
        List<Candlestick> dataList7Day = CzClient.listKline(symbol, startTime, null);
        log.info("十天前时间:{}", DateTime.of(startTime));
        log.info("第一条数据时间:{}", DateTime.of(dataList7Day.get(0).getOpenTime()));
        log.info("最后一条数据时间:{}", DateTime.of(dataList7Day.get(dataList7Day.size() - 1).getOpenTime()));
        log.info("七天理论数据总数:{},实际数据总数:{}", (7 * 24 * 60), dataList7Day.size());
        RuntimeException ex = null;
        try {
            CzUtil.klineDataTimeOrdered(dataList7Day);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }

    /**
     * 仓位数据一次性初始化后、长期数据更新测试
     */
//    @Test
    public void listOpenInterestLongTimeTest() {
        String symbol = "BTCUSDT";

        // 获取最近三天的数据、并检查连续性
        long startTime = DateUtil.offsetDay(new Date(), -3).getTime();
        List<OpenInterestStat> dataList3Day = CzClient.listOpenInterest(symbol, startTime, null);
        CzUtil.dataTimeOrdered(dataList3Day);
        // 最小时间周期5分钟、测试11分钟
        int i = 3;
        while (i-- > 0) {
            startTime = dataList3Day.get(dataList3Day.size() - 1).getTimestamp();
            log.info("最近的数据时间 :{}", DateTime.of(startTime));
            try {
                TimeUnit.MINUTES.sleep(5);
            } catch (InterruptedException e) {

            }
            log.info("更新了一条数据、调api获取");
            List<OpenInterestStat> dataList = CzClient.listOpenInterest(symbol, startTime, null);
            log.info("data.size:{}", dataList.size());
            OpenInterestStat data = dataList.get(dataList.size() - 1);
            log.info("新数据的时间:{}", DateTime.of(data.getTimestamp()));
            dataList3Day.add(data);
            CzUtil.dataTimeOrdered(dataList3Day);
        }
    }

    /**
     * 仓位数据一次性初始化后、长期数据更新测试
     */
//    @Test
    public void listKlineLongTimeTest() {
        String symbol = "BTCUSDT";

        // 获取最近三天的数据、并检查连续性
        long startTime = DateUtil.offsetDay(new Date(), -3).getTime();
        List<Candlestick> klineList = CzClient.listKline(symbol, startTime, null);
        CzUtil.dataTimeOrdered(klineList);
        // 最小时间周期5分钟、测试11分钟
        int i = 10;
        while (i-- > 0) {
            startTime = klineList.get(klineList.size() - 1).getOpenTime();
            log.info("最近的数据时间 :{}", DateTime.of(startTime));
            try {
                TimeUnit.MINUTES.sleep(3);
            } catch (InterruptedException e) {

            }
            log.info("更新了一条数据、调api获取");
            List<Candlestick> dataList = CzClient.listKline(symbol, startTime, null);
            log.info("data.size:{}", dataList.size());
            log.info("新数据的时间:{}", DateTime.of(dataList.get(dataList.size() - 1).getOpenTime()));
            klineList.addAll(dataList);
            CzUtil.dataTimeOrdered(klineList);
        }
    }
}



