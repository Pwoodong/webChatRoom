$(function () {

    getSum();

    getGroupInfo();

    connect();

    getFile();

    $("#addChatGroup").click(function () {
        layer.open({
            id: 'groupInfo',
            type: 2,
            title: '添加群聊信息',
            area: ['700px', '450px'],
            fixed: false,
            maxmin: true,
            content: 'static/addGroup.html',
            btn: ['确定', '取消'],
            yes: function (index, layero) {
                var userArray = [];
                var body = layer.getChildFrame('input[name="groupInfo"]:checked', index);
                $(body).each(function (index, element) {
                    var userInfo = {};
                    var groupValue = $(this).val();
                    userInfo.userId = groupValue.split("@@")[0];
                    userInfo.userName = groupValue.split("@@")[1];
                    userArray.push(userInfo);
                });
                console.log("获取选中的群聊联系人【" + userArray + "】");
                var postData = {
                    userInfo: JSON.stringify(userArray)
                };
                $.ajax({
                    url: webProjectName() + "/chatGroup/save",
                    type: 'post',
                    data: postData,
                    cache: false,
                    success: function (data) {
                        layer.close(index);
                        getGroupInfo();
                    }
                });
            }, btn2: function (index, layero) {
            }
        });
    });

});

function fileupload() {
    if ($("#cert").val() == "" || $("#cert").val() == null) {
        console.log("请上传文件");
        return;
    }
    $("#mask").css("height", $(document).height());
    $("#mask").css("width", $(document).width());
    $("#mask").show();
    $("#msg").show();

    var type = "file";
    var id = "cert";

    var groupId = document.getElementById('groupId').value;
    var userId = document.getElementById('userId').value;

    var formData = new FormData();
    formData.append(type, $("#" + id)[0].files[0]);
    formData.append("username", $("#username").val());
    formData.append("groupId", groupId);
    formData.append("userId", userId);
    $.ajax({
        type: "POST",
        url: webProjectName() + "/file/upload",
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
            console.log(data);
            ws.send(data);
            $("#cert").val("");
            $("#mask").hide();
            $("#msg").hide();
        }
    });
}

function getSum() {
    var groupId = document.getElementById('groupId').value;
    $.ajax({
        url: webProjectName() + "/chat/getSum",
        type: 'post',
        data: {groupId: groupId},
        success: function (data, status) {
            var obj = eval("(" + data + ")");
            $("#user-list").html("");
            var userliststr = "";
            for (var i = 0; i < obj.userList.length; i++) {
                var o = obj.userList[i];
                userliststr += "<p>[" + o.status + "]" + o.username + "</p>";
            }
            $("#user-list").html(userliststr);
            var temp = obj.size;
            $("#sum").html("在线人数：" + temp);
        },
        error: function (data, status, e) {
        }
    });
}

var ws = null;
var url = null;
var transports = [];
var now = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('echo').disabled = !connected;
    document.getElementById('username').disabled = connected;
}

function connect() {
    var host = window.location.host;
    var groupId = document.getElementById('groupId').value;
    var userId = document.getElementById('userId').value;
    var name = document.getElementById('username').value;
    if (!name || name.trim().length == 0) {
        alert('请输入昵称!')
        return;
    }
    if (name.length > 50) {
        alert('小样，昵称太长了!')
        return;
    }
    url = "http://" + window.location.host + webProjectName() + "/sockjs/echo?groupId=" + groupId + "&userId=" + userId + "&name=" + name;
    if (!url) {
        alert('Select whether to use W3C WebSocket or SockJS');
        return;
    }
    log(">>>>>>>>>>>>>>>欢迎进入聊天室<<<<<<<<<<<<<<<")
    ws = new SockJS(url, undefined, transports);

    ws.onopen = function () {
        setConnected(true);
        var username = document.getElementById('username').value;
        log('服务器连接成功!');
        getSum();
    };
    ws.onmessage = function (event) {
        if (event.data.toString().lastIndexOf("上线啦!") != -1 || event.data.toString().lastIndexOf("下线啦!") != -1) {
            getSum();
        }
        log(event.data);
    };
    ws.onclose = function (event) {
        setConnected(false);
        if (event.code == 1007) {
            log('小样，昵称太长了!');
        }
        log('断开连接!');
        setConnected(false);
    };
    document.getElementById('message').focus();
}

function disconnect() {
    if (ws != null) {
        ws.close();
        ws = null;
    }
    setConnected(false);
}

