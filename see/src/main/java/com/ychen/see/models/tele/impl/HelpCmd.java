package com.ychen.see.models.tele.impl;/**
 *
 */

import com.ychen.see.models.tele.BotCmdHandleFunc;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
@Slf4j
@Component("/help")
public class HelpCmd implements BotCmdHandleFunc {
	@Override
	public String handle(String cmd) {
		return "这里是/help的处理类";
	}
}
