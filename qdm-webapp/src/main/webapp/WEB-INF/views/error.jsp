<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>HTP REST</title>

<script type="text/javascript" src="resources/include/jquery-ui-1.8.19.custom/js/jquery-1.7.2.min.js"></script>
<script src="resources/include/bootstrap/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="resources/include/bootstrap/css/bootstrap.min.css">

<link rel="stylesheet" href="resources/style.css">

<style>
    .item {
        height: 400px;
    }
    .hero-unit {
        height: 100%;
        padding-top: 20px;
        padding-left: 60px;
    }
</style>

    <script type="text/javascript">
        $(document).ready(function() {
            $('.carousel').carousel();

            var value = location.hostname;
            $('.qdm2jsonUrl').attr("href", "${qdm2jsonUrl}");
        });
    </script>

</head>
<body>

<div class="navbar navbar-fixed-top">

    <div class="navbar-inner">

        <div class="container">
            <a href="https://github.com/SHARP-HTP/qdm-phenotyping"><img style="position: absolute; top: 0; right: 0; border: 0;" src="https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png" alt="Fork me on GitHub"></a>

            <a class="brand" href="#">
                QDM Phenotyping
            </a>

            <ul class="nav">
                <li class="divider-vertical"></li>
                <li><a class="qdm2jsonUrl" href="">Qdm2Json</a></li>
                <li class="divider-vertical"></li>
                <li><a href="qdm2drools">Qdm2Drools</a></li>
                <li class="divider-vertical"></li>
                <li><a href="executor">Phenotyping</a></li>
                <li class="divider-vertical"></li>
                <li><a href="executor/cypress/report">Cypress Validation</a></li>
                <li class="divider-vertical"></li>
                <li><a href="http://phenotypeportal.org/">PhenotypePortal</a></li>
                <li class="divider-vertical"></li>
            </ul>

        </div>
    </div>
</div>

<div class="container">

    <div class="alert">
    There was an error translating into Drools: <br/>
    ${error}
    <br/>
    <br/>
    This is most likely caused by an ill-formed input algorithm or an unsupported operator. QDM Phenotyping supports
    all Cypress-validated algorithms, and all other algorithms that use standard syntax and operations.
    <br/>
    <br/>
    Please check the input file and try again, or input another algorithm.
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
