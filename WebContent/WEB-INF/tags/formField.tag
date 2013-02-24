<%@ tag body-content="scriptless" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="type" required="false" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty type}">
<c:set var="type" value="text" scope="page" />
</c:if>

<spring:bind path="${path}">
<tr>
  <td><label for="${status.expression}"
  <c:if test="${status.error}">class="error"</c:if>>${name}:</label></td>
  <td>
      <input type="${type}" id="${status.expression}" name="${status.expression}"
        value="${status.value}" />
  </td>
</tr>
</spring:bind>