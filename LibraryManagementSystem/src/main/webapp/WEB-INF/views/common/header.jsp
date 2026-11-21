<%-- Shared page top: <head>, sidebar, top bar and flash message. Every inner page includes this, then footer.jsp. --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${sessionScope.user.role}" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${pageTitle}" /> | Library</title>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link href="${ctx}/css/style.css" rel="stylesheet">
</head>
<body>
<aside class="sidebar" id="sidebar">
  <div class="brand">
    <div class="logo"><i class="bi bi-book-half"></i> Library Manager</div>
    <small><c:out value="${sessionScope.libraryName}" /></small>
  </div>
  <ul class="nav-list">
    <li><a href="${ctx}/dashboard" class="${active == 'dashboard' ? 'active' : ''}"><i class="bi bi-grid-1x2"></i> Dashboard</a></li>
    <li><a href="${ctx}/catalog" class="${active == 'catalog' ? 'active' : ''}"><i class="bi bi-search"></i> Search Books</a></li>

    <c:if test="${role == 'ADMIN' || role == 'LIBRARIAN'}">
      <li class="group">Library desk</li>
      <li><a href="${ctx}/issue" class="${active == 'issue' ? 'active' : ''}"><i class="bi bi-box-arrow-up-right"></i> Issue Book</a></li>
      <li><a href="${ctx}/return" class="${active == 'return' ? 'active' : ''}"><i class="bi bi-box-arrow-in-down-left"></i> Return Book</a></li>
      <li><a href="${ctx}/fines" class="${active == 'fines' ? 'active' : ''}"><i class="bi bi-cash-coin"></i> Fines</a></li>
      <li class="group">Records</li>
      <li><a href="${ctx}/books" class="${active == 'books' ? 'active' : ''}"><i class="bi bi-journal-bookmark"></i> Books</a></li>
      <li><a href="${ctx}/students" class="${active == 'students' ? 'active' : ''}"><i class="bi bi-mortarboard"></i> Students</a></li>
      <li><a href="${ctx}/reports" class="${active == 'reports' ? 'active' : ''}"><i class="bi bi-file-earmark-bar-graph"></i> Reports</a></li>
    </c:if>
    <c:if test="${role == 'ADMIN'}">
      <li class="group">Administration</li>
      <li><a href="${ctx}/librarians" class="${active == 'librarians' ? 'active' : ''}"><i class="bi bi-person-badge"></i> Librarians</a></li>
      <li><a href="${ctx}/settings" class="${active == 'settings' ? 'active' : ''}"><i class="bi bi-gear"></i> Settings</a></li>
    </c:if>
  </ul>
  <div class="sidebar-foot">
    <a href="${ctx}/logout"><i class="bi bi-box-arrow-left"></i> Log out</a>
  </div>
</aside>

<div class="main">
  <header class="topbar">
    <button class="menu-btn" id="menuBtn" aria-label="Open menu"><i class="bi bi-list"></i></button>
    <h1><c:out value="${pageTitle}" /></h1>
    <div class="who">
      <strong><c:out value="${sessionScope.user.fullName}" /></strong><br>
      <small><c:out value="${fn:toLowerCase(role)}" /></small>
    </div>
  </header>
  <div class="content">
    <c:if test="${not empty sessionScope.flashMsg}">
      <div class="alert alert-${sessionScope.flashType} alert-dismissible fade show flash-alert" role="alert">
        <c:out value="${sessionScope.flashMsg}" />
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
      </div>
      <c:remove var="flashMsg" scope="session" />
      <c:remove var="flashType" scope="session" />
    </c:if>
