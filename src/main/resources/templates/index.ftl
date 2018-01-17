<!DOCTYPE html>
<html>
<head>
    登录成功
</head>
<body>
<h1>${key!}！</h1>
<div>
<#if friends??>
    <#list friends as friend>
        <a href="/getMsg?jid=${friend.user!}&friendName=${friend.name!}&userName=${userName!}">${(friend.name)!}&nbsp;&nbsp;</a><br>
    </#list>
</#if>
    <a href="/goSearch?userName=${userName!}">添加好友</a><br/>
    <a href="/goUpdate?">修改密码</a>
</div>
<a href="/logout?userName=${userName!}">退出</a>
</body>
</html>