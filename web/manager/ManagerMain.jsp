
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
		<div class="form-group">
			<div class="col-sm-5">
				<h4>主页图片</h4>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(1)">大图1</button>
			</div>
			<div class="col-sm-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(2)">大图2</button>
			</div>
			<div class="col-sm-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(3)">大图3</button>
			</div>
			<div class="col-sm-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(4)">大图4</button>
			</div>
			<div class="col-sm-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(5)">大图5</button>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(6)">小图1</button>
			</div>
			<div class="col-sm-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(7)">小图2</button>
			</div>
			<div class="col-sm-1 col-sm-offset-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(8)">小图3</button>
			</div>
			<div class="col-sm-1">
				<button type="button" class="btn btn-default" onclick="gotoImage(9)">小图4</button>
			</div>
		</div>
		<script type="text/javascript">
			function gotoImage(id) {
				window.location = "UpdateHeader.jsp?imgId=" + id;
			}
		</script>
		<div class="form-group">
			<div class="col-sm-5">
				<h4>身份审核</h4>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-1 col-sm-offset-1">
				<button type="button" class="btn btn-default center"
					onclick="gotoCard(1)">身份证</button>
			</div>
			<div class="col-sm-1 col-sm-offset-1">
				<button type="button" class="btn btn-default center"
					onclick="gotoCard(2)">学生证</button>
			</div>
		</div>
		<script type="text/javascript">
			function gotoCard(type) {
				window.location = "VerifyUser.jsp?type=" + type + "&page=1";
			}
		</script>
		<div class="form-group">
			<div class="col-sm-5">
				<h4>提现列表下载</h4>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-3">
				<input type="text" class="form-control" id="time"
					placeholder="日期：XXXX-XX-XX">
			</div>
			<div class="col-sm-1">
				<input type="button" class="btn btn-primary" id="download"
					value="下载">
			</div>
			<script>
				var download_file = function downloadFile() {
					var time = $("#time").val();
					if (time == "") {
						alert("请输入日期");
						return;
					}
					$.post(signUrl("file_tradeDownload.action"), {
						'today' : time
					}, function(data, status) {
						if (data == null || data == "" || data == "-1") {
							alert("获取文件失败");
							return;
						}
						window.open(data);
					});
				};
				$('#download').click(download_file);
			</script>
			<div class="col-sm-1">
				<button type="button" class="btn btn-default"
					onClick="window.open('https://open.alipay.com/platform/home.htm');">去支付宝</button>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-5">
				<h4>提现结果上传</h4>
			</div>
		</div>
		<div class="form-group">
			<form action="file_alipayUpload.action" method="post" id="resultForm"
				onsubmit="return uploadResult();" enctype="multipart/form-data"
				target="form_iframe">
				<div class="col-sm-4">
					<input type="file" class="file" id="file" name="alipayResult">
					<script>
						$('#file')
								.fileinput(
										{
											language : 'zh', //设置语言
											//uploadUrl : "test.action", //上传的地址
											allowedFileExtensions : [ 'xls',
													'xlsx' ],//接收的文件后缀
											showUpload : false, //是否显示上传按钮
											showCaption : true,//是否显示标题
											maxFileCount : 1, //同时最多上传1个文件
											browseClass : "btn btn-primary", //按钮样式             
											previewFileIcon : "<i class='glyphicon glyphicon-king'></i>",
										});
						function uploadResult() {
							if ($('#file').val() == "") {
								alert("请选择文件！");
								return false;
							}
							$("#resultForm").attr("action",
									signUrl("file_alipayUpload.action"));
							//$("#resultForm").ajaxSubmit(function(message) {
							//	alert(message);
							//});
							window.location = "ManagerMain.jsp";//刷新
							return true;//false禁止html提交
						}
					</script>
				</div>
				<div class="col-sm-1">
					<button type="submit" class="btn btn-primary">上传</button>
				</div>
			</form>
			<iframe name="form_iframe" width="1px" height="1px" scrolling="0"
				style="display: none"> </iframe>
		</div>
		<div class="form-group">
			<div class="col-sm-5">
				<h4>状态管理</h4>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-5">
				<button type="button" class="btn btn-default" id="logout">退出登录</button>
			</div>
			<script>
				var user_logout = function userLogout() {
					$.post(signUrl("userVerify_adminLogout.action"), {},
							function(data, status) {
								window.location = "ManagerLogin.html";
							});
				};
				$('#logout').click(user_logout);
			</script>
		</div>
	</div>
</body>

</html>