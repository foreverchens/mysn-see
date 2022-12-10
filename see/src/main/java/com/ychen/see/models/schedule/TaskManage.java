package com.ychen.see.models.schedule;

import com.ychen.see.models.schedule.server.ContractDataStatisticAndAnalyzeService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author yyy
 */
@Slf4j
@Component
public class TaskManage implements CommandLineRunner {


	@Resource
	private ContractDataStatisticAndAnalyzeService analyzeService;

	// @Scheduled(cron = "0 */5 * * * ?")
	public void contractDataAnalyze() {
		analyzeService.exe();
	}


	@Override
	public void run(String... args) throws Exception {
		contractDataAnalyze();
	}
}
