<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Strict//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>firefly</title>
</head>
<body>
<form action="<c:url value="/app/book/create"></c:url>" method="POST">
	书名：<input type="text" name="title"></input><br/>
	价格：<input type="text" name="price"></input><br/>
	<input type="submit" value="提交"></input>
</form>
</body>
</html>
