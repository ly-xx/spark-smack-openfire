<!DOCTYPE html>
<html>
<head>
    注册
</head>
<body>
<form onsubmit="return registerAccount()">
    <input id="username" type="text" name="username"><br>
    <input id="psd" type="text" name="psd"><br>
    <input type="submit" value="注册">
    <a href="/logout">退出</a>
</form>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function registerAccount() {
        var username = $("#username").val();
        var psd = $("#psd").val();
        $.ajax({
            url: "/registerAccount",
            type: "post",
            data: {"username": username, "psd": psd},
            dataType: "json",
            success: function (result) {
                if (result.code == 0) {
                    alert("注册成功！");
                }
            }
        });
        return false;
    }
</script>
</body>
</html>