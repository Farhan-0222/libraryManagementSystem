<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><title>Error</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"></head>
<body class="bg-light d-flex align-items-center justify-content-center vh-100">
<div class="text-center"><h1 class="display-4 text-primary">Something went wrong</h1>
<p class="text-muted">The page could not be loaded. Check the Tomcat log for details.</p>
<a class="btn btn-primary" href="<%= request.getContextPath() %>/dashboard">Back to dashboard</a></div></body></html>
