package com.mutool.box.advice;

import com.mutool.box.config.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/11/28 14:43<br>
 */
@ControllerAdvice
public class MyControllerAdvice {

    @Autowired
    private SystemConfig systemConfig;

    /**
     * 获取公共基础数据
     * @return
     */
    @ModelAttribute(name = "baseAttr")
    public Map<String,Object> getBaseAttr() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("serverDoamin", systemConfig.serverDoamin);
        map.put("serverPort", systemConfig.serverPort);
        return map;
    }

}
