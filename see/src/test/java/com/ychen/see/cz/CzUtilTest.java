package com.ychen.see.cz;

import com.binance.client.model.market.OpenInterestStat;
import com.ychen.see.common.enums.IntervalEnum;
import com.ychen.see.models.binance.util.CzUtil;

import cn.hutool.core.date.DateUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
public class CzUtilTest {


    @Test
    public void sucTest() {
        List<OpenInterestStat> dataList = new ArrayList<>();
        long time = DateUtil.offsetDay(new Date(), -1).getTime();
        for (int i = 0; i < 100; i++) {
            OpenInterestStat data = new OpenInterestStat();
            data.setTimestamp(time += IntervalEnum.m5.time);
            dataList.add(data);
        }
        RuntimeException ex = null;
        try {
            CzUtil.dataTimeOrdered(dataList);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }

    @Test
    public void failTest() {
        List<OpenInterestStat> dataList = new ArrayList<>();
        long time = DateUtil.offsetDay(new Date(), -1).getTime();
        for (int i = 0; i < 100; i++) {
            OpenInterestStat data = new OpenInterestStat();
            data.setTimestamp(time += IntervalEnum.m5.time);
            if (i == 50) {
                continue;
            }
            dataList.add(data);
        }
        RuntimeException ex = null;
        try {
            CzUtil.dataTimeOrdered(dataList);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);

        dataList = new ArrayList<>();
        time = DateUtil.offsetDay(new Date(), -1).getTime();
        for (int i = 0; i < 100; i++) {
            OpenInterestStat data = new OpenInterestStat();
            data.setTimestamp(time += IntervalEnum.m5.time);
            if (i == 50) {
                dataList.add(data);
            }
            dataList.add(data);
        }
        ex = null;
        try {
            CzUtil.dataTimeOrdered(dataList);
        } catch (RuntimeException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }
}
