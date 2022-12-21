package com.ychen.see.models.analyze;

import com.ychen.see.common.CallResult;
import com.ychen.see.models.analyze.func.MarketFeatureFunc;
import com.ychen.see.models.event.EventDataDomain;
import com.ychen.see.models.event.domain.ChangeEventInfo;

import cn.hutool.core.collection.CollectionUtil;

import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yyy
 */
@Slf4j
@Configuration
public class AnalyzeService {

	@Resource
	private EventDataDomain eventDataDomain;

	@Resource
	private List<MarketFeatureFunc> featureFuncList;

	public List<String> analyze(String symbol) {
		List<ChangeEventInfo> eventInfoList = eventDataDomain.listEventInfo(symbol);
		if (CollectionUtil.isEmpty(eventInfoList)) {
			return Collections.EMPTY_LIST;
		}
		List<String> rlt = new ArrayList<>();
		for (MarketFeatureFunc featureFunc : featureFuncList) {
			CallResult<String> analyzeRlt = featureFunc.analyze(symbol, eventInfoList);
			if (analyzeRlt.getSuccess()) {
				rlt.add(analyzeRlt.getData());
			}
		}
		return rlt;
	}
}
