<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Student registration | College Library</title>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link href="${ctx}/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5" style="max-width:720px">
  <div class="panel">
    <div class="panel-body p-4">
      <h2 class="fw-bold mb-1">Student registration</h2>
      <p class="text-muted">Create your library account. You can borrow books once you are registered.</p>
      <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}" /></div></c:if>
      <form method="post" action="${ctx}/register" class="row g-3">
        <div class="col-md-6"><label class="form-label">Full name</label>
          <input class="form-control" name="fullName" required maxlength="100" value="<c:out value='${form.fullName}'/>"></div>
        <div class="col-md-6"><label class="form-label">Roll number</label>
          <input class="form-control" name="rollNo" required maxlength="30" value="<c:out value='${form.rollNo}'/>"></div>
        <div class="col-md-6"><label class="form-label">Email</label>
          <input class="form-control" type="email" name="email" required value="<c:out value='${form.email}'/>"></div>
        <div class="col-md-6"><label class="form-label">Phone (10 digits)</label>
          <input class="form-control" name="phone" pattern="[0-9]{10}" value="<c:out value='${form.phone}'/>"></div>
        <div class="col-md-6"><label class="form-label">Course</label>
          <input class="form-control" name="course" required value="${empty form.course ? 'BCA' : fn:escapeXml(form.course)}"></div>
        <div class="col-md-6"><label class="form-label">Semester</label>
          <input class="form-control" type="number" name="semester" min="1" max="12" required value="<c:out value='${form.semester}'/>"></div>
        <div class="col-12"><label class="form-label">Username</label>
          <input class="form-control" name="username" required pattern="[A-Za-z0-9_]{4,30}" title="4-30 letters, digits or underscore" value="<c:out value='${form.username}'/>"></div>
        <div class="col-md-6"><label class="form-label">Password</label>
          <input class="form-control" type="password" name="password" required minlength="6"></div>
        <div class="col-md-6"><label class="form-label">Confirm password</label>
          <input class="form-control" type="password" name="confirm" required minlength="6"></div>
        <div class="col-12 d-flex gap-2 align-items-center">
          <button class="btn btn-primary" type="submit">Register</button>
          <a href="${ctx}/login">Back to sign in</a>
        </div>
      </form>
    </div>
  </div>
</div>
</body>
</html>
