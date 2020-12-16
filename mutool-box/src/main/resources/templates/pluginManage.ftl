<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>mutool插件管理</title>
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

<div class="layui-form layui-form-pane" style="padding-top:10px; float:right;">
    <div class="layui-form-item">
        <div class="layui-input-inline">
            <input type="text" id="searchKeyWord" name="username" lay-verify="required" placeholder="请输入" autocomplete="off" class="layui-input">
        </div>
        <label class="layui-form-label" id="searchPlugin">搜索</label>
    </div>
</div>

<div>
    <div id="errorMessage"></div>
    <div id="scriptURI"></div>
    <div id="lineNumber"></div>
    <div id="columnNumber"></div>
    <div id="errorObj"></div>
</div>

<div style="padding: 10px; background-color: #F2F2F2;">
    <div class="layui-row layui-col-space15" id="pluginDiv">
        <#if pluginList?size gt 0>
            <#list pluginList as plugin>
                <div class="layui-col-md3">
                    <div class="layui-card">
                        <div class="layui-card-header" style="height: 80px;">
                            <img src="../images/icon.jpg" width="25" height="25"/>
                            <span>${plugin.name!""}</span>
                            <i class="layui-icon layui-icon-tips" style="font-size: 23px; color: #FFB800; float:right;" onclick="showIntroduce('${plugin.synopsis}')"></i>
                            <br/>
                            <#if plugin.isDownload>
                                <button type="button" class="layui-btn layui-btn-xs layui-btn-disabled">下载</button>
                                <button type="button" class="layui-btn layui-btn-xs" onclick="deletePlugin('${plugin.jarName}')">更新</button>
                                <button type="button" class="layui-btn layui-btn-xs" onclick="deletePlugin('${plugin.jarName}')">删除</button>
                            <#else>
                                <button type="button" class="layui-btn layui-btn-xs" onclick="downloadPlugin('${plugin.jarName}')">下载</button>
                                <button type="button" class="layui-btn layui-btn-xs layui-btn-disabled">更新</button>
                                <button type="button" class="layui-btn layui-btn-xs layui-btn-disabled">删除</button>
                            </#if>
                        </div>
                        <div class="layui-card-body" title="${plugin.synopsis}" style="height: 50px;">
                            <#if plugin.synopsis?length gt 30>
                                ${plugin.synopsis?substring(0, 30)}...
                            <#else>
                                ${plugin.synopsis!''}
                            </#if>
                        </div>
                    </div>
                </div>
            </#list>
        <#else>
            暂无数据
        </#if>
    </div>
</div>

<script src="../js/jquery-3.5.1.min.js" charset="utf-8"></script>
<script src="../layui/layui.js" charset="utf-8"></script>
<script>document.write('<script src="../js/util.js?t='+new Date().getTime() + '" charset="utf-8"><\/script>')</script>

<script>

    //页面进入渲染插件列表
    /*$.post("/plugin/getPluginList",function(data,status){
        var businData = getBusinData(data);
        drawPluginList(businData);
    });*/
    //服务地址
    var serverUrl = "${systemConfig.serverDoamin}:${systemConfig.serverPort}";

    //回车搜索
    $("#searchKeyWord").keyup(function(event){
        if(event.keyCode ==13){
            $("#searchPlugin").trigger("click");
        }
    });

    //搜索
    $("#searchPlugin").click(function(){
        var searchKeyWord = $("#searchKeyWord").val();
        $.post(serverUrl+"/plugin/searchPlugin",{"keyword":searchKeyWord}, function(data,status){
            if(data.code != "200"){
                layer.open({title: '提示', content: data.msg, time:1500});
                return;
            }
            drawPluginList(data.data);
        });
    });

    //显示简介
    function showIntroduce(str){
        layui.use('layer', function(){
            var layer = layui.layer;
            layer.open({
                title: '插件简介',
                content: str
            });
        });
    }

    //下载插件
    function downloadPlugin(jarName){
        layui.use('layer', function(){
            var layer = layui.layer;
            var index = layer.load();
            $.post(serverUrl+"/plugin/downloadPlugin",{"jarName":jarName}, function(data,status){
                layer.close(index);
                if(data.code != "200"){
                    layer.open({title: '提示', content: data.msg, time:1500});
                    return;
                }
                layer.open({title: '提示', content: "下载成功", time:1500});
            });
        });
    }

    //删除插件
    function deletePlugin(jarName){
        layui.use('layer', function(){
            var layer = layui.layer;
            var index = layer.load();
            $.post(serverUrl+"/plugin/deletePlugin",{"jarName":jarName}, function(data,status){
                layer.close(index);
                if(data.code != "200"){
                    layer.open({title: '提示', content: data.msg, time:1500});
                    return;
                }
                layer.open({title: '提示', content: "删除成功", time:1500});
            });
        });
    }

    //渲染插件列表
    function drawPluginList(pluginJson){
        var pluginHtml = "";
        pluginJson.forEach((item,index,array)=>{
            var synopsis = item.synopsis;
            var jarName = item.jarName;
            var shortStr = limitStr(item.synopsis, 30);
            var currentHtml =
                '<div class="layui-col-md3">' +
                '    <div class="layui-card">' +
                '        <div class="layui-card-header" style="height: 80px;">' +
                '            <img src="../images/icon.jpg" width="25" height="25"/>' +
                '            <span>'+item.name+'</span>' +
                '            <i class="layui-icon layui-icon-tips" style="font-size: 23px; color: #FFB800; float:right;" onclick="showIntroduce(\''+synopsis+'\')"></i>' +
                '            <br/>';
            if(item.isDownload){
                currentHtml += '            <button type="button" class="layui-btn layui-btn-xs layui-btn-disabled">下载</button>';
                currentHtml += '            <button type="button" class="layui-btn layui-btn-xs" onclick="deletePlugin(\''+jarName+'\')">删除</button>';
            }else{
                currentHtml += '            <button type="button" class="layui-btn layui-btn-xs" onclick="downloadPlugin(\''+jarName+'\')">下载</button>';
                currentHtml += '            <button type="button" class="layui-btn layui-btn-xs layui-btn-disabled">删除</button>';
            }

            currentHtml +=
                '        </div>' +
                '        <div class="layui-card-body" title="'+synopsis+'" style="height: 50px;">' +
                            shortStr +
                '        </div>' +
                '    </div>' +
                '</div>';
            pluginHtml = pluginHtml + currentHtml;
        });
        $("#pluginDiv").html(pluginHtml);
    }

</script>


</body>
</html>