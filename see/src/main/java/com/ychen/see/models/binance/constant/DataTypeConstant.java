package com.ychen.see.models.binance.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 合约数据类型常量
 *
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
public interface DataTypeConstant {

    String openInterest = "0";
    String accRatio = "1";
    String topPositionRatio = "2";
    String kline = "3";

    List<String> typeList = Arrays.asList(openInterest, accRatio, topPositionRatio, kline);
}
