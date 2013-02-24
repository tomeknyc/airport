<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tag" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Create an Account - Enter Billing Address</title>
</head>
<body>

<h1>Create an Account - Enter Billing Address</h1>

<tag:errors name="createAccount" />

<spring:nestedPath path="createAccount">

<form action="" method="post">
<div>
<input type="hidden" name="_page1" value="true" />
<input type="hidden" name="_finish" value="true" />
</div>

<table>

<spring:nestedPath path="account.billingAddress">

<tag:formField name="Street" path="street" />
<tag:formField name="City" path="city" />
<tag:formField name="State" path="state" />
<tag:formField name="Postal/Zip Code" path="postalCode" />

</spring:nestedPath>

<tr>
  <td />
  <td><input type="submit" value="Create Account" /></td>
</tr>

</table>
</form>
</spring:nestedPath>

</body>
</html>