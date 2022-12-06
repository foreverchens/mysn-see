package com.ychen.see.models.binance.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author yyy
 * @wx ychen5325
 * @email yangyouyuhd@163.com
 */
@AllArgsConstructor
public class ContractDataTo<T> {

    @Getter
    List<T> data;

}
