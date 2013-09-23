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
<link rel="stylesheet" href="resources/include/fontawesome/css/font-awesome.min.css">

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
            //$('.qdm2jsonUrl').attr("href", "http://"+value+":8888");
            $('.qdm2jsonUrl').attr("href", "http://qdm2json.herokuapp.com/");
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
                <li><a href="http://phenotypeportal.org/">PhenotypePortal</a></li>
                <li class="divider-vertical"></li>
            </ul>

        </div>
    </div>
</div>

<div class="container">


    <div id="myCarousel" class="carousel slide">
        <ol class="carousel-indicators">
            <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
            <li data-target="#myCarousel" data-slide-to="1"></li>
            <li data-target="#myCarousel" data-slide-to="2"></li>
        </ol>
        <!-- Carousel items -->
        <div class="carousel-inner">
            <div class="item">
                <div class="hero-unit">
                    <a class="qdm2jsonUrl" href=""><h1>QDM XML to JSON</h1></a>
                    <p>Convert QDM XML file executable JBoss Drools Rules.</p>
                    <img src="resources/include/img/json.png"/>
                </div>
            </div>
            <div class="item">
                <div class="hero-unit">
                    <a href="qdm2drools"><h1>JSON to Drools</h1></a>
                    <p>Convert the health-data-standards JSON to JBoss Drools Rules.</p>
                    <img src="resources/include/img/drools.png"/>
                </div>
            </div>
            <div class="item active">
                <div class="hero-unit">
                    <a href="executor"><h1>Phenotyping</h1></a>
                    <p>Execute rules and find cohorts</p>
                    <img src="resources/include/img/phenotyping.png"/>
                    <div>
                    Visit the official <a href="http://phenotypeportal.org">Phenotype Portal</a>
                    </div>
                </div>
            </div>
        </div>
        <!-- Carousel nav -->
        <a class="carousel-control left" href="#myCarousel" data-slide="prev">&lsaquo;</a>
        <a class="carousel-control right" href="#myCarousel" data-slide="next">&rsaquo;</a>
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
