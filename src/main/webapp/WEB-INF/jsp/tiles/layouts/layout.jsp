<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>타일즈 템플릿 사용</title>
</head>
<body>
	<t:insertAttribute name="header"></t:insertAttribute>
	<t:insertAttribute name="content"></t:insertAttribute>
	<t:insertAttribute name="footer"></t:insertAttribute>
</body>
</html>