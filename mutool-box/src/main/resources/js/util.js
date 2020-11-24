/**
 * 字符串转json
 * @param str
 * @returns {Object}
 */
function strToJson(str){
    var json = eval('(' + str + ')');
    return json;
}

/**
 * 获取字符串长度（中文算两个）
 * @param val
 * @returns {number}
 */
function getStrLength(val) {
    var len = 0;
    for (var i = 0; i < val.length; i++) {
        var a = val.charAt(i);
        if (a.match(/[^\x00-\xff]/ig) != null) {
            len += 2;
        } else {
            len += 1;
        }
    }
    return len;
}

/**
 * 字符串长度超长截断点点显示
 * @param str
 * @returns {string}
 */
function limitStr(str, limitLen){
    if(getStrLength(str) > limitLen){
        return str.substr(0, limitLen)+"...";
    }
    return str;
}
