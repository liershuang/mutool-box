package com.mutool.box.plugin.db.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.mutool.box.plugin.db.model.CompareAnalyseResult;
import com.mutool.box.plugin.db.model.CompareResult;
import com.mutool.box.plugin.db.model.LineModel;
import com.mutool.box.plugin.db.model.SqlModel;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/12/2 23:06<br>
 */
public class SqlAnalyseUtil {

    /**
     * 思路：
     * 逐行读取，遇到分号隔开重新读取
     *
     * 读取sql文件：文件名、行号、行文本、每行单词列表
     * 分号分割，整条sql去空格，记录sql占用的行号及对应的文本
     * 比较原则：
     * 1、整条sql去空格后在目标列表中对比，若不完全相等则视为有差别
     * 2、逐个比较sql字段的不同，顺序比较，不同则记录
     *
     * 使用对象：
     * 行对象：行号、行文本、List<行单词>
     * 整条sql对象：文件名、去空sql、list<行对象>
     */

    /**
     * 1、首先展示源sql和目标sql区别
     * 2、根据文件名分组，同文件sql对比，按照行号依次展示
     * 3、相似分析，源和目标sql相似分析，相似sql展示在分析目录
     */
    public static List<CompareAnalyseResult> analyseCompareResult(String sourceDirPath, String targetDirPath){
        CompareResult cmopareResult = compare(sourceDirPath, targetDirPath);
        List<SqlModel> dupList = cmopareResult.getDuplicateSqlModelList();
        List<SqlModel> sourceLostList = cmopareResult.getSourceLostSqlModelList();
        List<SqlModel> targetLostList = cmopareResult.getTargetLostSqlModelList();
        Map<String, List<SqlModel>> sourceFileMap = sourceLostList.stream().collect(Collectors.groupingBy(i -> i.getFileName()));
        Map<String, List<SqlModel>> targetFileMap = targetLostList.stream().collect(Collectors.groupingBy(i -> i.getFileName()));

        Collection<String> fileList = CollUtil.union(sourceFileMap.keySet(), targetFileMap.keySet());
        List<CompareAnalyseResult> resultList = new ArrayList<>();
        fileList.forEach(fileName -> {
            CompareAnalyseResult compareResult = new CompareAnalyseResult();
            compareResult.setFileName(fileName);

            //重复数据多于已有数据
            List<SqlModel> currentDupList = dupList.stream().filter(i -> i.getFileName().equals(fileName)).collect(Collectors.toList());
            List<LineModel> dupLineModelList = new ArrayList<>();
            currentDupList.forEach(i -> dupLineModelList.addAll(i.getLineModelList()));
            compareResult.setDuplicateLineModelList(dupLineModelList);

            List<SqlModel> sourceList = sourceFileMap.get(fileName);
            List<LineModel> sortSourceList = new ArrayList<>();
            if(CollUtil.isNotEmpty(sourceList)){
                List<LineModel> sourceLineList = new ArrayList<>();
                sourceList.forEach(i -> sourceLineList.addAll(i.getLineModelList()));
                sortSourceList = sourceLineList.stream().sorted(Comparator.comparingInt(LineModel::getLineNum)).collect(Collectors.toList());
            }
            compareResult.setSourceLineModelList(sortSourceList);

            List<SqlModel> targetList = targetFileMap.get(fileName);
            List<LineModel> sortTargetList = new ArrayList<>();
            if(CollUtil.isNotEmpty(targetList)){
                List<LineModel> targetLineList = new ArrayList<>();
                targetList.forEach(i -> targetLineList.addAll(i.getLineModelList()));
                sortTargetList = targetLineList.stream().sorted(Comparator.comparingInt(LineModel::getLineNum)).collect(Collectors.toList());
            }
            compareResult.setTargetLineModelList(sortTargetList);

            resultList.add(compareResult);
        });
        dupList.removeIf(i -> fileList.contains(i.getFileName()));
        Map<String, List<SqlModel>> dupMap = dupList.stream().collect(Collectors.groupingBy(SqlModel::getFileName));
        dupMap.forEach((k, v) -> {
            CompareAnalyseResult compareResult = new CompareAnalyseResult();
            compareResult.setFileName(k);
            List<LineModel> dupLineModelList = new ArrayList<>();
            v.forEach(i -> dupLineModelList.addAll(i.getLineModelList()));
            compareResult.setDuplicateLineModelList(dupLineModelList);

            compareResult.setTargetLineModelList(Collections.emptyList());
            compareResult.setSourceLineModelList(Collections.emptyList());
            resultList.add(compareResult);
        });

        return resultList;
    }

