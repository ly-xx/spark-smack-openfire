<!DOCTYPE html>
<html>
<head>
    添加好友
</head>
<body>
<form onsubmit="return addFriend()">
    JID：<input id="jid" type="text" name="jid" value="${jid!}"><br>
    userName：<input id="username" type="text" name="username" value="${username!}"><br>
    <input type="submit" value="添加">
    <a href="/logout">退出</a>
</form>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function addFriend() {
        var jid = $("#jid").val();
        var username = $("#username").val();
        $.ajax({
            url: "/addFriend",
            type: "post",
            data: {"jid": jid, "username": username},
            dataType: "json",
            success: function (result) {
                if (result.code == 0) {
                    alert("添加成功！");
                }
            }
        });
        return false;
    }
</script>
</body>
</html>