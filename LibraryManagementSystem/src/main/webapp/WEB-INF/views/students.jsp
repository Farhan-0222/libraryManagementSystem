<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="panel">
  <div class="panel-head">
    <form class="search-bar" method="get" action="${ctx}/students">
      <input class="form-control" name="q" placeholder="Search name, roll number or email" value="<c:out value='${q}'/>">
      <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i> Search</button>
      <c:if test="${not empty q}"><a class="btn btn-outline-secondary" href="${ctx}/students">Clear</a></c:if>
    </form>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#studentModal"><i class="bi bi-person-plus"></i> Register student</button>
  </div>
  <div class="table-wrap"><table class="table table-hover">
    <thead><tr><th>Roll no.</th><th>Name</th><th>Email</th><th>Course</th><th>Phone</th><th class="text-end">Actions</th></tr></thead>
    <tbody>
      <c:forEach var="s" items="${students}">
        <tr>
          <td><c:out value="${s.rollNo}"/></td>
          <td><strong><c:out value="${s.fullName}"/></strong><br><small class="text-muted">@<c:out value="${s.username}"/></small></td>
          <td><c:out value="${s.email}"/></td>
          <td><c:out value="${s.course}"/> &middot; Sem ${s.semester}</td>
          <td><c:out value="${s.phone}"/></td>
          <td class="text-end text-nowrap">
            <a class="btn btn-sm btn-outline-secondary" href="${ctx}/students?history=${s.studentId}"><i class="bi bi-clock-history"></i> History</a>
            <button class="btn btn-sm btn-outline-primary btn-edit" data-bs-toggle="modal" data-bs-target="#studentModal"
              data-student-id="${s.studentId}" data-user-id="${s.userId}" data-full-name="<c:out value='${s.fullName}'/>"
              data-username="<c:out value='${s.username}'/>" data-email="<c:out value='${s.email}'/>" data-roll-no="<c:out value='${s.rollNo}'/>"
              data-course="<c:out value='${s.course}'/>" data-semester="${s.semester}" data-phone="<c:out value='${s.phone}'/>"><i class="bi bi-pencil"></i></button>
            <form method="post" action="${ctx}/students" class="d-inline" onsubmit="return confirm('Delete this student?');">
              <input type="hidden" name="action" value="delete"><input type="hidden" name="studentId" value="${s.studentId}">
              <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty students}"><tr><td colspan="6"><div class="empty"><i class="bi bi-people"></i>No students found.</div></td></tr></c:if>
    </tbody></table></div>
</div>

<div class="modal fade crud-modal" id="studentModal" tabindex="-1" data-entity="student">
  <div class="modal-dialog modal-lg"><form class="modal-content" method="post" action="${ctx}/students">
    <div class="modal-header"><h5 class="modal-title modal-title-text">Add student</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
    <div class="modal-body row g-3">
      <input type="hidden" name="action" value="add"><input type="hidden" name="studentId"><input type="hidden" name="userId">
      <div class="col-md-6"><label class="form-label">Full name</label><input class="form-control" name="fullName" required maxlength="100"></div>
      <div class="col-md-6"><label class="form-label">Roll number</label><input class="form-control" name="rollNo" required maxlength="30"></div>
      <div class="col-md-6"><label class="form-label">Username</label><input class="form-control" name="username" required pattern="[A-Za-z0-9_]{4,30}" title="4-30 letters, digits or underscore"></div>
      <div class="col-md-6"><label class="form-label">Email</label><input class="form-control" type="email" name="email" required></div>
      <div class="col-md-4"><label class="form-label">Course</label><input class="form-control" name="course" value="BCA" required></div>
      <div class="col-md-4"><label class="form-label">Semester</label><input class="form-control" type="number" name="semester" min="1" max="12" required></div>
      <div class="col-md-4"><label class="form-label">Phone</label><input class="form-control" name="phone" pattern="[0-9]{10}" title="10 digits"></div>
      <div class="col-12"><label class="form-label">Password</label><input class="form-control" type="password" name="password" minlength="6"></div>
    </div>
    <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button class="btn btn-primary">Save student</button></div>
  </form></div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
