<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%
	String name = request.getParameter("username");
	String pwd = request.getParameter("userpass");
	if (name != null) {
		if ("chengmeng".equals(name) && "chengmeng".equals(pwd)) {
			session.setAttribute("session", "session");
			response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			String newLocn = "ManagerMain.jsp";
			response.setHeader("Location", newLocn);
		} else {
			out.print("<script>alert('用户名或密码错误！'); </script>");
		}
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>

<head>
<title>橙果管理</title>
</head>

<body>

	<center>
		<table border='0'>
			<form action="#" method="post">
				<tr>
					<th colspan='2'><h2>管理员登录</h2></th>
				</tr>
				<tr>
					<th>账号：</th>
					<th><input type="text" name="username" id="text" /></th>
				</tr>
				<tr>
					<th>密码：</th>
					<th><input type="password" name="userpass" id="text" /></th>
				</tr>
				<tr>
					<th colspan='2'><input type="submit" name="submit" value="登录"
						id="btn" /></th>
				</tr>
			</form>
		</table>
	</center>

</body>

</html>
