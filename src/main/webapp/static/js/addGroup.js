$(function () {

    /** 初始化方法 */
    var init = function () {
        $.ajax({
            url: webProjectName() + "/chatGroup/selectUserList",
            type: 'post',
            data:{},
            success: function (data, status) {
                var obj = eval ("(" + data + ")");
                console.log("发起群聊返回值【"+obj+"】");
                $("#user-list").html("");

                var html = "";
                $.each(obj, function(idx,o) {
                    html += "<input type='checkbox' name='groupInfo' value='"+o.id+"@@"+o.name+"' /> <span>"+o.name+"</span><br/>";
                });
                console.log(html);
                $("#user-list").html(html);
            },
            error: function (data, status, e) {
                console.log("发起群聊出现异常."+e);
            }
        });
    };

    init();

});