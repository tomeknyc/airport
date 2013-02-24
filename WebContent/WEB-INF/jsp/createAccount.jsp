<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Create Account</title>
</head>
<body>
<h1>Account</h1>

<spring:nestedPath path="command">

<form action="" method="post">
<table>

<%@ include file="/WEB-INF/jspf/account_fields.jspf" %>

<tr>
  <td />
  <td><input type="submit" value="Create Account" /></td>
</tr>

</table>
</form>

</spring:nestedPath>

</body>
</html>