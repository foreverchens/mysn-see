package com.ychen.see.models.binance.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yyy
 * @wx ychen5325
 * @email yangyouyuhd@163.com
 */
@Data
@ToString
public class ContractDataM {
    private String symbol;

    /**
     * 0 -> 持仓量数据
     * 1 -> 账户多空比数据
     * 2 -> 大户持仓量多空比数据
     * 3 -> 价格数据
     */
    private Map<String, ArrayDeque> map;

    public ContractDataM(String symbol) {
        this.map = new HashMap<>();
    }


    public void fill(String type, List dataList) {
        ArrayDeque deque = map.get(type);
        if (Objects.isNull(deque)) {
            deque = new ArrayDeque();
            map.put(type, deque);
        }
        deque.addAll(dataList);
    }

    public <T> ArrayDeque<T> get(String type) {
        return map.get(type);
    }
}
