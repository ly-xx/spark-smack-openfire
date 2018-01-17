<!DOCTYPE html>
<html>
<head>
    修改密码
</head>
<body>
<form onsubmit="return updatePassword()">
    姓名：<input id="userName" type="text" name="userName"><br>
    密码：<input id="password" type="text" name="password"><br>
    新密码：<input id="newPassword" type="text" name="newPassword"><br>
    <input type="submit" value="修改">
</form>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function updatePassword() {
        var userName = $("#userName").val();
        var password = $("#password").val();
        var newPassword = $("#newPassword").val();
        $.ajax({
            url: "/updatePassword",
            type: "post",
            data: {"userName": userName, "password": password, "newPassword":newPassword},
            dataType: "json",
            success: function (result) {
                if (result.code == 0) {
                    alert("登录成功！");
                    window.location.href = "/goLogin";
                }
            }
        });
        return false;
    }
</script>
</body>
</html>