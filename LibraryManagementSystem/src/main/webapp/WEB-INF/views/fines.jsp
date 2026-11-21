<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="row g-3 mb-4">
  <div class="col-md-4"><div class="stat"><div class="ico red"><i class="bi bi-cash-coin"></i></div>
    <div><div class="num">&#8377;<fmt:formatNumber value="${stats.unpaidFines}" pattern="#,##0.00"/></div><div class="lbl">Unpaid fines</div></div></div></div>
  <div class="col-md-4"><div class="stat"><div class="ico green"><i class="bi bi-wallet2"></i></div>
    <div><div class="num">&#8377;<fmt:formatNumber value="${stats.collectedFines}" pattern="#,##0.00"/></div><div class="lbl">Fines collected</div></div></div></div>
  <div class="col-md-4"><div class="stat"><div class="ico"><i class="bi bi-calendar-x"></i></div>
    <div><div class="num">&#8377;<fmt:formatNumber value="${rate}" pattern="#,##0.##"/></div><div class="lbl">Fine per overdue day</div></div></div></div>
</div>

<div class="panel mb-4">
  <div class="panel-head"><h2>Recorded fines</h2><a class="btn btn-sm btn-outline-primary" href="${ctx}/reports?type=fines">Fine report</a></div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><th>Student</th><th>Book</th><th>Overdue</th><th>Amount</th><th>Status</th><th class="text-end">Action</th></tr></thead>
    <tbody>
      <c:forEach var="f" items="${fines}"><tr>
        <td><c:out value="${f.studentName}"/> <small class="text-muted">(<c:out value="${f.rollNo}"/>)</small></td>
        <td><c:out value="${f.bookTitle}"/></td>
        <td>${f.overdueDays} days</td>
        <td>&#8377;<fmt:formatNumber value="${f.fineAmount}" pattern="#,##0.00"/></td>
        <td><span class="badge-soft ${f.fineStatus == 'PAID' ? 'green' : 'red'}">${fn:toLowerCase(f.fineStatus)}</span></td>
        <td class="text-end">
          <c:if test="${f.fineStatus == 'UNPAID'}">
            <form method="post" action="${ctx}/fines" class="d-inline" onsubmit="return confirm('Mark this fine as paid?');">
              <input type="hidden" name="fineId" value="${f.fineId}">
              <button class="btn btn-sm btn-primary"><i class="bi bi-check2"></i> Mark paid</button>
            </form>
          </c:if>
        </td></tr></c:forEach>
      <c:if test="${empty fines}"><tr><td colspan="6"><div class="empty"><i class="bi bi-emoji-smile"></i>No fines recorded yet.</div></td></tr></c:if>
    </tbody></table></div>
</div>

<div class="panel">
  <div class="panel-head"><h2>Overdue books (fine grows every day until returned)</h2></div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><th>Student</th><th>Book</th><th>Due</th><th>Days overdue</th><th>Fine so far</th></tr></thead>
    <tbody>
      <c:forEach var="r" items="${overdue}"><tr>
        <td><c:out value="${r.studentName}"/> <small class="text-muted">(<c:out value="${r.rollNo}"/>)</small></td>
        <td><c:out value="${r.bookTitle}"/></td>
        <td><fmt:formatDate value="${r.dueDate}" pattern="dd MMM yyyy"/></td>
        <td><span class="badge-soft red">${r.overdueDays} days</span></td>
        <td>&#8377;<fmt:formatNumber value="${r.fineAmount}" pattern="#,##0.00"/></td></tr></c:forEach>
      <c:if test="${empty overdue}"><tr><td colspan="5"><div class="empty"><i class="bi bi-check2-all"></i>No overdue books today.</div></td></tr></c:if>
    </tbody></table></div>
  <div class="panel-body pt-2 text-muted small">The fine is saved and becomes payable when the librarian accepts the return.</div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
