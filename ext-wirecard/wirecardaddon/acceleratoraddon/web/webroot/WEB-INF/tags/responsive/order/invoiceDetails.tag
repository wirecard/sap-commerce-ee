<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.AbstractOrderData" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<div class="label-order"><spring:theme code="checkout.multi.invoice"/></div>
<div class="row">
    <div class="col-sm-3 col-md-3">
        <span class="item-label">Provider Transaction Reference Id</span>
        <span class="item-value">${fn:escapeXml(orderData.paymentInfo.providerTransactionReferenceId)}</span>
    </div>
    <div class="col-sm-3 col-md-3">
        <span class="item-label">IBAN</span>
        <span class="item-value">${fn:escapeXml(orderData.paymentInfo.iban)}</span>
    </div>
    <div class="col-sm-3 col-md-3">
        <span class="item-label">BIC</span>
        <span class="item-value">${fn:escapeXml(orderData.paymentInfo.bic)}</span>
    </div>
</div>
<div class="row">
    <c:if test="${not empty orderData.paymentInfo.bankName}">
        <div class="col-sm-3 col-md-3">
            <span class="item-label">Bank name</span>
            <span class="item-value">${fn:escapeXml(orderData.paymentInfo.bankName)}</span>
        </div>
    </c:if>
    <c:if test="${not empty orderData.paymentInfo.branchAddress}">
        <div class="col-sm-3 col-md-3">
            <span class="item-label">Branch address</span>
            <span class="item-value">${fn:escapeXml(orderData.paymentInfo.branchAddress)}</span>
        </div>
    </c:if>
    <c:if test="${not empty orderData.paymentInfo.branchState}">
        <div class="col-sm-3 col-md-3">
            <span class="item-label">Branch state</span>
            <span class="item-value">${fn:escapeXml(orderData.paymentInfo.branchState)}</span>
        </div>
    </c:if>
    <c:if test="${not empty orderData.paymentInfo.branchCity}">
        <div class="col-sm-3 col-md-3">
            <span class="item-label">Branch city</span>
            <span class="item-value">${fn:escapeXml(orderData.paymentInfo.branchCity)}</span>
        </div>
    </c:if>
</div>

