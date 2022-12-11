package com.ychen.see;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
public class MainTest {
	public static void main(String[] args) {
		System.out.println(DateUtil.offsetDay(new Date(), -2).getTime());
	}
}
