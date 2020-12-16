package com.mutool.box.plugin.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/12/3 20:12<br>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompareAnalyseResult {

    /** 文件完整路径名称 */
    private String fileName;

    /** 目标sql有但是源sql中没有的sql */
    private List<LineModel> sourceLineModelList;

    /** 源sql有但是目标sql没有的sql */
    private List<LineModel> targetLineModelList;

    /** 重复sql */
    private List<LineModel> duplicateLineModelList;

}
