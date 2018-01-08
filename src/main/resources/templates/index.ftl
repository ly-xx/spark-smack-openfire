<!DOCTYPE html>
<html>
<head>
    登录成功
</head>
<body>
<h1>${key!}！</h1>
<form action="/logout?userName=${userName!}">
    <div>
        <#if friends??>
            <#list friends as friend>
                <a href="/getMsg?jid=${friend.user!}&friendName=${friend.name!}&userName=${userName!}">${(friend.name)!}&nbsp;&nbsp;</a><br>
            </#list>
        </#if>
            <a href="/goSearch?userName=${userName!}">添加好友</a>
    </div>
    <input type="submit" value="退出">
</form>
</body>
</html>