    /**
     * 对比文件sql
     * @param sourceDirPath 源sql文件路径
     * @param targetDirPath 对比目标sql文件路径
     */
    public static CompareResult compare(String sourceDirPath, String targetDirPath){
        List<File> sourceFileList = FileUtil.loopFiles(sourceDirPath, i -> i.isDirectory() || (i.isFile()&&i.getName().endsWith(".sql")));
        if(FileUtil.isFile(sourceDirPath)){
            sourceFileList = ListUtil.toList(FileUtil.newFile(sourceDirPath));
        }
        List<File> targetFileList = FileUtil.loopFiles(targetDirPath, i -> i.isDirectory() || (i.isFile()&&i.getName().endsWith(".sql")));
        if(FileUtil.isFile(targetDirPath)){
            targetFileList = ListUtil.toList(FileUtil.newFile(targetDirPath));
        }

        List<SqlModel> sourceSqlModelList = new ArrayList<>();
        sourceFileList.forEach(i -> {
            sourceSqlModelList.addAll(getFileSqlModelList(i));
        });
        List<SqlModel> sourceDupList = getDuplicate(sourceSqlModelList);
        //同文件sql重复通过行号区分，同行sql重复直接去除
        Map<String, SqlModel> sourceMap = sourceSqlModelList.stream().collect(Collectors.toMap(i -> i.getTrimSql(), i -> i, (oldVlaue, newValue) -> newValue));
        ArrayList<String> sourceSqlList = ListUtil.toList(sourceMap.keySet());

        List<SqlModel> targetSqlModelList = new ArrayList<>();
        targetFileList.forEach(i -> {
            targetSqlModelList.addAll(getFileSqlModelList(i));
        });
        List<SqlModel> targetDupList = getDuplicate(targetSqlModelList);
        Map<String, SqlModel> targetMap = targetSqlModelList.stream().collect(Collectors.toMap(i -> i.getTrimSql(), i -> i, (oldVlaue, newValue) -> newValue));
        ArrayList<String> targetSqlList = ListUtil.toList(targetMap.keySet());

        //源sql有但是目标sql没有的sql
        Collection<String> targetLostList = CollUtil.subtract(sourceSqlList, targetSqlList);
        //目标sql有但是源sql中没有的sql
        Collection<String> sourceLostList = CollUtil.subtract(targetSqlList, sourceSqlList);

        List<SqlModel> targetLostSqlModelList = targetLostList.stream().map(i -> sourceMap.get(i)).collect(Collectors.toList());
        List<SqlModel> sourceLostSqlModelList = sourceLostList.stream().map(i -> targetMap.get(i)).collect(Collectors.toList());

        return CompareResult.builder().sourceLostSqlModelList(sourceLostSqlModelList)
                .targetLostSqlModelList(targetLostSqlModelList)
                .duplicateSqlModelList(ListUtil.toList(CollUtil.union(sourceDupList, targetDupList)))
                .build();
    }

    public static List<SqlModel> getDuplicate(List<SqlModel> sqlModelList){
        Set<String> items = new HashSet<>();
        Set<String> dupSet = sqlModelList.stream().filter(i -> !items.add(i.getTrimSql()))
                .map(i -> i.getTrimSql()).collect(Collectors.toSet());
//        Set<String> dupTrimSqlList = dupSet.stream().map(i -> i.getTrimSql()).collect(Collectors.toSet());
//        sqlModelList.removeIf(i -> dupTrimSqlList.contains(i.getTrimSql()));

        return sqlModelList.stream().filter(i -> dupSet.contains(i.getTrimSql())).collect(Collectors.toList());
    }


