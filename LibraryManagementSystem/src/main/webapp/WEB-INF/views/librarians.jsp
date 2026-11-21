<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="panel">
  <div class="panel-head">
    <form class="search-bar" method="get" action="${ctx}/librarians">
      <input class="form-control" name="q" placeholder="Search name, username or employee code" value="<c:out value='${q}'/>">
      <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i> Search</button>
    </form>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#libModal"><i class="bi bi-person-plus"></i> Add librarian</button>
  </div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><th>Employee code</th><th>Name</th><th>Email</th><th>Phone</th><th class="text-end">Actions</th></tr></thead>
    <tbody>
      <c:forEach var="l" items="${librarians}"><tr>
        <td><c:out value="${l.employeeCode}"/></td>
        <td><strong><c:out value="${l.fullName}"/></strong><br><small class="text-muted">@<c:out value="${l.username}"/></small></td>
        <td><c:out value="${l.email}"/></td><td><c:out value="${l.phone}"/></td>
        <td class="text-end text-nowrap">
          <button class="btn btn-sm btn-outline-primary btn-edit" data-bs-toggle="modal" data-bs-target="#libModal"
            data-user-id="${l.userId}" data-full-name="<c:out value='${l.fullName}'/>" data-username="<c:out value='${l.username}'/>"
            data-email="<c:out value='${l.email}'/>" data-employee-code="<c:out value='${l.employeeCode}'/>" data-phone="<c:out value='${l.phone}'/>"><i class="bi bi-pencil"></i> Edit</button>
          <form method="post" action="${ctx}/librarians" class="d-inline" onsubmit="return confirm('Delete this librarian account?');">
            <input type="hidden" name="action" value="delete"><input type="hidden" name="userId" value="${l.userId}">
            <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
          </form>
        </td></tr></c:forEach>
      <c:if test="${empty librarians}"><tr><td colspan="5"><div class="empty"><i class="bi bi-person-badge"></i>No librarian accounts found.</div></td></tr></c:if>
    </tbody></table></div>
</div>

<div class="modal fade crud-modal" id="libModal" tabindex="-1" data-entity="librarian">
  <div class="modal-dialog"><form class="modal-content" method="post" action="${ctx}/librarians">
    <div class="modal-header"><h5 class="modal-title modal-title-text">Add librarian</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
    <div class="modal-body row g-3">
      <input type="hidden" name="action" value="add"><input type="hidden" name="userId">
      <div class="col-12"><label class="form-label">Full name</label><input class="form-control" name="fullName" required maxlength="100"></div>
      <div class="col-md-6"><label class="form-label">Username</label><input class="form-control" name="username" required pattern="[A-Za-z0-9_]{4,30}" title="4-30 letters, digits or underscore"></div>
      <div class="col-md-6"><label class="form-label">Employee code</label><input class="form-control" name="employeeCode" required maxlength="30"></div>
      <div class="col-md-6"><label class="form-label">Email</label><input class="form-control" type="email" name="email" required></div>
      <div class="col-md-6"><label class="form-label">Phone</label><input class="form-control" name="phone" pattern="[0-9]{10}" title="10 digits"></div>
      <div class="col-12"><label class="form-label">Password</label><input class="form-control" type="password" name="password" minlength="6"></div>
    </div>
    <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button class="btn btn-primary">Save librarian</button></div>
  </form></div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
