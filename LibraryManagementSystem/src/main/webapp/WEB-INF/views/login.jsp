<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Sign in | College Library</title>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link href="${ctx}/css/style.css" rel="stylesheet">
</head>
<body>
<div class="auth-wrap">
  <section class="auth-art">
    <div class="mb-4 fs-4 fw-bold"><i class="bi bi-book-half me-2"></i>Library Manager</div>
    <h1>Every book, student and due date in one place.</h1>
    <p>Issue and return books, track fines automatically and see how the library is doing at a glance.</p>
    <ul>
      <li><i class="bi bi-check-circle-fill"></i> Live availability for every title</li>
      <li><i class="bi bi-check-circle-fill"></i> Fines calculated on return</li>
      <li><i class="bi bi-check-circle-fill"></i> Printable reports and CSV export</li>
    </ul>
  </section>
  <section class="auth-form">
    <div class="auth-box">
      <h2 class="mb-1">Sign in</h2>
      <p class="text-muted mb-4">Use the account given by your library.</p>

      <c:if test="${not empty sessionScope.flashMsg}">
        <div class="alert alert-${sessionScope.flashType}"><c:out value="${sessionScope.flashMsg}" /></div>
        <c:remove var="flashMsg" scope="session" /><c:remove var="flashType" scope="session" />
      </c:if>
      <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}" /></div></c:if>

      <form method="post" action="${ctx}/login">
        <div class="mb-3">
          <label class="form-label" for="username">Username</label>
          <input class="form-control form-control-lg" id="username" name="username" required autofocus value="<c:out value='${lastUsername}'/>">
        </div>
        <div class="mb-4">
          <label class="form-label" for="password">Password</label>
          <input class="form-control form-control-lg" type="password" id="password" name="password" required>
        </div>
        <button class="btn btn-primary btn-lg w-100" type="submit">Sign in</button>
      </form>
      <p class="mt-4 mb-0 text-center">New student? <a href="${ctx}/register">Create your account</a></p>
    </div>
  </section>
</div>
</body>
</html>
