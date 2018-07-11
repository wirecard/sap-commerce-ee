<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="paymentInfo" required="true" type="com.wirecard.hybris.core.converter.data.WirecardPaymentInfoData" %>
<%@ attribute name="showPaymentInfo" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<c:if test="${not empty paymentInfo && showPaymentInfo}">
    <ul class="checkout-order-summary-list">
        <li class="checkout-order-summary-list-heading">
            <div class="title"><spring:theme code="checkout.multi.payment" text="Payment:"/></div>
            <div class="address">
                <c:if test="${not empty paymentInfo.billingAddress}"> ${fn:escapeXml(paymentInfo.billingAddress.title)}</c:if>
                <c:if test="${not empty paymentInfo.billingAddress}">${fn:escapeXml(paymentInfo.billingAddress.firstName)} ${fn:escapeXml(paymentInfo.billingAddress.lastName)},<br/>${fn:escapeXml(paymentInfo.billingAddress.line1)},
                    <c:if test="${not empty paymentInfo.billingAddress.line2}">${fn:escapeXml(paymentInfo.billingAddress.line2)},</c:if>
                    ${fn:escapeXml(paymentInfo.billingAddress.town)}, ${fn:escapeXml(paymentInfo.billingAddress.region.name)}&nbsp;${fn:escapeXml(paymentInfo.billingAddress.postalCode)}, ${fn:escapeXml(paymentInfo.billingAddress.country.name)}
                </c:if>
                <br/><c:if test="${not empty paymentInfo.billingAddress.phone }">${fn:escapeXml(paymentInfo.billingAddress.phone)}</c:if>
            </div>
        </li>
        <c:if test="${not empty paymentInfo.iban && cartData.paymentMode.showOptionalFormField}">
            <li class="checkout-order-summary-list-heading">
                <div class="title"><spring:theme code="checkout.multi.invoice"/></div>
                <br/>
                <div class="address">
                    <ul><b>ProviderTransactionReferenceId: </b> ${fn:escapeXml(paymentInfo.providerTransactionReferenceId)}</ul>
                    <ul><b>IBAN: </b> ${fn:escapeXml(paymentInfo.iban)}</ul>
                    <ul><b>BIC: </b> ${fn:escapeXml(paymentInfo.bic)}</ul>
                    <ul><c:if test="${not empty paymentInfo.bankName}">     <b>Bank name: </b> ${fn:escapeXml(paymentInfo.bankName)}</c:if></ul>
                    <ul><c:if test="${not empty paymentInfo.branchAddress}"><b>Branch address: </b> ${fn:escapeXml(paymentInfo.branchAddress)}</c:if></ul>
                    <ul><c:if test="${not empty paymentInfo.branchCity}">   <b>Branch city: </b> ${fn:escapeXml(paymentInfo.branchCity)}</c:if></ul>
                    <ul><c:if test="${not empty paymentInfo.branchState}">  <b>Branch state: </b> ${fn:escapeXml(paymentInfo.branchState)}</c:if></ul>
                </div>
            </li>
        </c:if>
    </ul>
</c:if>

