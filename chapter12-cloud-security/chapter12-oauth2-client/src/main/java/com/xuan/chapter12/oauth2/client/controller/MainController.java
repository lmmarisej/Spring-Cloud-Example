package com.xuan.chapter12.oauth2.client.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * Created by xuan on 2018/4/10.
 */

@RestController
public class MainController {

    @RequestMapping(method = RequestMethod.GET)
    public String main(HttpServletResponse response) throws IOException {
        response.sendRedirect("/index");
        return "";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "Welcome to the index! <a href='/user'>Visit userinfo</a>";
    }

    @RequestMapping(value = {"/user"}, method = RequestMethod.GET)
    public Principal principal(Principal user) {
        return user;
    }
}
