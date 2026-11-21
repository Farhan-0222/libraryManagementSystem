<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="panel" style="max-width:640px">
  <div class="panel-head"><h2>Library rules</h2></div>
  <div class="panel-body">
    <form method="post" action="${ctx}/settings" class="row g-3">
      <div class="col-12"><label class="form-label">Library name</label>
        <input class="form-control" name="library_name" required maxlength="80" value="<c:out value='${cfg.library_name}'/>"></div>
      <div class="col-md-4"><label class="form-label">Fine per overdue day (&#8377;)</label>
        <input class="form-control" type="number" step="0.5" min="0" max="1000" name="fine_per_day" required value="<c:out value='${cfg.fine_per_day}'/>"></div>
      <div class="col-md-4"><label class="form-label">Loan period (days)</label>
        <input class="form-control" type="number" min="1" max="90" name="loan_days" required value="<c:out value='${cfg.loan_days}'/>"></div>
      <div class="col-md-4"><label class="form-label">Max books per student</label>
        <input class="form-control" type="number" min="1" max="20" name="max_books" required value="<c:out value='${cfg.max_books}'/>"></div>
      <div class="col-12"><button class="btn btn-primary" type="submit">Save settings</button></div>
      <div class="col-12 text-muted small">New rules apply to books issued or returned from now on.</div>
    </form>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
