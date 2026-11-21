<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="r" required="true" type="com.library.model.IssueRecord" %>
<%-- Shows Returned / Overdue / Due soon / Issued for one issue record --%>
<c:choose>
  <c:when test="${r.status == 'RETURNED'}"><span class="badge-soft green">Returned</span></c:when>
  <c:when test="${r.overdueDays > 0}"><span class="badge-soft red">Overdue ${r.overdueDays} day${r.overdueDays > 1 ? 's' : ''}</span></c:when>
  <c:when test="${r.daysLeft <= 2}"><span class="badge-soft amber">Due ${r.daysLeft == 0 ? 'today' : 'soon'}</span></c:when>
  <c:otherwise><span class="badge-soft blue">Issued</span></c:otherwise>
</c:choose>
