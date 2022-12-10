package com.ychen.see.models.event.func;

import com.ychen.see.models.event.domain.ChangeEventInfo;
import com.ychen.see.models.statistic.domain.SymbolBaseStatisticM;

import java.math.BigDecimal;

/**
 * @author yyy
 */
@FunctionalInterface
public interface ChangeEventFunc {


	ChangeEventInfo changeEvent(BigDecimal curVal, SymbolBaseStatisticM symbolBaseStatisticM);
}
