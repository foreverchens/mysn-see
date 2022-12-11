package com.ychen.see.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yyy
 */
@Slf4j
@Data
@Component
public class SwitchConfig {

	@Value("${see.config.oi:true}")
	private Boolean oi;
	@Value("${see.config.accRatio:false}")
	private Boolean accRatio;
	@Value("${see.config.topOiRatio:true}")
	private Boolean topOiRatio;
	@Value("${see.config.kline:false}")
	private Boolean kline;
}
