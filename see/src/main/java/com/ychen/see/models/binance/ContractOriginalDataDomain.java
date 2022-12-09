package com.ychen.see.models.binance;

import com.binance.client.model.market.Candlestick;
import com.binance.client.model.market.CommonLongShortRatio;
import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.binance.domain.SymbolOriginalDataM;
import com.ychen.see.models.binance.domain.SymbolOriginalDataTo;
import com.ychen.see.models.binance.util.CzUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
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
 */
@Slf4j
@Component
public class ContractOriginalDataDomain {

	/**
	 * 最多存储几天的数据
	 */
	@Value("${see.cz.storeDay:7}")
	private int storeDay;
	private int queueSize;

	/**
	 * symbol -> symbolDataM
	 */
	private Map<String, SymbolOriginalDataM> symbolAndDataMap;

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
			SymbolOriginalDataM symbolOriginalDataM = new SymbolOriginalDataM(symbol);
			symbolAndDataMap.put(symbol, symbolOriginalDataM);
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
			SymbolOriginalDataM symbolOriginalDataM = symbolAndDataMap.get(symbol);
			// 初始化持仓量数据。。。
			List<OpenInterestStat> openInterestStatList = CzClient.listOpenInterest(symbol, startTime, null);
			symbolOriginalDataM.fill(DataTypeConstant.openInterest, openInterestStatList);
			// 初始化大户持仓量多空比数据
			List<CommonLongShortRatio> topPositionRatioList = CzClient.listTopPositionRatio(symbol, startTime, null);
			symbolOriginalDataM.fill(DataTypeConstant.topPositionRatio, topPositionRatioList);
			// 初始化k线数据。。。
			List<Candlestick> klineList = CzClient.listKline(symbol, startTime, null);
			symbolOriginalDataM.fill(DataTypeConstant.kline, klineList);
			// 初始化账户多空比数据
			List<CommonLongShortRatio> accRatioList = CzClient.listAccRatio(symbol, startTime, null);
			symbolOriginalDataM.fill(DataTypeConstant.accRatio, accRatioList);
		}
	}

	/**
	 * 获取最近n天数据
	 * 持仓量、多空比数据都是5分钟一条、使用该方法
	 */
	public <T> SymbolOriginalDataTo<T> listLastContractData(String symbol, int day, String dataType) {
		int rltTotal = day * 24 * 12;
		if (StringUtils.equals(DataTypeConstant.kline, dataType)) {
			// kline 最小周期为1min
			rltTotal *= 5;
		}
		SymbolOriginalDataM symbolOriginalDataM = symbolAndDataMap.get(symbol);
		ArrayDeque<T> dataQueue = symbolOriginalDataM.<T>get(dataType);
		if (dataQueue.size() == rltTotal) {
			// 你想要全部
			return new SymbolOriginalDataTo<>(new ArrayList<>(dataQueue));
		}
		List<T> rlt = new ArrayList<>(rltTotal);
		Iterator<T> iterator = dataQueue.descendingIterator();
		while (iterator.hasNext()) {
			rlt.add(iterator.next());
			if (rlt.size() == rltTotal) {
				break;
			}
		}
		Collections.reverse(rlt);
		return new SymbolOriginalDataTo<>(rlt);
	}

	private final class UpdateTask implements Runnable {

		@SneakyThrows
		@Override
		public void run() {
			Thread.currentThread().setName("see.cz.dataDomain.UpdateTaskThread");
			log.info("[init] start suc .....");
			int min = DateTime.now().getMinutes() % 5;
			while (min != 0) {
				// 整点在运行
				Thread.yield();
			}
			while (true) {
				try {
					log.info("update start ......");
					for (String symbol : symbolAndDataMap.keySet()) {
						SymbolOriginalDataM symbolOriginalDataM = symbolAndDataMap.get(symbol);
						// 更新持仓量数据
						updateOpenInterestStatData(symbol, symbolOriginalDataM);
						// 更新账户多空比数据
						updateAccRatioData(symbol, symbolOriginalDataM);
						// 更新大户持仓量数据
						updateTopPositionRatioData(symbol, symbolOriginalDataM);
						// 更新kline数据
						updateKlineData(symbol, symbolOriginalDataM);
					}
					log.info("update end ......");
					TimeUnit.MINUTES.sleep(5);
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		}


		private void updateOpenInterestStatData(String symbol, SymbolOriginalDataM symbolOriginalDataM) {
			ArrayDeque<OpenInterestStat> dataQueue = symbolOriginalDataM.get(DataTypeConstant.openInterest);
			OpenInterestStat first = dataQueue.peekLast();
			List<OpenInterestStat> openInterestStatList = CzClient.listOpenInterest(symbol, first.getTimestamp(),
					null);
			log.info("{} 开始更新持仓量数据,新到数据{}条", symbol, openInterestStatList.size());
			log.info("{} 更新前持仓量数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getTimestamp()),
					DateTime.of(dataQueue.peekLast().getTimestamp()));
			for (OpenInterestStat data : openInterestStatList) {
				if (dataQueue.size() == queueSize) {
					dataQueue.removeFirst();
				}
				dataQueue.addLast(data);
			}
			CzUtil.dataTimeOrdered(dataQueue);
			log.info("{} 更新后持仓量数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getTimestamp()),
					DateTime.of(dataQueue.peekLast().getTimestamp()));
		}

		private void updateAccRatioData(String symbol, SymbolOriginalDataM symbolOriginalDataM) {
			ArrayDeque<CommonLongShortRatio> dataQueue = symbolOriginalDataM.get(DataTypeConstant.accRatio);
			CommonLongShortRatio first = dataQueue.peekLast();
			List<CommonLongShortRatio> accRatioList = CzClient.listAccRatio(symbol, first.getTimestamp(), null);
			log.info("{} 开始更新账户多空比数据,新到数据{}条", symbol, accRatioList.size());
			log.info("{} 更新前账户多空比数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getTimestamp()),
					DateTime.of(dataQueue.peekLast().getTimestamp()));
			for (CommonLongShortRatio data : accRatioList) {
				if (dataQueue.size() == queueSize) {
					dataQueue.removeFirst();
				}
				dataQueue.addLast(data);
			}
			CzUtil.dataTimeOrdered(dataQueue);
			log.info("{} 更新后账户多空比数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getTimestamp()),
					DateTime.of(dataQueue.peekLast().getTimestamp()));
		}

		private void updateTopPositionRatioData(String symbol, SymbolOriginalDataM symbolOriginalDataM) {
			ArrayDeque<CommonLongShortRatio> dataQueue = symbolOriginalDataM.get(DataTypeConstant.topPositionRatio);
			CommonLongShortRatio first = dataQueue.peekLast();
			List<CommonLongShortRatio> accRatioList = CzClient.listTopPositionRatio(symbol, first.getTimestamp(),
					null);
			log.info("{} 开始更新大户持仓量多空比数据,新到数据{}条", symbol, accRatioList.size());
			log.info("{} 更新前持仓量多空比数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getTimestamp()),
					DateTime.of(dataQueue.peekLast().getTimestamp()));
			for (CommonLongShortRatio data : accRatioList) {
				if (dataQueue.size() == queueSize) {
					dataQueue.removeFirst();
				}
				dataQueue.addLast(data);
			}
			CzUtil.dataTimeOrdered(dataQueue);
			log.info("{} 更新后持仓量多空比数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getTimestamp()),
					DateTime.of(dataQueue.peekLast().getTimestamp()));
		}

		private void updateKlineData(String symbol, SymbolOriginalDataM symbolOriginalDataM) {
			ArrayDeque<Candlestick> dataQueue = symbolOriginalDataM.get(DataTypeConstant.kline);
			Candlestick first = dataQueue.peekLast();
			List<Candlestick> accRatioList = CzClient.listKline(symbol, first.getOpenTime(), null);
			log.info("{} 开始更新kline数据,新到数据{}条", symbol, accRatioList.size());
			log.info("{} 更新前kline数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getOpenTime()),
					DateTime.of(dataQueue.peekLast().getOpenTime()));
			for (Candlestick data : accRatioList) {
				if (dataQueue.size() == queueSize) {
					dataQueue.removeFirst();
				}
				dataQueue.addLast(data);
			}
			CzUtil.dataTimeOrdered(dataQueue);
			log.info("{} 更新后kline数据范围: {} -> {}", symbol, DateTime.of(dataQueue.peekFirst().getOpenTime()),
					DateTime.of(dataQueue.peekLast().getOpenTime()));

		}
	}
}
