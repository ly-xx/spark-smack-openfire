<!DOCTYPE html>
<html>
<head>
    注册
</head>
<body>
<form onsubmit="return registerAccount()">
    <input id="userName" type="text" name="userName"><br>
    <input id="psd" type="text" name="psd"><br>
    <input type="submit" value="注册">
    <a href="/logout?userName=${userName!}">退出</a>
</form>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function registerAccount() {
        var userName = $("#userName").val();
        var psd = $("#psd").val();
        $.ajax({
            url: "/registerAccount",
            type: "post",
            data: {"userName": userName, "psd": psd},
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