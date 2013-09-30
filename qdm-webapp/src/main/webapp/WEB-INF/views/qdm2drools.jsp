<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>HTP REST</title>

<script type="text/javascript" src="resources/include/jquery-ui-1.8.19.custom/js/jquery-1.7.2.min.js"></script>
<script src="resources/include/bootstrap/js/bootstrap.min.js"></script>
<script src="resources/include/bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
<script src="resources/include/select2/select2.min.js"></script>
<script type="text/javascript" src="resources/include/syntaxhighlighter_3.0.83/scripts/shCore.js"></script>
<script type="text/javascript" src="resources/include/syntaxhighlighter_3.0.83/scripts/shBrushJava.js"></script>

<link rel="stylesheet" href="resources/include/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="resources/include/bootstrap-fileupload/bootstrap-fileupload.min.css">
<link rel="stylesheet" href="resources/include/select2/select2.css">

<link rel="stylesheet" href="resources/style.css" />

    <style type="text/css">
        .hero-unit {
            background-color: #ffffff;
        }
        .container {
            width: 800px;
        }
    </style>
</head>
<body>

    <div class="navbar navbar-fixed-top">

        <div class="navbar-inner">

            <div class="container">
                <a href="https://github.com/SHARP-HTP/qdm-phenotyping"><img style="position: absolute; top: 0; right: 0; border: 0;" src="https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png" alt="Fork me on GitHub"></a>

                <a class="brand" href="#">
                    QDM XML to Drools Converter
                </a>

                <ul class="nav">
                    <li class="divider-vertical"></li>
                    <li><a href="."><i class="icon-home"></i> Home</a></li>
                    <li class="divider-vertical"></li>
                </ul>

            </div>
        </div>
    </div>

    <div class="container">
        <div class="hero-unit">
            <h1>QDM XML to Drools</h1>
            <p>Convert your QDM XML file executable JBoss Drools Rules.</p>

            <script type="text/javascript">
                $(document).ready(function(){
                    $("#measureid").select2({
                        formatResult: format,
                        escapeMarkup: function(m) { return m; }
                    });
                });

                function format(emeasure) {
                    return "<span class='label'>"+$(emeasure.element).attr('data-nqfid')+"</span> "+emeasure.text;
                }
            </script>

            <ul id="tabs" class="nav nav-tabs" data-tabs="tabs">
                <li class="active"><a href="#byMeasureId" data-toggle="tab">Select Measure</a></li>
                <li><a href="#fileUpload" data-toggle="tab">File Upload</a></li>
            </ul>

            <div id="my-tab-content" class="tab-content">
                <div class="tab-pane active" id="byMeasureId">
                    <form action='qdm2drools' id="emeasureForm" method='get' class="form-inline well">
                        <select id="measureid" name="measureid" class="combobox" style="width:80%">
                            <c:forEach var="emeasure" items="${emeasures}">
                            <option data-nqfid='<c:if test="${emeasure.nqfId eq null}">NA</c:if><c:if test="${emeasure.nqfId ne null}">${emeasure.nqfId}</c:if>' value="${emeasure.measureId}"></span>${emeasure.title}</option>
                            </c:forEach>
                        </select>
                        <button class="btn btn-primary dropdown-toggle" type='submit'>Convert!</button>
                    </form>
                </div>
                <div class="tab-pane" id="fileUpload">
                    <form action='qdm2json' enctype='multipart/form-data' method='post' class="form-inline well">
                        <div class="fileupload fileupload-new" data-provides="fileupload">
                            <div class="input-append">
                                <div class="uneditable-input span3"><i class="icon-file fileupload-exists"></i>
                                    <span class="fileupload-preview"></span></div>
    <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
      <input type="file" name="file"/></span><a href="#" class="btn fileupload-exists" data-dismiss="fileupload">Remove</a>

                            </div>
                            <button class="btn btn-primary fileupload-exists dropdown-toggle" type='submit'>Convert!</button>
                            <div class="fileupload-exists">
                                <small>QDM Version</small>
                                <label class="radio">
                                    <input type="radio" name="version" id="qdm1.0" value="1.0" checked>
                                    <span >1.0</span>
                                </label>
                                <label class="radio">
                                    <input type="radio" name="version" id="qdm2.0" value="2.0">
                                    <span >2.0</span>
                                </label>
                            </div>

                        </div>
                    </form>
                </div>
            </div>
        </div>


        <script type="text/javascript">
            jQuery(document).ready(function ($) {
                $('#tabs').tab();
            });
        </script>

    </div>

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