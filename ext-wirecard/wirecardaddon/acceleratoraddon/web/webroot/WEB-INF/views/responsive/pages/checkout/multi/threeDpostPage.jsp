<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spring:htmlEscape defaultHtmlEscape="true"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <form:form id="downloadForm" name="downloadForm" action="${acsUrl}" method="POST">
        <input type="hidden" name="PaReq" value="${paReq}"/>
        <input type="hidden" name="TermUrl" value="${termUrl}"/>
        <input type="hidden" name="MD" value=""/>
    </form:form>
</template:page>