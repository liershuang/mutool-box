package com.mutool.box.plugin.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述：sql对象<br>
 * 作者：les<br>
 * 日期：2020/12/2 23:34<br>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlModel {

    /**
     * sql所在文件名
     */
    private String fileName;

    /**
     * 去除空格后的sql
     */
    private String trimSql;

    /**
     * sql包含的行对象
     */
    private List<LineModel> lineModelList;

    public SqlModel(String fileName){
        this.fileName = fileName;
    }

}
