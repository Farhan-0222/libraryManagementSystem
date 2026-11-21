<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="row g-3">
  <div class="col-lg-5">
    <div class="panel">
      <div class="panel-head"><h2>Issue a book</h2></div>
      <div class="panel-body">
        <form method="post" action="${ctx}/issue">
          <div class="mb-3"><label class="form-label">Student</label>
            <select class="form-select" name="studentId" required>
              <option value="">Choose a student...</option>
              <c:forEach var="s" items="${students}"><option value="${s.studentId}"><c:out value="${s.fullName}"/> (<c:out value="${s.rollNo}"/>)</option></c:forEach>
            </select></div>
          <div class="mb-3"><label class="form-label">Book (only books with copies on the shelf)</label>
            <select class="form-select" name="bookId" required>
              <option value="">Choose a book...</option>
              <c:forEach var="b" items="${books}"><option value="${b.bookId}"><c:out value="${b.title}"/> - <c:out value="${b.author}"/> (${b.availableCopies} left)</option></c:forEach>
            </select></div>
          <div class="alert alert-info py-2">Issue date is today. The book is due back in <strong>${loanDays} days</strong>.</div>
          <button class="btn btn-primary w-100" type="submit"><i class="bi bi-box-arrow-up-right"></i> Issue book</button>
        </form>
      </div>
    </div>
  </div>
  <div class="col-lg-7">
    <div class="panel">
      <div class="panel-head"><h2>Latest transactions</h2></div>
      <div class="table-wrap"><table class="table table-hover">
        <thead><tr><th>Book</th><th>Student</th><th>Due</th><th>Status</th></tr></thead>
        <tbody>
          <c:forEach var="r" items="${recent}"><tr>
            <td><c:out value="${r.bookTitle}"/></td><td><c:out value="${r.studentName}"/></td>
            <td><fmt:formatDate value="${r.dueDate}" pattern="dd MMM yyyy"/></td><td><t:statusBadge r="${r}"/></td></tr></c:forEach>
          <c:if test="${empty recent}"><tr><td colspan="4"><div class="empty"><i class="bi bi-inbox"></i>Nothing issued yet.</div></td></tr></c:if>
        </tbody></table></div>
    </div>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
