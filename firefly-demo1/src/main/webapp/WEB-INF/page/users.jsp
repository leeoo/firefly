<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Strict//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<title>firefly-users</title>
</head>
<body>
<table>
	<c:forEach var="user" items="${users}" >
		<tr>
			<td>${user.id}</td>
			<td>${user.name}</td>
			<td>${user.password}</td>
		</tr>
	</c:forEach>
</table>
</body>
</html>
