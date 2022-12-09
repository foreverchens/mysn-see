package com.ychen.see.models.statistic.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yyy
 */
@Data
@Builder
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


	@Override
	public String toString() {
		return "SymbolBaseStatisticM{" +
				"symbol='" + symbol + '\'' +
				", day3HighV=" + day3HighV +
				", day3LowV=" + day3LowV +
				", day3AvgV=" + day3AvgV +
				", day7HighV=" + day7HighV +
				", day7LowV=" + day7LowV +
				", day7AvgV=" + day7AvgV +
				", day3Amplitude=" + day3Amplitude +
				", day7Amplitude=" + day7Amplitude +
				", day15Amplitude=" + day15Amplitude +
				'}';
	}
}
