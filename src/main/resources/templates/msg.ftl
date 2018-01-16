<!DOCTYPE html>
<html>
<head>
    消息
</head>
<body>
<form onsubmit="return sendMsg()">
    <div id="contactBox">
        <input id="friendName" type="text" name="friendName" value="${friendName!}">
        <input id="msg" type="text" name="msg">
        <input type="submit" value="发送">
        <a href="/logout?userName=${userName!}">退出</a>
    </div>
</form>
<div>
    <h3>消息记录</h3>
    <div id="historyBox"></div>
</div>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function sendMsg() {
        var friendName = $("#friendName").val();
        var msg = $("#msg").val();
        $.ajax({
            url: "/sendMsg",
            type: "post",
            data: {"userName": '${userName!}', "msg": msg, "jid":'${jid!''}', friendName:'${friendName!}'},
            dataType: "json",
            success: function (result) {
                if (result.code == 0) {
                    setInterval(getMsgList, 1000);
                }
            }
        });
        return false;
    }


    function getMsgList() {
        $.ajax({
            url: "/getMsgList",
            type: "get",
            dataType: "json",
            success: function (result) {
                if (result.code == 0) {
                    var datas = result.datas;
                    var html = "";
                    for (var i in datas) {
                        html += datas[i].userName + "：" + datas[i].msg + "<br>";
                    }
                    $("#historyBox").html(html);
                }
            }
        });
    }
</script>
</body>
</html>