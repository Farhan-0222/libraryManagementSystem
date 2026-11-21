<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="panel mb-4"><div class="panel-body">
  <form class="search-bar" method="get" action="${ctx}/catalog">
    <input class="form-control flex-grow-1" name="q" placeholder="Search by book name, author, ISBN or category" value="<c:out value='${q}'/>">
    <select class="form-select" name="category" style="width:auto">
      <option value="0">All categories</option>
      <c:forEach var="c" items="${categories}"><option value="${c.categoryId}" ${c.categoryId == selectedCategory ? 'selected' : ''}><c:out value="${c.name}"/></option></c:forEach>
    </select>
    <div class="form-check align-self-center ms-1">
      <input class="form-check-input" type="checkbox" name="available" value="1" id="av" ${onlyAvailable ? 'checked' : ''}>
      <label class="form-check-label" for="av">Available only</label>
    </div>
    <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i> Search</button>
  </form>
</div></div>

<p class="text-muted">${fn:length(books)} book${fn:length(books) == 1 ? '' : 's'} found</p>
<div class="row g-3">
  <c:forEach var="b" items="${books}">
    <div class="col-6 col-md-4 col-xl-3">
      <div class="book-card">
        <t:cover b="${b}"/>
        <div class="info">
          <div class="t"><c:out value="${b.title}"/></div>
          <div class="a"><c:out value="${b.author}"/></div>
          <div><span class="badge-soft blue"><c:out value="${b.categoryName}"/></span></div>
          <div class="mt-auto pt-2 d-flex justify-content-between align-items-center">
            <c:choose>
              <c:when test="${b.availableCopies > 0}"><span class="badge-soft green">${b.availableCopies} available</span></c:when>
              <c:otherwise><span class="badge-soft red">All issued</span></c:otherwise>
            </c:choose>
            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#detailModal"
              data-title="<c:out value='${b.title}'/>" data-author="<c:out value='${b.author}'/>" data-isbn="<c:out value='${b.isbn}'/>"
              data-publisher="<c:out value='${b.publisher}'/>" data-category="<c:out value='${b.categoryName}'/>"
              data-year="${b.publicationYear}" data-copies="${b.availableCopies} of ${b.totalCopies} copies available">Details</button>
          </div>
        </div>
      </div>
    </div>
  </c:forEach>
</div>
<c:if test="${empty books}"><div class="panel"><div class="empty"><i class="bi bi-search"></i>No books found. Try a different word or remove the filters.</div></div></c:if>

<div class="modal fade" id="detailModal" tabindex="-1"><div class="modal-dialog"><div class="modal-content">
  <div class="modal-header"><div><h5 class="modal-title mb-0" id="dTitle"></h5><small class="text-muted" id="dAuthor"></small></div><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
  <div class="modal-body"><table class="table table-sm mb-0">
    <tr><th style="width:38%">ISBN</th><td id="d_isbn"></td></tr>
    <tr><th>Publisher</th><td id="d_publisher"></td></tr>
    <tr><th>Category</th><td id="d_category"></td></tr>
    <tr><th>Publication year</th><td id="d_year"></td></tr>
    <tr><th>Availability</th><td id="d_copies"></td></tr>
  </table></div>
</div></div></div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
