<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>mutool插件-sql对比工具</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../layui/css/layui.css" media="all">

    <!-- VConsole调试 -->
    <script src="https://cdn.bootcss.com/vConsole/3.3.4/vconsole.min.js"></script>
    <script>
        // 初始化
        var vConsole = new VConsole();
        // console.log('Hello world');
    </script>
</head>
<body>

<div class="layui-form">

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;">
        <legend>sql路径设置</legend>
    </fieldset>
    <div class="layui-form layui-form-pane">
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">源sql路径</label>
                <div class="layui-input-inline" style="width: 350px;">
                    <input id="sourceDirPath" type="text" name="number" autocomplete="off" class="layui-input">
                </div>
                <label class="layui-form-label">目标sql路径</label>
                <div class="layui-input-inline" style="width: 350px;">
                    <input id="targetDirPath" type="text" name="number" autocomplete="off" class="layui-input">
                </div>
                <button type="button" class="layui-btn" id="startCompare">分析</button>
            </div>
        </div>
    </div>

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;">
        <legend>对比数据</legend>
    </fieldset>

    <table class="layui-table">
        <colgroup>
            <col width="33%">
            <col width="33%">
            <col>
        </colgroup>
        <thead>
        <tr>
            <th>源文件缺失sql</th>
            <th>目标文件缺失sql</th>
            <th>重复sql</th>
        </tr>
        </thead>
        <tbody id="tableData">
        <tr>
            <td>贤心</td>
            <td>汉族</td>
            <#--<td>1989-10-14</td>-->
            <#--<td>人生似修行</td>-->
            <td colspan="2">1989-10-14 人生似修行</td>
        </tr>
        <tr>
            <td>张爱玲</td>
            <td>汉族</td>
            <td>1920-09-30</td>
            <td>于千万人之中遇见你所遇见的人，于千万年之中，时间的无涯的荒野里…</td>
        </tr>
        </tbody>
    </table>
</div>

<script src="../js/jquery-3.5.1.min.js" charset="utf-8"></script>
<script src="../layui/layui.js" charset="utf-8"></script>

<script>
    //服务地址
    var serverUrl = "http://127.0.0.1:9081";

    //开始分析
    $("#startCompare").click(function(){
        var sourceDirPath = $("#sourceDirPath").val();
        var targetDirPath = $("#targetDirPath").val();
        $.post(serverUrl+"/db/compareSql", {"sourceDirPath":sourceDirPath, "targetDirPath":targetDirPath}, function(data,status){
            // if(data.code != "200"){
            //
            //     return;
            // }
            drawTable(data);
        });
    });

    function drawTable(data){
        var tableHtml = '';
        data.forEach((item, index, array)=>{
            tableHtml +=
                '<tr>' +
                '    <td>文件名</td>' +
                '    <td colspan="2">'+item.fileName+'</td>' +
                '</tr>';

            var sourceLostTxt = '';
            item.sourceLineModelList.forEach((lineModel, sourceIndex,  sourceArray)=>{
                sourceLostTxt += "行号：" + lineModel.lineNum + "&nbsp;&nbsp;&nbsp;"+lineModel.lineContent + "<br/>";
            });
            var targetLostTxt = '';
            item.targetLineModelList.forEach((lineModel, targetIndex,  targetArray)=>{
                targetLostTxt += "行号：" + lineModel.lineNum + "&nbsp;&nbsp;&nbsp;"+lineModel.lineContent +"<br/>";
            });

            var dupTxt = '';
            item.duplicateLineModelList.forEach((lineModel, targetIndex,  targetArray)=>{
                    dupTxt += "行号：" + lineModel.lineNum + "&nbsp;&nbsp;&nbsp;"+lineModel.lineContent +"<br/>";
            });
            tableHtml +=
                '<tr>' +
                '    <td>'+sourceLostTxt+'</td>' +
                '    <td>'+targetLostTxt+'</td>' +
                '    <td>'+dupTxt+'</td>' +
                '</tr>';
        });
        $("#tableData").html(tableHtml);
    }

</script>


</body>
</html>