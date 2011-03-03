<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Strict//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<title>firefly</title>
</head>
<body>
	<form action='<c:url value="/app/upload"></c:url>' method="POST" enctype="multipart/form-data">
		<div>图片名：<input type="text" name="picName"></input></div>
		<div>地点：<input type="text" name="picSite"></input></div>
		<div>图片：<input type="file" name="fu"></input></div>
		<div><input type="submit" value="提交"></input></div>
	</form>
</body>
</html>
