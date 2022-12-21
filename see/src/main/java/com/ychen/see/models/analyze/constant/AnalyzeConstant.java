package com.ychen.see.models.analyze.constant;

import com.ychen.see.common.enums.IntervalEnum;

import cn.hutool.core.map.MapUtil;

import java.util.Map;

/**
 * @author yyy
 */
public interface AnalyzeConstant {

	Map<String, Integer> vePeriodMap = MapUtil.<String, Integer>builder()
											  .put(IntervalEnum.d3.name(), 1)
											  .put(IntervalEnum.w1.name(), 2)
											  .put(IntervalEnum.w2.name(), 4)
											  .build();
}
