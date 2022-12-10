package com.ychen.see.event;

import com.ychen.see.models.binance.constant.DataTypeConstant;
import com.ychen.see.models.event.EventDataDomain;
import com.ychen.see.models.statistic.StatisticDataDomain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author yyy
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EventDataDomainTest {
	@Resource
	EventDataDomain eventDataDomain;
	@Resource
	StatisticDataDomain statisticDataDomain;

	@Before
	public void before() {
		statisticDataDomain.statistic("BTCUSDT", DataTypeConstant.openInterest);
	}

	@Test
	public void changeEventTest() {
		eventDataDomain.changeEvent("BTCUSDT", DataTypeConstant.openInterest);
	}

	@Test
	public void listEventInfoTest() {
		System.out.println(eventDataDomain.listEventInfo("BTCUSDT"));
	}
}
