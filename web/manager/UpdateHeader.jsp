
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%
	Object sess_obj = session.getAttribute("admin");
	if (sess_obj != null) {//已登录，不处理
	} else {
		out.print("<script>alert('您还没有登录，请登录...'); window.location='ManagerLogin.html' </script>");
	}
%>
<%
	String imgId = request.getParameter("imgId");
	if (imgId == null || imgId.equals(""))
		imgId = "0";
%>
<html>

<head>
<title>图片更新</title>
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

	<div class="col-sm-offset-4 form-horizontal text-center">
		<div class="form-group">
			<div class="col-sm-6">
				<h3>图片更新</h3>
			</div>
		</div>
		<form action="prop_updateLink.action" method="post" id="resultForm"
			onsubmit="return uploadResult();" enctype="multipart/form-data"
			target="form_iframe">
			<iframe name="form_iframe" width="1px" height="1px" scrolling="0"
				style="display: none"> </iframe>
			<div class="form-group">
				<label for="firstname" class="col-sm-2 control-label">显示标题</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" id="name" name="prop.title">
				</div>
				<input type="hidden" id="imgId" name="prop.id" value="<%=imgId%>" />
			</div>
			<div class="form-group">
				<label for="lastname" class="col-sm-2 control-label">跳转链接</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" id="link" name="prop.link">
				</div>
			</div>
			<div class="form-group">
				<label for="lastname" class="col-sm-2 control-label">内容详情</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" id="detail"
						name="prop.context">
				</div>
			</div>
			<div class="form-group">
				<label for="lastname" class="col-sm-2 control-label">图片上传</label>
				<div class="col-sm-4">
					<input type="file" class="file" id="file" name="prop.img">
				</div>
				<script type="text/javascript">
					$('#file')
							.fileinput(
									{
										language : 'zh', //设置语言
										//uploadUrl : "test.action", //上传的地址
										allowedFileExtensions : [ 'jpg',
												'jpeg', 'png', 'bmp' ],//接收的文件后缀
										showUpload : false, //是否显示上传按钮
										showCaption : true,//是否显示标题
										maxFileCount : 1, //同时最多上传1个文件
										browseClass : "btn btn-primary", //按钮样式             
										previewFileIcon : "<i class='glyphicon glyphicon-king'></i>",
									});
					function uploadResult() {
						$("#resultForm").attr("action",
								signUrl("prop_updateLink.action"));
						//$("#resultForm").ajaxSubmit(function(message) {
						//	alert(message);
						//});
						setTimeout("jumpToRefresh()", 3000);
						return true;//false禁止html提交
					}
					function jumpToRefresh(){
						var imgId = $('#imgId').val();
						window.location = "UpdateHeader.jsp?imgId=" + imgId;//刷新
					}
				</script>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-1 col-sm-1">
					<button type="button" class="btn btn-warning" id="back">返回上级</button>
				</div>
				<div class="col-sm-offset-1 col-sm-1">
					<button type="button" class="btn btn-default" id="refresh">刷新界面</button>
				</div>
				<div class="col-sm-offset-1 col-sm-1">
					<button type="button" class="btn btn-success" id="sure">确认修改</button>
				</div>
				<script>
					$('#back').click(function userLogout() {
						window.location = "ManagerMain.jsp";
					});
					$('#refresh').click(
							function userLogout() {
								window.location = "UpdateHeader.jsp?imgId="
										+ $('#imgId').val();
							});
					$('#sure').click(function userLogout() {
						if ($("#name").val() == "") {
							alert("标题不能为空");
							return;
						}
						if ($("#link").val() == "") {
							alert("链接不能为空");
							return;
						}
						if ($("#file").val() == "") {
							alert("请选择文件！");
							return;
						}
						$('#resultForm').submit();
					});
				</script>
			</div>
			<div class="form-group">
				<div class="col-sm-6">
					<h4>当前图片</h4>
				</div>
			</div>
			<div class='form-group'>
				<a href='' title='点击查看大图' id="imageLink"> <img class='col-sm-6'
					id='imageShow' src='' />
				</a>
			</div>
		</form>

		<div id="test"></div>

	</div>

	<script type="text/javascript">
		function dealPageInfo(data) {
			var imgId = $('#imgId').val();
			data = jQuery.parseJSON(data);
			$.each(data.list, function(i, item) {
				var id = item.id;
				if (id == imgId) {
					var imgPath = item.image;
					var link = item.link;
					var title = item.title;
					var context = item.context;
					$('#name').val(title);
					$('#link').val(link);
					$('#detail').val(context);
					$('#imageLink').attr("href", "../.." + imgPath);
					$('#imageShow').attr("src", "../.." + imgPath);
				}
			});
		}

		var imgId = $('#imgId').val();
		var url = "prop_getMainPageImages.action";
		if (imgId > 5)
			url = "prop_getSmallImages.action";
		$.post(signUrl(url), {}, function(data, status) {
			if (data == "{\"list\":[]}") {//再次判断
				alert("获取数据失败");
				window.location = "ManagerMain.jsp";
				return;
			}
			dealPageInfo(data);
		});
	</script>
</body>

</html>