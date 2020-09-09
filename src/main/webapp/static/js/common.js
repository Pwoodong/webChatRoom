/** 判断对象是否为空 */
function isEmpty(obj) {
    if (typeof obj == "undefined" || obj == null || obj == "") {
        return true;
    }
    return false;
}

/** 获取URL的传参 */
function getUrlParam(url,name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = url.match(reg);
    if (r != null)
        return decodeURI(r[2]);
    return null;
}

/** 获取项目名称，此处可根据浏览器地址动态获取 */
function webProjectName() {
    return "/webChatRoom";
}