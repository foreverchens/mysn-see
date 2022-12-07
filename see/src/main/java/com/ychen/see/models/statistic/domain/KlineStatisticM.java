package com.ychen.see.models.statistic.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author yyy
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KlineStatisticM extends BaseStatisticM {

	private BigDecimal shakeVal;
}
