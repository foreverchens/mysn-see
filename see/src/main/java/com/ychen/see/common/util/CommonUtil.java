package com.ychen.see.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author yyy
 */
public class CommonUtil {

	/**
	 * 1k = 1000
	 */
	private static final BigDecimal k = BigDecimal.valueOf(1000);

	/**
	 * 1m = 100 0000
	 */
	private static final BigDecimal m = BigDecimal.valueOf(100 * 10000);

	public static String numConvert(BigDecimal num) {
		if (Objects.isNull(num)) {
			return null;
		}
		if (m.compareTo(num) < 1) {
			return num.divide(m, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN).toEngineeringString().concat("m");
		}
		if (k.compareTo(num) < 1) {
			return num.divide(k, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN).toEngineeringString().concat("k");
		}
		return num.toEngineeringString();
	}
}
