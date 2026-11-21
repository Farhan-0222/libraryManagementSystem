<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="panel">
  <div class="panel-head">
    <form class="search-bar" method="get" action="${ctx}/books">
      <input class="form-control" name="q" placeholder="Search title, author, ISBN or category" value="<c:out value='${q}'/>">
      <select class="form-select" name="category" style="width:auto" onchange="this.form.submit()">
        <option value="0">All categories</option>
        <c:forEach var="c" items="${categories}"><option value="${c.categoryId}" ${c.categoryId == selectedCategory ? 'selected' : ''}><c:out value="${c.name}"/></option></c:forEach>
      </select>
      <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i> Search</button>
      <c:if test="${not empty q || selectedCategory > 0}"><a class="btn btn-outline-secondary" href="${ctx}/books">Clear</a></c:if>
    </form>
    <div class="d-flex gap-2">
      <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#catModal"><i class="bi bi-tags"></i> Add category</button>
      <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#bookModal"><i class="bi bi-plus-lg"></i> Add book</button>
    </div>
  </div>
  <div class="table-wrap">
    <table class="table table-hover">
      <thead><tr><th>ID</th><th>Title</th><th>ISBN</th><th>Category</th><th>Year</th><th>Copies</th><th class="text-end">Actions</th></tr></thead>
      <tbody>
        <c:forEach var="b" items="${books}">
          <tr>
            <td>${b.bookId}</td>
            <td><strong><c:out value="${b.title}"/></strong><br><small class="text-muted"><c:out value="${b.author}"/> &middot; <c:out value="${b.publisher}"/></small></td>
            <td><c:out value="${b.isbn}"/></td>
            <td><span class="badge-soft blue"><c:out value="${b.categoryName}"/></span></td>
            <td>${b.publicationYear}</td>
            <td><span class="badge-soft ${b.availableCopies > 0 ? 'green' : 'red'}">${b.availableCopies} of ${b.totalCopies} available</span></td>
            <td class="text-end text-nowrap">
              <button class="btn btn-sm btn-outline-primary btn-edit" data-bs-toggle="modal" data-bs-target="#bookModal"
                data-book-id="${b.bookId}" data-isbn="<c:out value='${b.isbn}'/>" data-title="<c:out value='${b.title}'/>"
                data-author="<c:out value='${b.author}'/>" data-publisher="<c:out value='${b.publisher}'/>"
                data-category-id="${b.categoryId}" data-publication-year="${b.publicationYear}"
                data-total-copies="${b.totalCopies}" data-cover-url="<c:out value='${b.coverUrl}'/>"><i class="bi bi-pencil"></i> Edit</button>
              <form method="post" action="${ctx}/books" class="d-inline" onsubmit="return confirm('Delete this book?');">
                <input type="hidden" name="action" value="delete"><input type="hidden" name="bookId" value="${b.bookId}">
                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty books}"><tr><td colspan="7"><div class="empty"><i class="bi bi-journal-x"></i>No books match. Try another search or add a new book.</div></td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<%-- Add / Edit book --%>
<div class="modal fade crud-modal" id="bookModal" tabindex="-1" data-entity="book">
  <div class="modal-dialog modal-lg"><form class="modal-content" method="post" action="${ctx}/books">
    <div class="modal-header"><h5 class="modal-title modal-title-text">Add book</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
    <div class="modal-body row g-3">
      <input type="hidden" name="action" value="add"><input type="hidden" name="bookId">
      <div class="col-md-6"><label class="form-label">ISBN</label><input class="form-control" name="isbn" required pattern="[0-9\-]{10,17}" title="10-17 digits, hyphens allowed"></div>
      <div class="col-md-6"><label class="form-label">Category</label>
        <select class="form-select" name="categoryId" required><option value="">Choose...</option>
          <c:forEach var="c" items="${categories}"><option value="${c.categoryId}"><c:out value="${c.name}"/></option></c:forEach></select></div>
      <div class="col-12"><label class="form-label">Title</label><input class="form-control" name="title" required maxlength="200"></div>
      <div class="col-md-6"><label class="form-label">Author</label><input class="form-control" name="author" required maxlength="120"></div>
      <div class="col-md-6"><label class="form-label">Publisher</label><input class="form-control" name="publisher" maxlength="120"></div>
      <div class="col-md-4"><label class="form-label">Publication year</label><input class="form-control" type="number" name="publicationYear" min="1000" max="2100" required></div>
      <div class="col-md-4"><label class="form-label">Total copies</label><input class="form-control" type="number" name="totalCopies" min="0" required></div>
      <div class="col-md-4"><label class="form-label">Cover image URL (optional)</label><input class="form-control" name="coverUrl" maxlength="255"></div>
    </div>
    <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button class="btn btn-primary">Save book</button></div>
  </form></div>
</div>

<%-- Add category --%>
<div class="modal fade" id="catModal" tabindex="-1">
  <div class="modal-dialog"><form class="modal-content" method="post" action="${ctx}/books">
    <div class="modal-header"><h5 class="modal-title">Add category</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
    <div class="modal-body"><input type="hidden" name="action" value="addCategory">
      <label class="form-label">Category name</label><input class="form-control" name="name" required maxlength="60"></div>
    <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button class="btn btn-primary">Save category</button></div>
  </form></div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
