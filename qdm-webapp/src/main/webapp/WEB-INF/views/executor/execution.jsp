<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="execution" value="${Execution}"/>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<c:if test="${execution != null}">
	   		
	   		<c:if test="${ execution.status eq 'PROCESSING' }">
	   			<%
	   			out.print("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"10\" ");
	   			%>
	   		</c:if>
	   	</c:if>
	   	
		<title>Algorithm</title>
		<link rel="stylesheet" href="../resources/style.css" type="text/css"/>
	</head>
  <body>
    <h2>Algorithm</h2>
    <table>
        <tr>
            <th>Id</th>
            <th>Status</th>
            <th>Execution Time</th>
            <th>Parameters</th>
            <th>Image</th>
            <th>XML</th>
            <th>Delete</th>
        </tr>
        <tr>
            <td>${execution.id}</td>
            <td>${execution.status}</td>
            <td>
             <b>Start:</b> ${execution.start}
             <br/>
             <b>Finish:</b> ${execution.finish}
            </td>
            <td>
             <b>Start Date:</b> ${execution.parameters.startDate}
             <br/>
             <b>End Date:</b> ${execution.parameters.endDate}
             <br/>
	         <b>Zip:</b> <a href="../execution/${execution.id}/zip">${execution.parameters.zipFileName}</a>
            </td>
            <c:if test="${execution.status eq 'COMPLETE'}">
            	<td><a href="${execution.image.href}">Image</a></td>
            	<td><a href="${execution.xml.href}">XML</a></td>
            </c:if>
            <c:if test="${execution.status ne 'COMPLETE'}">
            	<td>&nbsp;</td>
            	<td>&nbsp;</td>
            </c:if>
            <td>
            	<form:form action="../execution/${execution.id}" method="delete">
            		<input type="submit" value="Delete"/>
                </form:form>
            </td>
        </tr>
             
    </table>
    
    <br/>
    <a href="../executions">Back to all Executions</a>

  </body>
</html>
