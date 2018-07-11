<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>
<html>
<head>
    <title>Wirecard plugin info</title>
</head>
<body>
<div class="prepend-top span-17" id="content">
    <div class="marginLeft" id="inner">
        <h2>Wirecard plugin.</h2>

        <c:forEach items="${extensions}" var="extension">
            <div>
                    ${extension}
            </div>
        </c:forEach>

        <br/>
    </div>
</div>
<div id="dialogContainer"></div>
</body>
</html>

