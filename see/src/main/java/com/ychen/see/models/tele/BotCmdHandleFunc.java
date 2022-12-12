package com.ychen.see.models.tele;

/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
@FunctionalInterface
public interface BotCmdHandleFunc {

	String handle(String cmd);
}