    /**
     * 解析文件sql对象列表
     * @param file
     * @return
     */
    public static List<SqlModel> getFileSqlModelList(File file){
        String fileName = file.getAbsolutePath();
        List<SqlModel> sqlModelList = new ArrayList<>();

        List<String> lineContentList = FileUtil.readLines(file, CharsetUtil.CHARSET_UTF_8);
        AtomicInteger lineNum = new AtomicInteger(1);

        SqlModel sqlModel = new SqlModel();
        sqlModel.setFileName(fileName);

        List<LineModel> lineModelList = new ArrayList<>();

        for(int i=0; i<lineContentList.size(); i++){
            String currentLine = lineContentList.get(i);
            if(StrUtil.isAllBlank(currentLine)){
                lineNum.getAndIncrement();
                continue;
            }

            if(StrUtil.contains(currentLine, ";")){
                ArrayList<String> linePartList = ListUtil.toList(StrUtil.splitTrim(currentLine, ";"));
                //处理首行sql，首行sql表示整条sql结束
                String firstLineContent = CollUtil.getFirst(linePartList);
                LineModel lineModel = getLineModel(lineNum.get(), firstLineContent);
                lineModelList.add(lineModel);
                sqlModel.setLineModelList(lineModelList);
                List<String> contentList = sqlModel.getLineModelList().stream()
                        .map(model -> model.getLineContent()).collect(Collectors.toList());
                sqlModel.setTrimSql(StrUtil.cleanBlank(CollUtil.join(contentList, "")));
                sqlModelList.add(sqlModel);
                sqlModel = new SqlModel(fileName);
                lineModelList = new ArrayList<>();

                //处理都在一行的sql
                if(linePartList.size() > 2){
                    List<String> singleLineSqlList = linePartList.subList(1, linePartList.size() - 2);
                    singleLineSqlList.forEach(singleLine -> {
                        sqlModelList.add(getSingleLineSqlModel(fileName, lineNum.get(), singleLine));
                    });
                }
                if(linePartList.size() > 1){
                    //最后sql继续和下行sql拼接
                    String lastLineContent = CollUtil.getLast(linePartList);
                    LineModel lastLineModel = getLineModel(lineNum.get(), lastLineContent);
                    lineModelList.add(lastLineModel);
                }
            }else{
                LineModel lineModel = getLineModel(lineNum.get(), currentLine);
                lineModelList.add(lineModel);
            }
            lineNum.getAndIncrement();
        }

        //最后一行处理
        if(CollUtil.isNotEmpty(lineModelList)){
            sqlModel.setLineModelList(lineModelList);
            List<String> contentList = sqlModel.getLineModelList().stream()
                    .map(model -> model.getLineContent()).collect(Collectors.toList());
            sqlModel.setTrimSql(StrUtil.cleanBlank(CollUtil.join(contentList, "")));
            sqlModelList.add(sqlModel);
        }

        return sqlModelList;
    }

    /**
     * 获取单行sql对象
     * @param fileName
     * @param lineNum
     * @param lineContent
     * @return
     */
    private static SqlModel getSingleLineSqlModel(String fileName, int lineNum, String lineContent){
        SqlModel sqlModel = new SqlModel();
        sqlModel.setFileName(fileName);
        sqlModel.setTrimSql(StrUtil.cleanBlank(lineContent));
        sqlModel.setLineModelList(ListUtil.toList(getLineModel(lineNum, lineContent)));
        return sqlModel;
    }

    /**
     * 获取行对象
     * @param lineNum
     * @param lineContent
     * @return
     */
    private static LineModel getLineModel(int lineNum, String lineContent){
        LineModel lineModel = new LineModel();
        lineModel.setLineNum(lineNum);
        lineModel.setLineContent(lineContent);
        lineModel.setLineWordList(StrUtil.split(lineContent, ' ', true, true));
        return lineModel;
    }



}
