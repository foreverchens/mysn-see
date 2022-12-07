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
public class SymbolBaseStatisticM {

	private String symbol;


	private BigDecimal day3HighV;

	private BigDecimal day3LowV;

	private BigDecimal day3AvgV;

	private BigDecimal day7HighV;

	private BigDecimal day7LowV;

	private BigDecimal day7AvgV;

	private BigDecimal day3Amplitude;

	private BigDecimal day7Amplitude;

	private BigDecimal day15Amplitude;

}
