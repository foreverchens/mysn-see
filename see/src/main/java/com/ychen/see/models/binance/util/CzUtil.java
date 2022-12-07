package com.ychen.see.models.binance.util;

import com.ychen.see.common.enums.IntervalEnum;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
@Slf4j
public class CzUtil {

    public static void dataTimeOrdered(Collection dataList) {
        checkOrdered(dataList, "timestamp", IntervalEnum.m5);
    }

    public static void klineDataTimeOrdered(Collection dataList) {
        checkOrdered(dataList, "openTime", IntervalEnum.m1);
    }

//    private static void checkOrdered(List dataList, String fieldName, IntervalEnum intervalEnum) {
//        Field timestamp = null;
//        try {
//            timestamp = dataList.get(0).getClass().getDeclaredField(fieldName);
//            timestamp.setAccessible(true);
//            for (int i = 1; i < dataList.size(); i++) {
//                Long lv = (Long) timestamp.get(dataList.get(i - 1));
//                Long rv = (Long) timestamp.get(dataList.get(i));
//                if (rv - lv != intervalEnum.time) {
//                    throw new RuntimeException("data not order");
//                }
//            }
//        } catch (RuntimeException ex) {
//            throw ex;
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }

    private static void checkOrdered(Collection dataList, String fieldName, IntervalEnum intervalEnum) {
        Field timestamp = null;
        try {
            Object[] arr = dataList.toArray();
            timestamp = arr[0].getClass().getDeclaredField(fieldName);
            timestamp.setAccessible(true);
            for (int i = 1; i < arr.length; i++) {
                Long lv = (Long) timestamp.get(arr[i - 1]);
                Long rv = (Long) timestamp.get(arr[i]);
                if (rv - lv != intervalEnum.time) {
                    throw new RuntimeException("data not order");
                }
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
