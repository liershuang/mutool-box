<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>mutool菜单</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../layui/css/layui.css"  media="all">
</head>
<body>

<ul class="layui-nav layui-nav-tree layui-bg-cyan layui-inline" lay-filter="demo" id="menuDiv">
    暂无数据
</ul>

<div id="menuContent"></div>

<script src="../js/jquery-3.5.1.min.js" charset="utf-8"></script>
<script src="../layui/layui.js" charset="utf-8"></script>
<script>document.write('<script src="../js/util.js?t='+new Date().getTime() + '" charset="utf-8"><\/script>')</script>
<script>

    layui.use('element', function(){
        //导航的hover效果、二级菜单等功能，需要依赖element模块
        var element = layui.element;
    });

    //加载菜单数据要同步的方式，保证渲染页面在layui之前执行
    $.ajax({
        type : "post",
        url : "${systemConfig.serverDoamin}:${systemConfig.serverPort}/index/getMenuTree",
        async : false,
        success : function(data){
            if(data.code != "200"){
                layer.open({title: '提示', content: data.msg, time:1500});
                return;
            }
            var resulMenutHtml = drawMenuList(data.data);
            $("#menuDiv").html(resulMenutHtml);
        }
    });

    //组织菜单列表
    function drawMenuList(menuList){
        if(menuList == null || menuList.length == 0){
            return;
        }
        var menuHtml = '';
        $.each(menuList, function(index, value){
            if(value.childMenuList.length > 0){
                menuHtml +=
                    '<li class="layui-nav-item">' +
                    '    <a href="javascript:;">'+value.menuName+'</a>' +
                    '    <dl class="layui-nav-child">';

                $.each(value.childMenuList, function(childIndex, childValue){
                    if(childValue.childMenuList.length > 0){
                        menuHtml += drawMenuList(childValue.childMenuList);
                    }else{
                        menuHtml += '<dd><a href="javascript:;" onclick="openMenu(\''+childValue.menuType+'\',\''+childValue.url+'\');">'+childValue.menuName+'</a></dd>';
                    }
                });
                menuHtml +=
                    '    </dl>' +
                    '</li>';
            }else{
                menuHtml += '<li class="layui-nav-item"><a href="javascript:;" onclick="openMenu(\''+value.menuType+'\',\''+value.url+'\');">'+value.menuName+'</a></li>';
            }
        });
        return menuHtml;
    }

    //打开插件页面
    function openMenu(menuType, pageUrl){
        if(menuType == "Node"){
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.open({
                    title: '提示',
                    content: "暂不支持页面打开，请到客户端查看"
                });
            });
        }
        if(menuType == "WebView"){
            $.get("${systemConfig.serverDoamin}:${systemConfig.serverPort}"+pageUrl, function(result){
                if(result.code != "200"){
                    layer.open({title: '提示', content: result.msg, time:1500});
                    return;
                }
                $("#menuContent").html(result.data);
            });
        }
    }

</script>

</body>
</html>