package com.mutool.box.controller;

import com.mutool.box.model.PluginJarInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/11/24 13:14<br>
 */
@Controller
public class TestController {

    @ResponseBody
    @PostMapping("test")
    public String test(){
        return "this is test";
    }

    @RequestMapping("testpage")
    public String gettest(){
        return "/html/testpage.html";
    }

}
