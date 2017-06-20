function signUrl(url) {
	var key="qianxun";
	var time=new Date().getTime();
	var sign=$.md5($.md5(time+key));
	url+="?timestamp="+time+"&signature="+sign;
	return url;
}
