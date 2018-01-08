<!DOCTYPE html>
<html>
<head>
    登录
</head>
<body>
<form onsubmit="return login()">
    <input id="userName" type="text" name="userName"><br>
    <input id="psd" type="text" name="psd"><br>
    <input type="submit" value="登录">
    <a href="/goRegisterAccount">注册</a>
</form>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function login() {
        var userName = $("#userName").val();
        var psd = $("#psd").val();
        $.ajax({
            url: "/login",
            type: "post",
            data: {"userName": userName, "psd": psd},
            dataType: "json",
            success: function (result) {
                if (result.code == 0) {
                    alert("登录成功！");
                    window.location.href = "/goIndex?userName=" + userName;
                }
            }
        });
        return false;
    }
</script>
</body>
</html>