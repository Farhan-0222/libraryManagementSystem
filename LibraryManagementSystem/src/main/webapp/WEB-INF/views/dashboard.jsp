<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row g-3 mb-4">
  <div class="col-6 col-xl-3"><div class="stat"><div class="ico"><i class="bi bi-journals"></i></div>
    <div><div class="num">${stats.totalBooks}</div><div class="lbl">Total copies (${stats.bookTitles} titles)</div></div></div></div>
  <div class="col-6 col-xl-3"><div class="stat"><div class="ico green"><i class="bi bi-check2-circle"></i></div>
    <div><div class="num">${stats.availableBooks}</div><div class="lbl">Available copies</div></div></div></div>
  <div class="col-6 col-xl-3"><div class="stat"><div class="ico amber"><i class="bi bi-box-arrow-up-right"></i></div>
    <div><div class="num">${stats.issuedBooks}</div><div class="lbl">Books issued</div></div></div></div>
  <div class="col-6 col-xl-3"><div class="stat"><div class="ico red"><i class="bi bi-alarm"></i></div>
    <div><div class="num">${stats.overdueBooks}</div><div class="lbl">Overdue books</div></div></div></div>
  <div class="col-6 col-xl-3"><div class="stat"><div class="ico"><i class="bi bi-mortarboard"></i></div>
    <div><div class="num">${stats.totalStudents}</div><div class="lbl">Students</div></div></div></div>
  <div class="col-6 col-xl-3"><div class="stat"><div class="ico"><i class="bi bi-person-badge"></i></div>
    <div><div class="num">${stats.totalLibrarians}</div><div class="lbl">Librarians</div></div></div></div>
  <div class="col-6 col-xl-3"><div class="stat"><div class="ico red"><i class="bi bi-cash-coin"></i></div>
    <div><div class="num">&#8377;<fmt:formatNumber value="${stats.unpaidFines}" pattern="#,##0"/></div><div class="lbl">Unpaid fines</div></div></div></div>
  <div class="col-6 col-xl-3"><div class="stat"><div class="ico green"><i class="bi bi-wallet2"></i></div>
    <div><div class="num">&#8377;<fmt:formatNumber value="${stats.collectedFines}" pattern="#,##0"/></div><div class="lbl">Fines collected</div></div></div></div>
</div>

<div class="row g-3 mb-4">
  <div class="col-lg-7"><div class="panel h-100">
    <div class="panel-head"><h2>Copies by category</h2></div>
    <div class="panel-body"><canvas id="catChart" height="130"></canvas></div></div></div>
  <div class="col-lg-5"><div class="panel h-100">
    <div class="panel-head"><h2>Books issued per month</h2></div>
    <div class="panel-body"><canvas id="monthChart" height="190"></canvas></div></div></div>
</div>

<div class="panel">
  <div class="panel-head">
    <h2>Recent transactions</h2>
    <div class="d-flex gap-2">
      <a class="btn btn-primary btn-sm" href="${ctx}/issue"><i class="bi bi-box-arrow-up-right"></i> Issue a book</a>
      <a class="btn btn-outline-primary btn-sm" href="${ctx}/return"><i class="bi bi-box-arrow-in-down-left"></i> Accept a return</a>
    </div>
  </div>
  <div class="table-wrap">
    <table class="table table-hover">
      <thead><tr><th>Book</th><th>Student</th><th>Issued</th><th>Due</th><th>Status</th></tr></thead>
      <tbody>
        <c:forEach var="r" items="${recent}">
          <tr>
            <td><c:out value="${r.bookTitle}"/></td>
            <td><c:out value="${r.studentName}"/> <small class="text-muted">(<c:out value="${r.rollNo}"/>)</small></td>
            <td><fmt:formatDate value="${r.issueDate}" pattern="dd MMM yyyy"/></td>
            <td><fmt:formatDate value="${r.dueDate}" pattern="dd MMM yyyy"/></td>
            <td><t:statusBadge r="${r}"/></td>
          </tr>
        </c:forEach>
        <c:if test="${empty recent}"><tr><td colspan="5"><div class="empty"><i class="bi bi-inbox"></i>No transactions yet. Issue the first book to see it here.</div></td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script>
  new Chart(document.getElementById('catChart'), {
    type: 'bar',
    data: { labels: ${catLabels}, datasets: [{ label: 'Copies', data: ${catValues}, backgroundColor: '#2563eb', borderRadius: 6 }] },
    options: { plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
  });
  new Chart(document.getElementById('monthChart'), {
    type: 'line',
    data: { labels: ${monthLabels}, datasets: [{ label: 'Issued', data: ${monthValues}, borderColor: '#1d4ed8', backgroundColor: 'rgba(37,99,235,.12)', fill: true, tension: .3 }] },
    options: { plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
  });
</script>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
