package com.ychen.see.models.analyze.func;

import com.ychen.see.common.CallResult;
import com.ychen.see.models.event.domain.ChangeEventInfo;

import java.util.List;

/**
 * @author yyy
 */
@FunctionalInterface
public interface MarketFeatureFunc {


	/**
	 * 分析事件列表、
	 * 检查是否满足某种看涨|看跌特征
	 */
	CallResult<String> analyze(String symbol, List<ChangeEventInfo> eventInfoList);
}
