<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<c:set var="me" value="${sessionScope.student}" />

<div class="panel mb-4"><div class="panel-body d-flex flex-wrap justify-content-between gap-2">
  <div><h2 class="h5 mb-1">Hello, <c:out value="${sessionScope.user.fullName}"/></h2>
    <span class="text-muted">Roll no. <c:out value="${me.rollNo}"/> &middot; <c:out value="${me.course}"/> semester <c:out value="${me.semester}"/></span></div>
  <a class="btn btn-primary align-self-center" href="${ctx}/catalog"><i class="bi bi-search"></i> Find a book</a>
</div></div>

<div class="row g-3 mb-4">
  <div class="col-6 col-lg-3"><div class="stat"><div class="ico amber"><i class="bi bi-book"></i></div>
    <div><div class="num">${currentCount}</div><div class="lbl">Books with me</div></div></div></div>
  <div class="col-6 col-lg-3"><div class="stat"><div class="ico red"><i class="bi bi-alarm"></i></div>
    <div><div class="num">${overdueCount}</div><div class="lbl">Overdue</div></div></div></div>
  <div class="col-6 col-lg-3"><div class="stat"><div class="ico red"><i class="bi bi-cash-coin"></i></div>
    <div><div class="num">&#8377;<fmt:formatNumber value="${fineDue}" pattern="#,##0"/></div><div class="lbl">Fine to pay</div></div></div></div>
  <div class="col-6 col-lg-3"><div class="stat"><div class="ico"><i class="bi bi-clock-history"></i></div>
    <div><div class="num">${totalBorrowed}</div><div class="lbl">Books borrowed so far</div></div></div></div>
</div>

<div class="panel mb-4">
  <div class="panel-head"><h2>Books I need to return</h2></div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><th>Book</th><th>Issued</th><th>Return by</th><th>Status</th><th>Fine so far</th></tr></thead>
    <tbody>
      <c:set var="shown" value="0"/>
      <c:forEach var="r" items="${records}">
        <c:if test="${r.status == 'ISSUED'}">
          <c:set var="shown" value="${shown + 1}"/>
          <tr>
            <td><c:out value="${r.bookTitle}"/></td>
            <td><fmt:formatDate value="${r.issueDate}" pattern="dd MMM yyyy"/></td>
            <td><fmt:formatDate value="${r.dueDate}" pattern="dd MMM yyyy"/>
              <c:if test="${r.overdueDays == 0}"><small class="text-muted"> (${r.daysLeft} days left)</small></c:if></td>
            <td><t:statusBadge r="${r}"/></td>
            <td><c:choose><c:when test="${r.fineAmount > 0}">&#8377;<fmt:formatNumber value="${r.fineAmount}" pattern="#,##0.00"/></c:when><c:otherwise>-</c:otherwise></c:choose></td>
          </tr>
        </c:if>
      </c:forEach>
      <c:if test="${shown == 0}"><tr><td colspan="5"><div class="empty"><i class="bi bi-emoji-smile"></i>You have no books to return.</div></td></tr></c:if>
    </tbody></table></div>
</div>

<div class="panel">
  <div class="panel-head"><h2>Borrowing history</h2></div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><th>Book</th><th>Issued</th><th>Due</th><th>Returned</th><th>Fine</th></tr></thead>
    <tbody>
      <c:forEach var="r" items="${records}">
        <tr>
          <td><c:out value="${r.bookTitle}"/></td>
          <td><fmt:formatDate value="${r.issueDate}" pattern="dd MMM yyyy"/></td>
          <td><fmt:formatDate value="${r.dueDate}" pattern="dd MMM yyyy"/></td>
          <td><c:choose><c:when test="${r.status == 'RETURNED'}"><fmt:formatDate value="${r.returnDate}" pattern="dd MMM yyyy"/></c:when><c:otherwise><span class="text-muted">Not yet</span></c:otherwise></c:choose></td>
          <td><c:choose>
            <c:when test="${r.fineStatus == 'PAID'}"><span class="badge-soft green">&#8377;<fmt:formatNumber value="${r.fineAmount}" pattern="#,##0"/> paid</span></c:when>
            <c:when test="${r.fineStatus == 'UNPAID'}"><span class="badge-soft red">&#8377;<fmt:formatNumber value="${r.fineAmount}" pattern="#,##0"/> unpaid</span></c:when>
            <c:otherwise>-</c:otherwise></c:choose></td>
        </tr>
      </c:forEach>
      <c:if test="${empty records}"><tr><td colspan="5"><div class="empty"><i class="bi bi-inbox"></i>No borrowing history yet.</div></td></tr></c:if>
    </tbody></table></div>
</div>
<p class="text-muted small mt-3">Fines are paid at the library desk. Ask the librarian to mark a fine as paid.</p>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