function echo() {
    if (ws != null) {
        var message = document.getElementById('message').value;
        if (message.trim().length == 0) {
            log('请输入内容!')
            return;
        }
        if (message.length > 100) {
            alert('太长了，明显是来刷屏的啊!');
            return;
        }
        ws.send(message);

        var groupId = document.getElementById('groupId').value;
        var userId = document.getElementById('userId').value;
        var username = document.getElementById('username').value;
        $.ajax({
            url: webProjectName() + "/chat/saveLog",
            type: 'post',
            data: {"groupId": groupId, "userId": userId, "name": username, "text": message},
            success: function (data, status) {

            }
        });
        document.getElementById('message').value = '';
    } else {
        alert('connection not established, please connect.');
    }
}

function log(message) {
    var s = message;
    var console = document.getElementById('console');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    var str = null;
    if (s.indexOf("resultMessageOldname") != -1) {
        var w = s.indexOf("{");
        var x = s.indexOf("}");
        str = s.substr(0, (w));
        var stright = s.substr(w, x)
        var f = JSON.parse(stright)

        if (message.indexOf('说：') > -1) {
            message = message.split('说：')[0];
        }
        var a1 = document.createElement('a');
        var text = document.createTextNode('' + f.resultMessageOldname);
        a1.appendChild(text);
        a1.setAttribute("href", "#");
        a1.setAttribute("onclick", "downloadFtpFile('" + f.resultFilePath + "','" + f.resultMessageOldname + "')");
        p.appendChild(document.createTextNode(str));
        p.appendChild(a1);
    } else {
        str = message;
        p.appendChild(document.createTextNode(str));
    }

    console.appendChild(p);
    while (console.childNodes.length > 35) {
        console.removeChild(console.firstChild);
    }
    console.scrollTop = console.scrollHeight;
}

function downloadFtpFile(localPath, originalName) {
    var url = webProjectName() + "/file/downloadFtpFile?localPath=" + localPath + "&originalName=" + originalName;
    url = encodeURI(encodeURI(url));
    window.open(url);
}

function getFile() {
    $.ajax({
        url: webProjectName() + "/chat/getFile",
        type: 'get',
        success: function (data, status) {
            var obj = eval("(" + data + ")");
            var userliststr = "";
            $("#file-list").html("");
            for (var i = 0; i < obj.list.length; i++) {
                var o = obj.list[i];
                userliststr += "<p><input name='files' type='checkbox' value='" + o.id + "' />" + o.text + " </p>";
            }
            userliststr += "<p><input type='button' onclick='checkboxId()' value='下载' />"
            userliststr += " <input type='button' onclick='refresh()' value='刷新' /></p>"
            $("#file-list").html(userliststr);
        },
        error: function (data, status, e) {
        }
    });

}

function checkboxId() {
    var ids = "";
    $("input[name='files']:checked").each(function (i) {
        ids += $(this).val() + ",";
    })
    console.log(ids);
    var url = webProjectName() + "/file/packingDownload?last=" + ids;
    url = encodeURI(encodeURI(url));
    window.open(url);
}

function refresh() {
    getFile();
}

/** 获取群聊组信息 */
function getGroupInfo() {
    var userId = document.getElementById('userId').value;
    $.ajax({
        url: webProjectName() + "/chatGroup/selectGroupByUserId",
        type: 'post',
        data: {userId: userId},
        success: function (data, status) {
            var obj = eval("(" + data + ")");
            console.log("发起群聊返回值【" + obj + "】");
            $("#group-list").html("");
            var html = "<br/>";
            $.each(obj, function (idx, o) {
                html += "<a style=\"color:blue;text-decoration:underline;\" onclick='groupChatConnect(" + o.id + ",\"" + o.groupName + "\")'>" + o.groupName + "</a><br/><br/>";
            });
            console.log(html);
            $("#group-list").html(html);
        },
        error: function (data, status, e) {
        }
    });
}

function groupChatConnect(groupId, groupName) {
    var userId = document.getElementById('userId').value;
    var userName = document.getElementById('username').value;
    layer.open({
        id: groupId,
        type: 2,
        shade: 0,
        title: groupName,
        area: ['1000px', '640px'],
        fixed: false,
        maxmin: true,
        content: 'static/chatGroup.html?groupId=' + groupId + '&userId=' + userId + '&userName=' + userName,
        btn: ['取消'],
        btn2: function (index, layero) {

        }
    });
}