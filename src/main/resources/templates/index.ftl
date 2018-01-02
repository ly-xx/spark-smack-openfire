<!DOCTYPE html>
<html>
<head>
    登录成功
</head>
<body>
<h1>${key!}！</h1>
<form action="/logout">
    <div>
        <#if friends??>
            <#list friends as friend>
                <a href="/getMsg?username=${friend.user!}">${(friend.name)!}&nbsp;&nbsp;</a><br>
            </#list>
        </#if>
    </div>
    <input type="submit" value="退出">
</form>
</body>
</html>