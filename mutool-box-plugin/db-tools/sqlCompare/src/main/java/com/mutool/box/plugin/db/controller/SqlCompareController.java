package com.mutool.box.plugin.db.controller;

import com.mutool.box.plugin.db.model.CompareAnalyseResult;
import com.mutool.box.plugin.db.util.SqlAnalyseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/12/4 09:23<br>
 */
@Controller
@RequestMapping("db")
public class SqlCompareController {

    @RequestMapping(value="viewSqlComparePage")
    public String viewPluginPage(Model model){
        return "sqlCompare";
    }

    @ResponseBody
    @RequestMapping("compareSql")
    public List<CompareAnalyseResult> compareSql(String sourceDirPath, String targetDirPath){
        return SqlAnalyseUtil.analyseCompareResult(sourceDirPath, targetDirPath);
    }


}
