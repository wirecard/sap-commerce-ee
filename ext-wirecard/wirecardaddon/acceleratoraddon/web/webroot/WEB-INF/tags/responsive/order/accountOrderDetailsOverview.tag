<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="wirecardorder" tagdir="/WEB-INF/tags/addons/wirecardaddon/responsive/order" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<div class="order-detail-overview">
    <div class="row">
        <div class="col-sm-2 col-md-2">
            <div class="item-group">
                <ycommerce:testId code="orderDetail_overviewOrderID_label">
                    <span class="item-label"><spring:theme code="text.account.orderHistory.orderNumber"/></span>
                    <span class="item-value">${fn:escapeXml(order.code)}</span>
                </ycommerce:testId>
            </div>
        </div>
        <c:if test="${not empty orderData.statusDisplay}">
            <div class="col-sm-2 col-md-2">
                <div class="item-group">
                    <ycommerce:testId code="orderDetail_overviewOrderStatus_label">
                        <span class="item-label"><spring:theme code="text.account.orderHistory.orderStatus"/></span>
                        <span class="item-value"><spring:theme
                                code="text.account.order.status.display.${fn:escapeXml(order.statusDisplay)}"/></span>
                    </ycommerce:testId>
                </div>
            </div>
        </c:if>
        <div class="col-sm-2 col-md-2">
            <div class="item-group">
                <ycommerce:testId code="orderDetail_overviewPaymentStatus_label">
                    <span class="item-label"><spring:theme code="text.account.orderHistory.paymentStatus"/></span>
                    <span class="item-value"><spring:theme code="checkout.wirecard.payment.status.${orderData.paymentStatus}"/></span>
                </ycommerce:testId>
            </div>
        </div>
        <div class="col-sm-2 col-md-2">
            <div class="item-group">
                <ycommerce:testId code="orderDetail_overviewStatusDate_label">
                    <span class="item-label"><spring:theme code="text.account.orderHistory.datePlaced"/></span>
                    <span class="item-value"><fmt:formatDate value="${order.created}" dateStyle="medium" timeStyle="short"
                                                             type="both"/></span>
                </ycommerce:testId>
            </div>
        </div>
        <div class="col-sm-2 col-md-2">
            <div class="item-group">
                <ycommerce:testId code="orderDetail_overviewOrderTotal_label">
                    <span class="item-label"><spring:theme code="text.account.order.total"/></span>
                    <span class="item-value"><format:price priceData="${order.totalPrice}"/></span>
                </ycommerce:testId>
            </div>
        </div>
        <div class="col-sm-2 col-md-2" style="${style}">
            <div class="item-action">
                <c:set var="orderCode" value="${fn:escapeXml(order.code)}" scope="request"/>
                <c:set var="orderCancellable" value="${order.cancellable}" scope="request"/>
                <c:set var="orderReturnable" value="${order.returnable}" scope="request"/>
                <action:actions element="div" parentComponent="${component}"/>
                <script type="text/javascript" src="<c:url value="/static/js/jquery.dataTables.min.js"/>"></script>
            </div>
        </div>
    </div>
    <c:if test="${orderData.paymentMode.showOptionalFormField == true && not empty orderData.paymentInfo.iban}">
        <div class="row">
            <div class="col-sm-12 col-md-12 order-invoice-details">
                <wirecardorder:invoiceDetails order="${orderData}"/>
            </div>
        </div>
    </c:if>
</div>