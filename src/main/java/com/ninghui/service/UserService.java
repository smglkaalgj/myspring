package com.ninghui.service;

import com.mysping.Autowired;
import com.mysping.Component;
import com.mysping.Scope;

@Component("userService")
@Scope("prototype")
public class UserService {

    @Autowired
    private OrderService orderService;

    private String a = "asd";
}
