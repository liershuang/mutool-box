package com.mutool.box.controller;

import com.mutool.box.model.MenuConfig;
import com.mutool.box.services.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 描述：首页控制器<br>
 * 作者：les<br>
 * 日期：2020/11/25 14:17<br>
 */
@Controller
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @RequestMapping("viewMenu")
    public String viewMenu(Model model){
        model.addAttribute("menuTree", indexService.getMenuTree());
        return "menu";
    }

    @ResponseBody
    @PostMapping("getMenuTree")
    public List<MenuConfig> getMenuTree(){
        return indexService.getMenuTree();
    }



}
