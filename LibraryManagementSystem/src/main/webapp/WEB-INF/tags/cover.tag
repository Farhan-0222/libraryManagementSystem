<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="b" required="true" type="com.library.model.Book" %>
<%-- Book cover: uses cover_url if given, otherwise tries Open Library by ISBN. Falls back to a blue placeholder. --%>
<c:choose>
  <c:when test="${empty b.coverUrl}"><c:set var="src" value="https://covers.openlibrary.org/b/isbn/${fn:replace(b.isbn, '-', '')}-M.jpg?default=false"/></c:when>
  <c:otherwise><c:set var="src" value="${b.coverUrl}"/></c:otherwise>
</c:choose>
<div class="cover">
  <div class="ph"><i class="bi bi-book"></i><c:out value="${b.title}"/></div>
  <img src="<c:out value='${src}'/>" alt="" loading="lazy" onerror="coverFail(this)">
</div>
