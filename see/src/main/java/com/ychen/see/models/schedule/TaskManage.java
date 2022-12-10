package com.ychen.see.models.schedule;

import com.ychen.see.models.schedule.server.ContractDataStatisticAndAnalyzeService;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author yyy
 */
@Slf4j
// @Component
public class TaskManage {


	@Resource
	private ContractDataStatisticAndAnalyzeService analyzeService;

	@Scheduled(cron = "0 */5 * * * ?")
	public void contractDataAnalyze() {
		analyzeService.exe();
	}


}
