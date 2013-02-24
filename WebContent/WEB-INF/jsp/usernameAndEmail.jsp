<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Create an Account</title>
<link rel="stylesheet" href="<c:url value="/css/main.css" />" type="text/css" />
</head>
<body>

<h1>Create an Account</h1>

<tag:errors name="createAccount" />

<spring:nestedPath path="createAccount">

<form action="" method="post">
<div>
<input type="hidden" name="_page0" value="true" />
<input type="hidden" name="_target1" value="true" />
</div>

<table>

<spring:nestedPath path="account">

<tag:formField name="Username" path="username" />
<tag:formField name="Password" path="password" type="password" />

</spring:nestedPath>

<tag:formField name="Confirm Password" path="confirmPassword" type="password" />

<spring:nestedPath path="account">

<tag:formField name="Email" path="email" />

</spring:nestedPath>

<tr>
  <td />
  <td><input type="submit" value="Go to Step 2" /></td>
</tr>

</table>
</form>
</spring:nestedPath>

</body>
</html>