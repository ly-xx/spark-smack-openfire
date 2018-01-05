<!DOCTYPE html>
<html>
<head>
    消息
</head>
<body>
<form onsubmit="return sendMsg()">
    <div id="contactBox">
        <input id="username" type="text" name="username" value="${username!}">
        <input id="msg" type="text" name="msg">
        <input type="submit" value="发送">
        <a href="/logout">退出</a>
    </div>
</form>
<div>
    <h3>消息记录</h3>
<#if messageList??>
    <#list messageList as msg>
        <a>${msg!}</a><br>
    </#list>
</#if>
    <div id="historyBox"></div>
</div>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function sendMsg() {
        var username = $("#username").val();
        var msg = $("#msg").val();
        $.ajax({
            url: "/sendMsg",
            type: "post",
            data: {"username": username, "msg": msg},
            dataType: "json",
            success: function (result) {
                if (result.code == 0) {
                    var data = result.data;
                    $("#historyBox").append(data.username + "：" + data.msg + "<br>");
                }
            }
        });
        return false;
    }
</script>
</body>
</html>