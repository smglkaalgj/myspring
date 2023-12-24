package com.ninghui;

import com.mysping.MyApplicationContext;
import com.ninghui.service.UserService;


public class Test {
    public static void main(String[] args) {

        MyApplicationContext context = new MyApplicationContext(AppConfig.class);
        UserService userService = (UserService) context.getBean("userService");
        UserService userService1 = (UserService) context.getBean("userService");
        System.out.println(userService);
        System.out.println(userService1);
    }

}
