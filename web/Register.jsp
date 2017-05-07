<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>User Register Page</title>
</head>
<body>

<s:form action="userAction">
    <s:textfield name="user.username" label="用户名"/>
    <s:password name="user.userpass" label="密码"/>
    <s:submit value="页面注册"/>
</s:form>
<br/>
<s:form action="user_register">
    <s:textfield name="user.username" label="用户名"/>
    <s:password name="user.userpass" label="密码"/>
    <s:submit value="请求注册"/>
</s:form>
<br/>
<s:form action="user_login">
    <s:textfield name="user.username" label="用户名"/>
    <s:password name="user.userpass" label="密码"/>
    <s:submit value="请求登录"/>
</s:form>

</body>
</html>