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
	   			out.print("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"10\" ");
	   			%>
	   		</c:if>
	   	</c:if>
		
		<title>Algorithms</title>

        <script type="text/javascript" src="resources/include/jquery-ui-1.8.19.custom/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="resources/include/jquery-ui-1.8.19.custom/js/jquery-ui-1.8.19.custom.min.js"></script>
        <script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>
        <script type="text/javascript" src="resources/include/jquery.tablesorter/jquery.tablesorter.js"></script>
        <script type="text/javascript" src="resources/include/syntaxhighlighter_3.0.83/scripts/shCore.js"></script>
        <script type="text/javascript" src="resources/include/syntaxhighlighter_3.0.83/scripts/shBrushJava.js"></script>

        <link href="resources/include/syntaxhighlighter_3.0.83/styles/shCore.css" rel="stylesheet" type="text/css" />
        <link href="resources/include/syntaxhighlighter_3.0.83/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="resources/include/jquery.tablesorter/themes/blue/style.css" />
        <link rel="stylesheet" href="resources/include/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="resources/include/bootstrap-fileupload/bootstrap-fileupload.min.css">
        <link rel="stylesheet" href="resources/include/select2/select2.css">
        <link rel="stylesheet" href="resources/include/fontawesome/css/font-awesome.min.css">

        <style type="text/css">
            body {
                padding-top: 65px;
            }
		</style>
		<script>
		
		  $(document).ready(function() {
			    
			    $( ".datepicker" ).datepicker(
						{ dateFormat: "dd-MM-yy" }		
				);
			    
			    $.extend(jQuery.validator.messages, {
			        required: "<br/>This field is required."
			    });
			    
			    $("#executionForm").validate();
			 
			    $("#executionsTable").tablesorter( {sortList: [ [0,0] ] } ); 
		  });
		</script>
	</head>
  <body>
  <div class="navbar navbar-fixed-top">

      <div class="navbar-inner">

          <div class="container">
              <a href="https://github.com/SHARP-HTP/qdm2json"><img style="position: absolute; top: 0; right: 0; border: 0;" src="https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png" alt="Fork me on GitHub"></a>

              <a class="brand" href="#">
                  QDM XML to JSON Converter
              </a>

              <ul class="nav">
                  <li class="divider-vertical"></li>
                  <li><a href="/">Home</a></li>
                  <li class="divider-vertical"></li>
                  <li><a href="/executions">Executions</a></li>
                  <li class="divider-vertical"></li>
              </ul>

          </div>
      </div>
  </div>

    <div class="container">
        <h2>Algorithms</h2>

        <table id="executionsTable" class="tablesorter">
    	<thead>
	        <tr>
	            <th>Id</th>
	            <th>Status</th>
	            <th>Execution Time</th>
	            <th>Parameters</th>
	            <th>Image</th>
	            <th>XML</th>
	            <th>Delete</th>
	        </tr>
        </thead>
        <tbody>
	        <c:choose>
	            <c:when test="${Executions != null}">
	                <c:forEach var="execution" items="${Executions.executions}" varStatus="counter">
	                    <tr>
	                        <td><a href="execution/${execution.id}">${execution.id}</a></td>
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
		                        <b>Zip:</b> <a href="execution/${execution.id}/zip">${execution.parameters.zipFileName}</a>
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

    <h2>Execute Algorithm</h2>
    <form action="executions" id="executionForm" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td><p>Zip: <input class="required" type="file" name="file" /></p></td>
                <td><p>Start Date: <input class="required datePicker" type="text" id="startDate" name="startDate"></p></td>
                <td><p>End Date: <input class="required datePicker" type="text" id="endDate" name="endDate"></p></td>
                <td><input type="submit" name="submit" value="Execute Algorithm"/></td>
            </tr>
        </table>
    </form>
    
<div id="hidden_element_1" class="hidden" style="display: none">
    <pre class="brush: java">
	public static void pollStatus() throws Exception {
		URL executions = new URL("http://localhost:8080/qdm2drools-rest/execution/1");
		
        URLConnection connection = executions.openConnection();
        connection.setRequestProperty("Accept", "application/xml");
        InputStream in = connection.getInputStream();
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(in);
 
		Attr status = (Attr)doc.getElementsByTagName("execution").	
						item(0).getAttributes().getNamedItem("status");
		
		System.out.println(status.getValue());
        
        in.close();
	}
	</pre>
	</div>
        </div>
  </body>
</html>
