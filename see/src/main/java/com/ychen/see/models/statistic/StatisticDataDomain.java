package com.ychen.see.models.statistic;

import com.ychen.see.models.binance.OriginalDataDomain;
import com.ychen.see.models.statistic.domain.ContractStatisticDataM;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.Map;

/**
 * @author yyy
 */
@Slf4j
@Component
public class StatisticDataDomain {

	@Resource
	private OriginalDataDomain originalDataDomain;

	private Map<String, ContractStatisticDataM> map;


}
