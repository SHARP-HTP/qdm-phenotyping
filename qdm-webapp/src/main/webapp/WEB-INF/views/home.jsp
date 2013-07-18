<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
<link rel="stylesheet" href="resources/include/fontawesome/css/font-awesome.min.css">
<link rel="stylesheet" href="resources/include/syntaxhighlighter_3.0.83/styles/shCore.css" />
<link rel="stylesheet" href="resources/include/syntaxhighlighter_3.0.83/styles/shThemeDefault.css" />

<style>
    body {
        padding-top: 65px;
    }
</style>

<script>
	$(document).ready(function() {
		SyntaxHighlighter.all();
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
	<h1>HTP REST Service (<a href="executions">View/Load Executions</a>)</h1>
    The HTP REST Service has one resource:
    <pre>/execution/{id} - an Execution by Id</pre>
	And Several Sub-resources
	
	<pre>/execution/{id}/xml - an Execution result XML</pre>
	
	<pre>/execution/{id}/image - an Execution result Image</pre>
	
	<pre>/execution/{id}/zip - an Execution input Zip</pre>

	<h1>API/Example Code</h1>
	<h2>Polling For Status</h2>
	<h3>URL: /execution/{id} (GET)</h3>
	<div>
		<pre class="brush: java">
	public static void pollStatus() throws Exception {
		URL executions = new URL(
				"http://.../execution/{id}");

		URLConnection connection = executions.openConnection();
		connection.setRequestProperty("Accept", "application/xml");
		InputStream in = connection.getInputStream();

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(in);

		Attr status = (Attr) doc.getElementsByTagName("execution").item(0)
				.getAttributes().getNamedItem("status");

		System.out.println("Status: " + status.getValue());

		in.close();
	}

	</pre>
	</div>
	
	<h2>Get Result XML</h2>
	<h3>URL: /execution/{id}/xml (GET)</h3>
	<div>
		<pre class="brush: java">
	public static void getXml() throws Exception {
		URL executions = new URL(
				"http://.../execution/{id}/xml");

		URLConnection connection = executions.openConnection();
		connection.setRequestProperty("Accept", "application/xml");
		InputStream in = connection.getInputStream();

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(in);

		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer trans = transFactory.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc.getDocumentElement());

		trans.transform(source, result);

		System.out.println("XML: " + sw.toString());

		in.close();
	}
	</pre>
	</div>
	
	<h2>Get Result Image</h2>
	<h3>URL: /execution/{id}/image (GET)</h3>
	<div>
		<pre class="brush: java">
	public static void getImage() throws Exception {
		URL executions = new URL(
				"http://.../execution/{id}/image");

		URLConnection connection = executions.openConnection();
		connection.setRequestProperty("Accept", "image/png");
		InputStream in = connection.getInputStream();

		File tempFile = File.createTempFile(UUID.randomUUID().toString(),
				".png");

		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				tempFile));
		for (int b; (b = in.read()) != -1;) {
			out.write(b);
		}

		out.flush();
		out.close();
		in.close();

		System.out.println("Image Saved to: " + tempFile.getAbsolutePath());
	}
	</pre>
	</div>
	
	<h2>Create a new Result</h2>
	<h3>URL: /executions (POST)</h3>
	<div>
		<pre class="brush: java">
	public static void createExecution(String targetUrl, File zipFile, String startDate,
			String endDate) throws Exception {
		String charset = "UTF-8";

		String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
		String CRLF = "\r\n"; // Line separator required by multipart/form-data.

		URL url = new URL(targetUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		
		PrintWriter writer = null;
		try {
		    OutputStream output = connection.getOutputStream();
		    writer = new PrintWriter(new OutputStreamWriter(output, charset), true); // true = autoFlush, important!

		    // Send start date.
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"startDate\"").append(CRLF);
		    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
		    writer.append(CRLF);
		    writer.append(startDate).append(CRLF).flush();

		    // Send end date.
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"endDate\"").append(CRLF);
		    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
		    writer.append(CRLF);
		    writer.append(endDate).append(CRLF).flush();

		    // Send binary file.
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + zipFile.getName() + "\"").append(CRLF);
		    writer.append("Content-Type: application/zip").append(CRLF);
		    writer.append("Content-Transfer-Encoding: binary").append(CRLF);
		    writer.append(CRLF).flush();
		    InputStream input = null;
		    try {
		        input = new FileInputStream(zipFile);
		        byte[] buffer = new byte[1024];
		        for (int length = 0; (length = input.read(buffer)) > 0;) {
		            output.write(buffer, 0, length);
		        }
		        output.flush(); // Important! Output cannot be closed. Close of writer will close output as well.
		    } finally {
		        if (input != null) {
		        	input.close(); 
		        }
		    }
		    writer.append(CRLF).flush(); // CRLF is important! It indicates end of binary boundary.

		    // End of multipart/form-data.
		    writer.append("--" + boundary + "--").append(CRLF).flush();
		    
		    Map headerfields = connection.getHeaderFields();
		    
		    System.out.println("Resource Created At: " + headerfields.get("Location").get(0));

		} finally {
		    if (writer != null) {
		    	writer.close();
		    }
		    connection.disconnect();
		}
	}
	</pre>
	</div>
	
		<h2>Delete a Result</h2>
	<h3>URL: /execution/{id} (DELETE)</h3>
	<div>
		<pre class="brush: java">
	public static void deleteExecution() throws Exception {
		URL url = new URL("http://.../execution/{id}");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Accept", "application/xml");
		connection.setDoOutput(false);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("DELETE");
		
		if(connection.getResponseCode() == 200){
			System.out.println("DELETED");
		} else {
			System.out.println("ERROR: " + connection.getResponseCode());
		}
	}
	</pre>
	</div>

</div>
</body>
</html>
