
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
	String pageId = request.getParameter("page");
	if (pageId == null || pageId.equals(""))
		pageId = "1";
	String typeId = request.getParameter("type");
	if (typeId == null || typeId.equals(""))
		typeId = "1";
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
				<h3>用户审核</h3>
			</div>
		</div>
		<input type="hidden" id="page" value="<%=pageId%>" /> <input
			type="hidden" id="type" value="<%=typeId%>" />
		<div class="form-group">
			<div class="col-sm-1">
				<button type="button" class="btn btn-success" id="last">上一页</button>
			</div>
			<div class="col-sm-1 col-sm-offset-1">
				<button type="button" class="btn btn-default" id="back">返回</button>
			</div>
			<div class="col-sm-1 col-sm-offset-1">
				<button type="button" class="btn btn-success" id="next">下一页</button>
			</div>
			<script>
				//type:1 id,2 stu
				$('#last').click(function userLogin() {
					var page = $('#page').val();
					var type = $('#type').val();
					if (page == 1) {
						alert("没有上一页了");
						return;
					}
					page--;
					var url = "userVerify_checkUserInfoByPage.action";
					if (type == 2)
						url = "userVerify_checkStuInfoByPage.action";
					$.post(signUrl(url), {
						'page' : page
					}, function(data, status) {
						if (data == "{\"list\":[]}") {//再次判断
							alert("没有上一页了");
							return;
						}
						$('#page').val(page);
						$('#code').empty();
						$('#code').append(data);
					});
				});
				$('#back').click(function userLogin() {
					window.location = 'ManagerMain.jsp'
				});
				$('#next').click(function userLogin() {
					var page = $('#page').val();
					var type = $('#type').val();
					page++;
					var url = "userVerify_checkUserInfoByPage.action";
					if (type == 2)
						url = "userVerify_checkStuInfoByPage.action";
					$.post(signUrl(url), {
						'page' : page
					}, function(data, status) {
						if (data == "{\"list\":[]}") {
							alert("没有下一页了");
							return;
						}
						//如果列表为空，则没有下一页
						$('#page').val(page);
						$('#code').empty();
						$('#code').append(data);
					})
				});
			</script>
		</div>
		<div id="content">
			<!-- 
			<div class='form-group'>
				<div class='col-sm-5' id='name'>用户姓名</div>
			</div>
			<div class='form-group'>
				<div class='col-sm-5' id='code'>证件号码</div>
			</div>
			<div class='form-group'>
				<div class='col-sm-5'>学历</div>
			</div>
			<div class='form-group'>
				<div class='col-sm-5'>学校</div>
			</div>
			<div class='form-group'>
				<div class='col-sm-5'>学院</div>
			</div>
			<div class='form-group'>
				<div class='col-sm-5'>专业</div>
			</div>
			<div class='form-group'>
				<div class='col-sm-5'>入学时间</div>
			</div>
			<div class='form-group'>
				<a href='../test.jpg' title='点击查看大图'> <img class='col-sm-5'
					id='image' src='../test.jpg' />
				</a>
			</div>
			<div class='form-group'>
				<div class='col-sm-1'>
					<button type='button' class='btn btn-primary'
						onclick="agree('state', '12345678910')">同意</button>
				</div>
				<div class='col-sm-3'>
					<input type='hidden' class='btn btn-default' id='state' value="" />
				</div>
				<div class='col-sm-1'>
					<button type='button' class='btn btn-danger'
						onclick="refuse('state', '12345678910')">拒绝</button>
				</div>
			</div>
			<br /> <br />
		</div>
		<div id="test"></div>
		 -->
		</div>

		<script>
			function agree(id, phone) {
				if ($('#' + id).attr("value") != "") {
					alert("您已审核，不能再次处理");
					return;
				}
				var url = "userVerify_confirmUserVerify.action";
				var type = $('#type').val();
				if (type == 2)
					url = "userVerify_confirmStuVerify.action";
				if (phone == "") {
					alert("处理失败");
					return;
				}
				getResult(id, url, phone, true);
			}

			function refuse(id, phone) {
				if ($('#' + id).attr("value") != "") {
					alert("您已审核，不能再次处理");
					return;
				}
				var url = "userVerify_denyUserVerify.action";
				var type = $('#type').val();
				if (type == 2)
					url = "userVerify_denyStuVerify.action";
				if (phone == "") {
					alert("处理失败");
					return;
				}
				getResult(id, url, phone, false);
			}

			function getResult(id, url, phone, agree) {
				$.post(signUrl(url), {
					'username' : phone
				}, function(data, status) {
					if (data == 1) {//成功
						dealResult(id, agree);
					} else {
						alert("处理失败");
					}
				})
			}
			function dealResult(id, agree) {
				$('#' + id).attr("type", "button");
				if (agree)
					$('#' + id).attr("value", "已同意");
				else
					$('#' + id).attr("value", "已拒绝");
			}
		</script>

		<script>
			function buildStuInfo(degree, school, college, major, time) {
				var stuInfo = "<div class='form-group'><div class='col-sm-5'>学历："
						+ degree
						+ "</div></div><div class='form-group'><div class='col-sm-5'>学校："
						+ school
						+ "</div></div><div class='form-group'><div class='col-sm-5'>学院："
						+ college
						+ "</div></div><div class='form-group'><div class='col-sm-5'>专业："
						+ major
						+ "</div></div><div class='form-group'><div class='col-sm-5'>入学时间："
						+ time + "</div></div>";
				return stuInfo;
			}
			function buildInfo(realName, userName, idNumber, imgPath, stuInfo) {
				var anCard = "<div class='form-group'><div class='col-sm-5' id='name'>真实姓名："
						+ realName
						+ "</div></div><div class='form-group'><div class='col-sm-5' id='code'>证件号码："
						+ idNumber
						+ "</div></div>"
						+ stuInfo
						+ "<div class='form-group'><a href='"
					+imgPath+"' title='点击查看大图'> <img class='col-sm-5'id='image' src='"
					+imgPath+"' /></a></div><div class='form-group'><div class='col-sm-1'><button type='button' class='btn btn-primary' onclick=\"agree('state"
						+ userName
						+ "', '"
						+ userName
						+ "')\">同意</button></div><div class='col-sm-3'><input type='hidden' class='btn btn-default' id='state"
					+userName+"' value='' /></div><div class='col-sm-1'><button type='button' class='btn btn-danger' onclick=\"refuse('state"
						+ userName
						+ "', '"
						+ userName
						+ "')\">拒绝</button></div></div><br /> <br />";
				return anCard;
			}

			function dealPageInfo(data) {
				$.each(data.list, function(i, item) {
					var type = $('#type').val();
					var realName = item.realName;
					var userName = item.phone;
					var idNumber = "";
					var imgPath = "";
					var stuInfo = "";
					if (type == 2) {
						idNumber = item.stuID;
						imgPath = item.stuIDImage;
						var degree = item.degree;
						var school = item.school;
						var college = item.college;
						var major = item.major;
						var time = item.admissionTime;
						stuInfo = buildStuInfo(degree, school, college, major,
								time);
					} else {
						idNumber = item.IDNumber;
						imgPath = item.IDImage;
					}
					var info = buildInfo(realName, userName, idNumber, imgPath,
							stuInfo);
					$('#content').append(info);
				});
			}
		</script>
		<script>
			function dealData(data) {
				/////////////////
				//$('#test').text(data); 
				/////////////////

				var type = $('#type').val();
				data = jQuery.parseJSON(data);
				var count = 0;
				$.each(data.list, function(i, item) {
					count++;
					if (type != 2) {//ID
						var id = item.id;
						var realName = item.realName;
						var userName = item.phone;
						var idNumber = item.IDNumber;// 身份证号
						var imgPath = item.IDImage;// 身份证图片
						$('#content').append(
								buildInfo(realName, userName, idNumber,
										imgPath, ""));
					} else {//STU
						var id = item.id;
						var realName = item.realName;
						var userName = item.phone;
						var stuID = item.stuID;
						var stuIDImage = item.stuIDImage;// 学生证
						var degree = item.degree;// 学历
						var school = item.school;// 学校
						var college = item.college;// 学院
						var major = item.major;// 专业
						var admissionTime = item.admissionTime;// 入学时间
						$('#content').append(
								buildInfo(realName, userName, stuID,
										stuIDImage, buildStuInfo(degree,
												school, college, major,
												admissionTime)));
					}
				});
				if (count == 0) {
					alert("没有了");
					return;
				}

				////////////再次处理已经同意或拒绝的
			}

			function getData(page) {
				var type = $('#type').val();
				var url = "userVerify_checkUserInfoByPage.action";
				if (type == 2)
					url = "userVerify_checkStuInfoByPage.action";
				$.post(signUrl(url), {
					'page' : page
				}, function(data, status) {
					if (data == "{\"list\":[]}") {
						alert("没有了");
						return;
					}
					//如果列表为空，则没有下一页
					$('#page').val(page);
					dealData(data);
				});
			}
		</script>

		<script type="text/javascript">
			var page = $('#page').val();
			if (page == 1)
				getData(1);
		</script>
</body>

</html>