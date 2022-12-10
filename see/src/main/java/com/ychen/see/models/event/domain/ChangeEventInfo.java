package com.ychen.see.models.event.domain;

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
public class ChangeEventInfo {
	private String symbol;

	private String dataType;

	private String period;

	/**
	 * 数值位置、[高位,低位]
	 */
	private String location;

	private BigDecimal lowV;

	private BigDecimal highV;

	private BigDecimal curV;

	private String eventTime;

}
