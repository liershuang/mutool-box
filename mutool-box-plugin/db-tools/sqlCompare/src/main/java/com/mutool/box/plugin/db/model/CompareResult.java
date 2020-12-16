package com.mutool.box.plugin.db.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/12/3 20:12<br>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompareResult {

    /** 目标sql有但是源sql中没有的sql */
    private List<SqlModel> sourceLostSqlModelList;
    /** 源sql有但是目标sql没有的sql */
    private List<SqlModel> targetLostSqlModelList;
    /** 重复sql */
    private List<SqlModel> duplicateSqlModelList;

}
