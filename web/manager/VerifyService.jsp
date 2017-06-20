
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%
	Object sess_obj = session.getAttribute("admin");
	if (sess_obj != null) {//已登录，不处理
	} else {
		out.print("<script>alert('您还没有登录，请登录...'); window.location='ManagerLogin.html' </script>");
	}
%>

<html>

<head>
<title>高能管理</title>
<link rel="stylesheet" href="css/bootstrap.min.css">
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/jQuery.md5.js"></script>
<script type="text/javascript" src="js/sign.js"></script>
<link rel="stylesheet" href="css/fileinput.css">
<script src="js/fileinput.js"></script>
<script src="js/fileinput_locale_zh.js"></script>
<style type="text/css">
.center {
	width: auto;
	display: table;
	margin-left: auto;
	margin-right: auto;
}

.text-center {
	text-align: center;
}
</style>
</head>

<body>

	<div class="col-sm-offset-5 form-horizontal text-center" role="form">
		<div class="form-group">
			<div class="col-sm-5">
				<h3>高能管理</h3>
			</div>
		</div>
		 
	</div>
</body>

</html>