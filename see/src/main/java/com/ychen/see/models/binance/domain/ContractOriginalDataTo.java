package com.ychen.see.models.binance.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
@AllArgsConstructor
public class ContractOriginalDataTo<T> {

    @Getter
    List<T> data;

}
