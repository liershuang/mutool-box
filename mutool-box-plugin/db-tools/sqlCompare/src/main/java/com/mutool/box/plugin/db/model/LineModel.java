package com.mutool.box.plugin.db.model;

import lombok.Data;

import java.util.List;

/**
 * 描述：行数据对象<br>
 * 作者：les<br>
 * 日期：2020/12/2 23:31<br>
 */
@Data
public class LineModel {
    /**
     * 行号
     */
    private int lineNum;
    /**
     * 行文本
     */
    private String lineContent;
    /**
     * 行单词列表
     */
    private List<String> lineWordList;


}
