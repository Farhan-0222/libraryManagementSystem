<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<a href="${ctx}/students" class="btn btn-sm btn-outline-secondary mb-3"><i class="bi bi-arrow-left"></i> All students</a>
<div class="panel mb-4"><div class="panel-body">
  <h2 class="h5 mb-1"><c:out value="${student.fullName}"/></h2>
  <span class="text-muted">Roll no. <c:out value="${student.rollNo}"/> &middot; <c:out value="${student.course}"/> semester ${student.semester} &middot; <c:out value="${student.email}"/></span>
</div></div>
<div class="panel">
  <div class="panel-head"><h2>Borrowing history (${fn:length(records)})</h2></div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><th>Book</th><th>Issued</th><th>Due</th><th>Returned</th><th>Status</th><th>Fine</th></tr></thead>
    <tbody>
      <c:forEach var="r" items="${records}"><tr>
        <td><c:out value="${r.bookTitle}"/></td>
        <td><fmt:formatDate value="${r.issueDate}" pattern="dd MMM yyyy"/></td>
        <td><fmt:formatDate value="${r.dueDate}" pattern="dd MMM yyyy"/></td>
        <td><c:choose><c:when test="${r.status == 'RETURNED'}"><fmt:formatDate value="${r.returnDate}" pattern="dd MMM yyyy"/></c:when><c:otherwise>-</c:otherwise></c:choose></td>
        <td><t:statusBadge r="${r}"/></td>
        <td><c:choose><c:when test="${r.fineAmount > 0}">&#8377;<fmt:formatNumber value="${r.fineAmount}" pattern="#,##0.00"/> <small class="text-muted">(${fn:toLowerCase(r.fineStatus)})</small></c:when><c:otherwise>-</c:otherwise></c:choose></td>
      </tr></c:forEach>
      <c:if test="${empty records}"><tr><td colspan="6"><div class="empty"><i class="bi bi-inbox"></i>This student has not borrowed any book yet.</div></td></tr></c:if>
    </tbody></table></div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
