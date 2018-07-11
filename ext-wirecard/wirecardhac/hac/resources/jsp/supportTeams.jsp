<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>
<html>
<head>
    <title>Wirecard support teams</title>
</head>
<body>
<div class="prepend-top span-17" id="content">
    <div class="marginLeft" id="inner">
        <h2>Support teams.</h2>
        <div>
            <a href="mailto:support@wirecard.com">support@wirecard.com</a> "Support Team Wirecard AG, Germany"
        </div>
        <div>
            <a href="mailto:support.at@wirecard.com">support.at@wirecard.com</a> "Support Team Wirecard CEE, Austria"
        </div>
        <div>
            <a href="mailto:support.sg@wirecard.com">support.sg@wirecard.com</a> "Support Team Wirecard Singapore" where Support Team
            Germany is the default value.
        </div>
        <br/>
    </div>
</div>
<div id="dialogContainer"></div>
</body>
</html>

