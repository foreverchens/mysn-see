package com.ychen.see.models.binance;

import com.binance.client.model.market.Candlestick;
import com.binance.client.model.market.CommonLongShortRatio;
import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.common.config.SwitchConfig;
import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.binance.domain.SymbolOriginalDataTo;
import com.ychen.see.models.binance.util.CzUtil;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 存储原始数据、并保持其最新
 *
 * @author yyy
 */
@Data
@Slf4j
@Component
public class ContractOriginalDataDomain {

	/**
	 * 最多存储几天的数据
	 */
	@Value("${see.cz.storeDay:7}")
	private int storeDay;
	private int queueSize;

	private List<String> symbolList;
	/**
	 * 币对的持仓量数据
	 */
	private Map<String, ArrayDeque> symbolAndOpenPositionMap;
	/**
	 * 币对的Kline数据
	 */
	private Map<String, ArrayDeque> symbolAndKlineMap;
	/**
	 * 币对的账户多空比数据
	 */
	private Map<String, ArrayDeque> symbolAndAccRatioMap;
	/**
	 * 币对的大户持仓量多空比数据
	 */
	private Map<String, ArrayDeque> symbolAndTopOpenPosRatioMap;

	@Resource
	private SwitchConfig switchConfig;

	/*-----------------初始化函数-----------------*/

	@PostConstruct
	private void init() {
		// 初始化容器和客户端
		queueSize = storeDay * 24 * 12;
		symbolList = CzClient.listSymbol();
		log.info("[init] symbolList.size = {}", symbolList.size());
		// todo 测试用
//		symbolList = Arrays.asList("AXSUSDT", "BTCUSDT");

		symbolAndOpenPositionMap = new HashMap<>(symbolList.size());
		symbolAndKlineMap = new HashMap<>(symbolList.size());
		symbolAndAccRatioMap = new HashMap<>(symbolList.size());
		symbolAndTopOpenPosRatioMap = new HashMap<>(symbolList.size());

		// 初始数据填充
		this.initOpenInterestStatList();
	}

	private void initOpenInterestStatList() {
		long startTime = DateUtil.offsetDay(new Date(), storeDay * -1).getTime();
		for (String symbol : symbolList) {
			log.info("[init] {} 源数据初始化中....", symbol);
			if (switchConfig.getOpenPos()) {
				// 初始化持仓量数据。。。
				List<OpenInterestStat> openInterestStatList = CzClient.listOpenInterest(symbol, startTime, null);
				symbolAndOpenPositionMap.put(symbol, new ArrayDeque<>(openInterestStatList));
			}
			if (switchConfig.getTopPosRatio()) {
				// 初始化大户持仓量多空比数据
				List<CommonLongShortRatio> topPositionRatioList = CzClient.listTopPositionRatio(symbol, startTime,
						null);
				symbolAndTopOpenPosRatioMap.put(symbol, new ArrayDeque<>(topPositionRatioList));
			}
			if (switchConfig.getKline()) {
				// 初始化k线数据。。。
				List<Candlestick> klineList = CzClient.listKline(symbol, startTime, null);
				symbolAndKlineMap.put(symbol, new ArrayDeque<>(klineList));
			}
			if (switchConfig.getAccRatio()) {
				// 初始化账户多空比数据
				List<CommonLongShortRatio> accRatioList = CzClient.listAccRatio(symbol, startTime, null);
				symbolAndAccRatioMap.put(symbol, new ArrayDeque<>(accRatioList));
			}
		}
	}

	/*-----------------开放函数-----------------*/

