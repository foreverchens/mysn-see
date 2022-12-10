package com.ychen.see.models.event.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

	private String eventTime;

}
