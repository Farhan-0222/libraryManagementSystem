<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="panel">
  <div class="panel-head">
    <form class="search-bar" method="get" action="${ctx}/return">
      <input class="form-control" name="q" placeholder="Search by student, roll number or book" value="<c:out value='${q}'/>">
      <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i> Search</button>
      <c:if test="${not empty q}"><a class="btn btn-outline-secondary" href="${ctx}/return">Clear</a></c:if>
    </form>
    <span class="text-muted">${fn:length(records)} book${fn:length(records) == 1 ? '' : 's'} currently issued</span>
  </div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><th>Book</th><th>Student</th><th>Issued</th><th>Due</th><th>Status</th><th>Fine if returned today</th><th class="text-end">Action</th></tr></thead>
    <tbody>
      <c:forEach var="r" items="${records}"><tr>
        <td><c:out value="${r.bookTitle}"/></td>
        <td><c:out value="${r.studentName}"/> <small class="text-muted">(<c:out value="${r.rollNo}"/>)</small></td>
        <td><fmt:formatDate value="${r.issueDate}" pattern="dd MMM yyyy"/></td>
        <td><fmt:formatDate value="${r.dueDate}" pattern="dd MMM yyyy"/></td>
        <td><t:statusBadge r="${r}"/></td>
        <td><c:choose><c:when test="${r.fineAmount > 0}"><strong class="text-danger">&#8377;<fmt:formatNumber value="${r.fineAmount}" pattern="#,##0.00"/></strong></c:when><c:otherwise>-</c:otherwise></c:choose></td>
        <td class="text-end">
          <form method="post" action="${ctx}/return" class="d-inline"
                onsubmit="return confirm('Accept the return of this book?');">
            <input type="hidden" name="issueId" value="${r.issueId}">
            <button class="btn btn-sm btn-primary"><i class="bi bi-box-arrow-in-down-left"></i> Accept return</button>
          </form>
        </td></tr></c:forEach>
      <c:if test="${empty records}"><tr><td colspan="7"><div class="empty"><i class="bi bi-check2-all"></i>No issued books found.</div></td></tr></c:if>
    </tbody></table></div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