	/**
	 * 获取最近n天数据
	 * 持仓量、多空比数据都是5分钟一条、使用该方法
	 */
	public <T> SymbolOriginalDataTo<T> listLastContractData(String symbol, String dataType, int day) {
		if (day > storeDay) {
			// 超过存储日期的调api获取
			return listLongTimeContractData(symbol, dataType, day);
		}
		int rltTotal = day * 24 * 12;
		if (StringUtils.equals(DataTypeConstant.kline, dataType)) {
			// kline 最小周期为1min
			rltTotal *= 5;
		}
		ArrayDeque<T> dataQueue = this.<T>get(symbol, dataType);
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

	public void updateContractDataSource(String symbol, String dataType) {
		if (StringUtils.equals(dataType, DataTypeConstant.openInterest)) {
			if (switchConfig.getOpenPos()) {
				updateOpenInterestStatData(symbol);
			}
		} else if (StringUtils.equals(dataType, DataTypeConstant.kline)) {
			if (switchConfig.getKline()) {
				updateKlineData(symbol);
			}
		} else if (StringUtils.equals(dataType, DataTypeConstant.accRatio)) {
			if (switchConfig.getAccRatio()) {
				updateAccRatioData(symbol);
			}
		} else if (StringUtils.equals(dataType, DataTypeConstant.topPositionRatio)) {
			if (switchConfig.getTopPosRatio()) {
				updateTopPositionRatioData(symbol);
			}
		}
	}

	public BigDecimal getCurVal(String symbol, String dataType) {
		if (!DataTypeConstant.typeList.contains(dataType)) {
			throw new RuntimeException("dataType illegal");
		}
		ArrayDeque<Object> deque = get(symbol, dataType);
		Object last = deque.getLast();
		if (last instanceof OpenInterestStat) {
			return ((OpenInterestStat) last).getSumOpenInterestValue();
		} else if (last instanceof CommonLongShortRatio) {
			return ((CommonLongShortRatio) last).getLongShortRatio();
		} else {
			return ((Candlestick) last).getOpen();
		}
	}

	/*-----------------私有函数-----------------*/

	private SymbolOriginalDataTo listLongTimeContractData(String symbol, String dataType, int day) {
		long startTime = DateUtil.offsetDay(new Date(), day * -1).getTime();
		switch (dataType) {
			case DataTypeConstant.openInterest:
				return new SymbolOriginalDataTo<>(CzClient.listOpenInterest(symbol, startTime, null));
			case DataTypeConstant.kline:
				return new SymbolOriginalDataTo<>(CzClient.listKline(symbol, startTime, null));
			case DataTypeConstant.accRatio:
				return new SymbolOriginalDataTo<>(CzClient.listAccRatio(symbol, startTime, null));
			case DataTypeConstant.topPositionRatio:
				return new SymbolOriginalDataTo<>(CzClient.listTopPositionRatio(symbol, startTime, null));
			default:
				throw new RuntimeException("type not found");
		}
	}

	private <T> ArrayDeque<T> get(String symbol, String dataType) {
		switch (dataType) {
			case DataTypeConstant.openInterest:
				return symbolAndOpenPositionMap.get(symbol);
			case DataTypeConstant.kline:
				return symbolAndKlineMap.get(symbol);
			case DataTypeConstant.accRatio:
				return symbolAndAccRatioMap.get(symbol);
			case DataTypeConstant.topPositionRatio:
				return symbolAndTopOpenPosRatioMap.get(symbol);
			default:
				throw new RuntimeException("type not found");
		}
	}

	private void updateOpenInterestStatData(String symbol) {
		ArrayDeque<OpenInterestStat> dataQueue = symbolAndOpenPositionMap.get(symbol);
		OpenInterestStat first = dataQueue.peekLast();
		List<OpenInterestStat> openInterestStatList = CzClient.listOpenInterest(symbol, first.getTimestamp(), null);
		if (CollectionUtil.isEmpty(openInterestStatList)) {
			log.info("{}-{}无更新数据。。。。", symbol, DataTypeConstant.openInterest);
			return;
		}
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

	private void updateAccRatioData(String symbol) {
		ArrayDeque<CommonLongShortRatio> dataQueue = symbolAndAccRatioMap.get(symbol);
		CommonLongShortRatio first = dataQueue.peekLast();
		List<CommonLongShortRatio> accRatioList = CzClient.listAccRatio(symbol, first.getTimestamp(), null);
		if (CollectionUtil.isEmpty(accRatioList)) {
			log.info("{}-{}无更新数据。。。。", symbol, DataTypeConstant.accRatio);
			return;
		}
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

	private void updateTopPositionRatioData(String symbol) {
		ArrayDeque<CommonLongShortRatio> dataQueue = symbolAndTopOpenPosRatioMap.get(symbol);
		CommonLongShortRatio first = dataQueue.peekLast();
		List<CommonLongShortRatio> accRatioList = CzClient.listTopPositionRatio(symbol, first.getTimestamp(), null);
		if (CollectionUtil.isEmpty(accRatioList)) {
			log.info("{}-{}无更新数据。。。。", symbol, DataTypeConstant.topPositionRatio);
			return;
		}
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

	private void updateKlineData(String symbol) {
		ArrayDeque<Candlestick> dataQueue = symbolAndKlineMap.get(symbol);
		Candlestick first = dataQueue.peekLast();
		List<Candlestick> accRatioList = CzClient.listKline(symbol, first.getOpenTime(), null);
		if (CollectionUtil.isEmpty(accRatioList)) {
			log.info("{}-{}无更新数据。。。。", symbol, DataTypeConstant.kline);
			return;
		}
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
