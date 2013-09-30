<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<c:if test="${Executions != null}">
			<c:set var="pending" value="false" scope="page" />
		
	   		<c:forEach var="loop" items="${Executions.executions}">
	   			<c:if test="${ loop.status eq 'PROCESSING' }">
	   				<c:set var="pending" value="true" scope="page"/>
	   			</c:if>
	   		</c:forEach>
	   		
	   		<c:if test="${ pending }">
	   			<%
	   			out.print("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"3\" >");
	   			%>
	   		</c:if>
	   	</c:if>

        <script type="text/javascript" src="../resources/include/jquery-ui-1.8.19.custom/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="../resources/include/bootstrap/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="../resources/include/bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
        <script type="text/javascript" src="../resources/include/datepicker/js/bootstrap-datepicker.js"></script>
        <script type="text/javascript" src="../resources/include/jquery.validate/jquery.validate.min.js"></script>
        <script type="text/javascript" src="../resources/include/jquery.tablesorter/jquery.tablesorter.js"></script>

        <link rel="stylesheet" href="../resources/include/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="../resources/include/bootstrap-fileupload/bootstrap-fileupload.min.css">
        <link rel="stylesheet" href="../resources/include/datepicker/css/datepicker.css" />

        <link rel="stylesheet" href="../resources/style.css">

        <style type="text/css">
            body {
                padding-top: 65px;
            }
		</style>
		<script>
		
		  $(document).ready(function() {
			    
			    $( ".datePicker" ).datepicker({
                    autoclose:true,
                    format:"dd-M-yyyy"
                });
			    
			    $.extend(jQuery.validator.messages, {
			        required: "<br/>This field is required."
			    });
			    
			    $("#executionForm").validate();
			 
			    $("#executionsTable").tablesorter( {sortList: [ [0,0] ] } );

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
                  QDM Phenotyping Executions
              </a>

              <ul class="nav">
                  <li class="divider-vertical"></li>
                  <li><a href="../"><i class="icon-home"></i> Home</a></li>
                  <li class="divider-vertical"></li>
                  <li><a href="../executor">Phenotyping</a></li>
                  <li class="divider-vertical"></li>
                  <li><a href="api">API</a></li>
                  <li class="divider-vertical"></li>
              </ul>

          </div>
      </div>
  </div>

    <div class="container">
        <h2>Algorithms</h2>

        <table id="executionsTable" class="table table-striped">
    	<thead>
	        <tr>
	            <th>Id</th>
	            <th>Status</th>
	            <th>Execution Time</th>
	            <th>Parameters</th>
	            <th>Results</th>
	            <th>Delete</th>
	        </tr>
        </thead>
        <tbody>
	        <c:choose>
	            <c:when test="${Executions != null}">
	                <c:forEach var="execution" items="${Executions.executions}" varStatus="counter">
	                    <tr>
	                        <td><a href="execution/${execution.id}">${execution.id}</a></td>
                            <c:if test="${execution.status eq 'FAILED'}">
                                <td><span rel="popover" data-content="${execution.error}" class='badge badge-important'>${execution.status}</span></td>
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
		                        <b>Input XML:</b> <a href="execution/${execution.id}/input">${execution.parameters.xmlFileName}</a>
	                        </td>
				            <c:if test="${execution.status eq 'COMPLETE'}">
				            	<td><a href="${execution.xml.href}">XML</a></td>
				            </c:if>
				            <c:if test="${execution.status ne 'COMPLETE'}">
				            	<td>&nbsp;</td>
				            </c:if>
	                        <td>
	                        	<form:form action="execution/${execution.id}" method="delete">
	                        		<input type="submit" value="Delete"/>
	                            </form:form>
	                        </td>
	                    </tr>
	                </c:forEach>
	            </c:when>
	        </c:choose>
        </tbody>
    </table>

    <form action="executions" id="executionForm" method="post" class="form-horizontal" enctype="multipart/form-data">
            <legend>Execute Algorithm</legend>
        <div class="control-group">
            <label class="control-label" for="file">QDM/HQMF XML:</label>
            <div class="controls">
                <input class="required" type="file" id="file" name="file" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="startDate">Start Date:</label>
            <div class="controls">
                <input class="required datePicker" type="text" id="startDate" name="startDate" value="01-Jan-2012"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="endDate">End Date:</label>
            <div class="controls">
                <input class="required datePicker" type="text" id="endDate" name="endDate" value="31-Dec-2012"/>
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
                <button type="submit" class="btn" name="submit">Execute Algorithm</button>
            </div>
        </div>
    </form>

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
