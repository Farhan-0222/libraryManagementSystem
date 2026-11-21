<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<ul class="nav nav-pills mb-3 no-print">
  <c:forEach var="t" items="${types}">
    <li class="nav-item"><a class="nav-link ${t[0] == type ? 'active' : ''}" href="${ctx}/reports?type=${t[0]}"><c:out value="${t[1]}"/></a></li>
  </c:forEach>
</ul>
<div class="panel">
  <div class="panel-head">
    <div>
      <h2><c:out value="${reportTitle}"/></h2>
      <small class="text-muted"><c:out value="${sessionScope.libraryName}"/> &middot; generated on ${today} &middot; ${fn:length(body)} row${fn:length(body) == 1 ? '' : 's'}</small>
    </div>
    <div class="d-flex gap-2 no-print">
      <a class="btn btn-outline-primary btn-sm" href="${ctx}/reports?type=${type}&format=csv"><i class="bi bi-filetype-csv"></i> Download CSV</a>
      <button class="btn btn-primary btn-sm" onclick="window.print()"><i class="bi bi-printer"></i> Print</button>
    </div>
  </div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><c:forEach var="h" items="${head}"><th><c:out value="${h}"/></th></c:forEach></tr></thead>
    <tbody>
      <c:forEach var="row" items="${body}"><tr><c:forEach var="cell" items="${row}"><td><c:out value="${cell}"/></td></c:forEach></tr></c:forEach>
      <c:if test="${empty body}"><tr><td colspan="${fn:length(head)}"><div class="empty"><i class="bi bi-file-earmark-x"></i>This report has no records yet.</div></td></tr></c:if>
    </tbody></table></div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
