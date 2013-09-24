<!DOCTYPE HTML>
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

        <script type="text/javascript" src="../../resources/include/jquery-ui-1.8.19.custom/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="../../resources/include/bootstrap/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="../../resources/include/bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
        <script type="text/javascript" src="../../resources/include/datepicker/js/bootstrap-datepicker.js"></script>
        <script type="text/javascript" src="../../resources/include/jquery.validate/jquery.validate.min.js"></script>
        <script type="text/javascript" src="../../resources/include/jquery.tablesorter/jquery.tablesorter.js"></script>

        <link rel="stylesheet" href="../../resources/include/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="../../resources/include/bootstrap-fileupload/bootstrap-fileupload.min.css">
        <link rel="stylesheet" href="../../resources/include/datepicker/css/datepicker.css" />

        <style type="text/css">
            body {
                padding-top: 65px;
            }
        </style>

        <script>
            $(document).ready(function() {
                $('span[rel=popover]').popover({trigger:"hover"});
            });
        </script>
    </head>
  <body>
  <div class="navbar navbar-fixed-top">

      <div class="navbar-inner">

          <div class="container">
              <a href="https://github.com/SHARP-HTP/qdm2json"><img style="position: absolute; top: 0; right: 0; border: 0;" src="https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png" alt="Fork me on GitHub"></a>

              <a class="brand" href="#">
                  QDM Phenotyping Executor
              </a>

              <ul class="nav">
                  <li class="divider-vertical"></li>
                  <li><a href="../../"><i class="icon-home"></i> Home</a></li>
                  <li class="divider-vertical"></li>
                  <li><a href="../executions">Executions</a></li>
                  <li class="divider-vertical"></li>
                  <li><a href="../api">API</a></li>
                  <li class="divider-vertical"></li>
              </ul>

          </div>
      </div>
  </div>

  <div class="container">
  <h2>Algorithm</h2>
    <table class="table table-striped">
        <tr>
            <th>Id</th>
            <th>Status</th>
            <th>Execution Time</th>
            <th>Parameters</th>
            <th>Results</th>
            <th>Delete</th>
        </tr>
        <tr>
            <td>${execution.id}</td>
            <c:if test="${execution.status eq 'FAILED'}">
                <td><span rel="popover" data-content="${execution.error}s" class='badge badge-important'>${execution.status}</span></td>
            </c:if>
            <c:if test="${execution.status ne 'FAILED'}">
                <td><span class='badge badge-success'>${execution.status}</span></td>
            </c:if>
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
	         <b>Input XML:</b> <a href="../execution/${execution.id}/input">${execution.parameters.xmlFileName}</a>
            </td>
            <c:if test="${execution.status eq 'COMPLETE'}">
            	<td><a href="${execution.xml.href}">XML</a></td>
            </c:if>
            <c:if test="${execution.status ne 'COMPLETE'}">
            	<td>&nbsp;</td>
            </c:if>
            <td>
            	<form:form action="../execution/${execution.id}" method="delete">
            		<input type="submit" value="Delete"/>
                </form:form>
            </td>
        </tr>
             
    </table>

      </div>

  <footer class="navbar navbar-fixed-bottom">
      <div class="container">
          <p class="muted credit">
              Powered by the <a href="https://github.com/projectcypress/health-data-standards">hqmf-parser</a>,
              <a href="https://ushik.ahrq.gov/">USHIK</a>,
              and the <a href="https://vsac.nlm.nih.gov/">NLM VSAC</a>,
              For more information see the
              <a href="http://phenotypeportal.org/">Phenotype Portal</a>.
          </p>
      </div>
  </footer>
  </body>
</html>
