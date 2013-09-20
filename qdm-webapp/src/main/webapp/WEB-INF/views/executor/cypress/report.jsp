<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

        <script type="text/javascript" src="../../resources/include/jquery-ui-1.8.19.custom/js/jquery-1.7.2.min.js"></script>
        <script type="text/javascript" src="../../resources/include/bootstrap/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="../../resources/include/bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
        <script type="text/javascript" src="../../resources/include/datepicker/js/bootstrap-datepicker.js"></script>
        <script type="text/javascript" src="../../resources/include/jquery.validate/jquery.validate.min.js"></script>
        <script type="text/javascript" src="../../resources/include/jquery.tablesorter/jquery.tablesorter.js"></script>

        <link rel="stylesheet" href="../../resources/include/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="../../resources/include/bootstrap-fileupload/bootstrap-fileupload.min.css">
        <link rel="stylesheet" href="../../resources/include/datepicker/css/datepicker.css" />

        <link rel="stylesheet" href="../../resources/style.css">

        <style type="text/css">
            .container {
                width: 1000px;
            }
            .resultBox {
                border: 1px outset;
                padding: 5px;
            }

		</style>

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
                  <li><a href="../../">Home</a></li>
                  <li class="divider-vertical"></li>
                  <li><a href="api">API</a></li>
                  <li class="divider-vertical"></li>
              </ul>

          </div>
      </div>
  </div>

    <div class="container">
        <h2>Cypress Validation Report - <span style="color: green">PASS: <span id="pass">0</span></span> / <span style="color: red">FAIL: <span id="fail">0</span></span></h2>

        <table id="executionsTable" class="table table-striped">
    	<thead>
	        <tr>
	            <th class="span1">Id</th>
                <th class="span1">Status</th>
                <th class="span6">Populations</th>
	        </tr>
        </thead>
        <tbody>
            <c:forEach var="measure" items="${measures}">
                <tr class="resultRow" data-hqmfId="${measure}">
                    <td>${measure}</td>
                    <td class="overallStatus"></td>
                    <td class="populationsStatus"></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

        <script>
            $(document).ready(function() {
                populateReport($.makeArray( $('.resultRow') ).reverse());
            });


            function populateReport(rowList){
                if(rowList == null || rowList.length == 0){
                    return;
                }

                var $row = $(rowList.pop());

                $.getJSON("../cypress/measure/"+$row.attr("data-hqmfId")+"/validation", function( data ) {
                    var populationResult = ""

                    var pass = true;

                    for(population in data){
                        if(data[population]){
                            populationResult += "<span class='resultBox'>"+population+": <span class='badge badge-success'>PASS</span></span> ";
                        } else {
                            populationResult += "<span class='resultBox'>"+population+": <span class='badge badge-important'>FAIL</span></span> ";
                            pass = false;
                        }
                    }

                    populationResult = populationResult.slice(0,-1);

                    var overallResult = ""
                    if(pass){
                        increment($("#pass"));
                        overallResult += "<span class='resultBox badge badge-success'>VALIDATED</span> ";
                    } else {
                        increment($("#fail"));
                        overallResult += "<span class='resultBox badge badge-important'>FAILED</span> ";
                    }

                    $row.find('.populationsStatus').html(populationResult)
                    $row.find('.overallStatus').html(overallResult);

                    populateReport(rowList);
                });
            }

            function increment($num){
                $num.html((parseInt($num.html(),10)+1).toString());
            }
        </script>

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
