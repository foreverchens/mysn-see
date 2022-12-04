package com.ychen.see.info.controller;

import com.ychen.see.models.info.controller.InfoController;
import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author yyy
 * @wx ychen5325
 * @email yangyouyuhd@163.com
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class InfoControllerTest {

    @Resource
    private InfoController infoController;

    @Test
    public void pingTest(){
        System.out.println(infoController.ping());
    }

}
