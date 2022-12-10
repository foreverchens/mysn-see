package com.ychen.see;

import com.ychen.see.common.util.SpringContextUtil;
import com.ychen.see.models.schedule.TaskManage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yyy
 */
@Slf4j
@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
		log.info("-----------------MySN-SEE SUC-----------------");
	}
}
