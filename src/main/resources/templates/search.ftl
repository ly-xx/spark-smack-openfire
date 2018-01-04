<!DOCTYPE html>
<html>
<head>
    查询
</head>
<body>
<form onsubmit="return searchUser()">
    <input id="userName" type="text" name="userName"><br>
    <input type="submit" value="搜索">
</form>
<div id="userBox"></div>
<script src="http://filealiyun.geeker.com.cn/ued/js/jquery-1.8.3.min.js"></script>
<script>
    function searchUser() {
        var userName = $("#userName").val();
        $.ajax({
            url: "/searchUser",
            type: "post",
            data: {"userName": userName},
            dataType: "json",
            success: function (result) {
                if (result.code == 0) {
                    var html = "";
                    for (var i in result.datas) {
                        html += "<a href='/goAddFriend?username="+result.datas[i].username+"&jid="+result.datas[i].jid+"'>"
                                + result.datas[i].username + "</a><br>";
                    }
                    $("#userBox").html(html);
                }
            }
        });
        return false;
    }
</script>
</body>
</html>