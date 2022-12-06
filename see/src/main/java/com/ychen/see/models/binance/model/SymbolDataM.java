package com.ychen.see.models.binance.model;

import com.binance.client.model.market.OpenInterestStat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayDeque;

/**
 * @author yyy
 * @wx ychen5325
 * @email yangyouyuhd@163.com
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SymbolDataM {
    private String symbol;

    /**
     * 持仓量数据
     */
    private ArrayDeque<OpenInterestStat> openInterestStatList;
}
