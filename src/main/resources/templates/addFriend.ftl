<!DOCTYPE html>
<html>
<head>
    添加好友
</head>
<body>
<form onsubmit="return addFriend()">
    <input id="username" type="text" name="username"><br>
    <input id="name" type="text" name="name"><br>
    <input type="submit" value="添加">
    <a href="/logout">退出</a>
</form>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function addFriend() {
        var username = $("#username").val();
        var name = $("#name").val();
        $.ajax({
            url: "/addFriend",
            type: "post",
            data: {"username": username, "name": name},
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