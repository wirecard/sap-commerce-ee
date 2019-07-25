<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<spring:htmlEscape defaultHtmlEscape="true"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <jsp:attribute name="pageScripts">
       <script src="${wirecardURL}/engine/hpp/paymentPageLoader.js" type="text/javascript"></script>
        <script>
            var requestData = {
                merchant_account_id:       "${wirecardRequestData.merchantAccountId}",
                request_id:                "${wirecardRequestData.requestId}",
                request_time_stamp:        "${wirecardRequestData.requestTimeStamp}",
                payment_method:            "${wirecardRequestData.paymentMethod}",
                transaction_type:          "${wirecardRequestData.transactionType}",
                requested_amount:          "${wirecardRequestData.requestedAmount}",
                requested_amount_currency: "${wirecardRequestData.requestedAmountCurrency}",
                locale:                    "${wirecardRequestData.locale}",
                request_signature:         "${wirecardRequestData.requestSignature}"
            };
            WirecardPaymentPage.seamlessRenderForm();
        </script>
        
    </jsp:attribute>

    <jsp:body>
        <div class="row">
            <div class="col-sm-6">
                <div class="checkout-headline">
                    <span class="glyphicon glyphicon-lock"></span>
                    <spring:theme code="checkout.multi.secure.checkout"/>
                </div>
                <multiCheckout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                    <jsp:body>
                        <div class="checkout-paymentmethod">
                            <div class="checkout-indent">
                                <div class="headline"><spring:theme code="checkout.multi.paymentMethod"/></div>

                                <ycommerce:testId code="wirecardPaymentDetailsForm">
                                    <form:form id="wirecardPaymentDetailsForm" name="wirecardPaymentDetailsForm" commandName="wirecardPaymentDetailsForm" action="./add" method="POST">

                                        <input type="hidden" id="tokenId" value="" name="tokenId">
                                        <c:forEach var="paymentMethod" items="${paymentMethodList}">
                                            <c:set var="countries">
                                                [
                                                <c:forEach var="country" items="${paymentMethod.supportedBillingCountries}" varStatus="status">
                                                    <c:if test="${status.index ne 0}">,</c:if>
                                                    {
                                                    "name" : "${fn:escapeXml(country.name)}",
                                                    "isocode" : "${fn:escapeXml(country.isocode)}"
                                                    }
                                                </c:forEach>
                                                ]
                                            </c:set>
                                            <div class="paymentMethods">
                                                <input type="radio" id="wd-${paymentMethod.paymentAlias}" value="${paymentMethod.code}" name="paymentMethodChosen" data-countries='${countries}' data-sameaddress='${paymentMethod.sameAddress}'>
                                                <img src="${paymentMethod.media.url}" alt="${paymentMethod.name}"/>
                                                <div class="description">${paymentMethod.description}</div>
                                            </div>
                                            <c:if test="${paymentMethod.code == 'wd-sepa'}">
                                                <div class="form-group row" id="sepa-credentials-div" style="display: none">
                                                    <div class="col-sm-12">
                                                        <label for="bankAccountOwner-SEPA-DD"><spring:theme code="checkout.wirecard.payment.bank.accountOwner"/></label>
                                                        <small id="bankAccountOwner-required" class="pull-right" style="display: none">
                                                            <spring:theme code="checkout.wirecard.payment.field.required"/></small>
                                                        <div class="form-group">
                                                            <input name="bankAccountOwner" id="bankAccountOwner-SEPA-DD" class="form-control" type="text">
                                                        </div>
                                                    </div>
                                                    <div class="col-sm-12">
                                                        <label for="bankAccountIban-SEPA-DD"><spring:theme code="checkout.wirecard.payment.bank.iban"/></label>
                                                        <div class="form-group">
                                                            <input name="bankAccountIban" id="bankAccountIban-SEPA-DD" class="form-control" type="text" autocomplete="off">
                                                        </div>
                                                    </div>
                                                    <c:if test="${paymentMethod.showOptionalFormField}">
                                                        <div class="col-sm-12">
                                                            <label for="bankBic-SEPA-DD"><spring:theme code="checkout.wirecard.payment.bank.bic"/></label>
                                                            <div class="form-group">
                                                                <input name="bankBic" id="bankBic-SEPA-DD" class="form-control" type="text" autocomplete="off">
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </c:if>
                                        </c:forEach>

                                        <div class="form-group" id="saved-cards-div" style="display: none">
                                            <c:if test="${not empty paymentInfos}">
                                                <button type="button" class="btn btn-default btn-block js-saved-payments">
                                                    <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useSavedCard"/></button>
                                            </c:if>
                                        </div>

                                        <div id="creditcard-form-div" class="seamless-form-div" style="display: none"></div>


                                        <div id="save-payment-div" style="display: none">
                                            <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
                                                <formElement:formCheckbox idKey="savePaymentMethod" labelKey="checkout.multi.sop.savePaymentInfo" path="saveInAccount"
                                                                          inputCSS="" labelCSS="" mandatory="false" tabindex="10"/>
                                            </sec:authorize>
                                        </div>

                                        <div id="ideal-selector" style="display: none" class="col-sm-12">
                                            <div class="form-group">
                                                <div class="col-sm-12" class="col-sm-12">
                                                    <select name="ideal-bic" id="financialInstitution-IDEAL-DD" class="form-control col-sm-4">
                                                        <option value="ABNANL2A">
                                                            <spring:theme code="checkout.wirecard.bic.name.abn"/></option>
                                                        <option value="ASNBNL21">
                                                            <spring:theme code="checkout.wirecard.bic.name.asn"/></option>
                                                        <option value="BUNQNL2A">
                                                            <spring:theme code="checkout.wirecard.bic.name.bunq"/></option>
                                                        <option value="INGBNL2A">
                                                            <spring:theme code="checkout.wirecard.bic.name.ing"/></option>
                                                        <option value="KNABNL2H">
                                                            <spring:theme code="checkout.wirecard.bic.name.knab"/></option>
                                                        <option value="RABONL2U">
                                                            <spring:theme code="checkout.wirecard.bic.name.rabobank"/></option>
                                                        <option value="SNSBNL2A">
                                                            <spring:theme code="checkout.wirecard.bic.name.sns"/></option>
                                                        <option value="RGGINL21">
                                                            <spring:theme code="checkout.wirecard.bic.name.regiobank"/></option>
                                                        <option value="TRIONL2U">
                                                            <spring:theme code="checkout.wirecard.bic.name.triodos"/></option>
                                                        <option value="FVLBNL22">
                                                            <spring:theme code="checkout.wirecard.bic.name.vanlanschot"/></option>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                        <div id="billingAdrressInfo" style="display:block">
                                            <h1 class="headline">
                                                <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/></h1>

                                            <formElement:formCheckbox idKey="differentAddress" path="newBillingAddress" labelKey="checkout.multi.paymentMethod.addPaymentDetails.enterDifferentBillingAddress"/>

                                            <div id="paymentDetailsContent" class="select2-group">
                                                <form:hidden path="billingAddress.addressId" class="create_update_address_id"/>
                                                <formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="text" mandatory="true"/>
                                                <formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billingAddress.lastName" inputCSS="text" mandatory="true"/>
                                                <formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="billingAddress.line1" inputCSS="text" mandatory="true"/>
                                                <formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="billingAddress.line2" inputCSS="text" mandatory="false"/>
                                                <formElement:formSelectBox idKey="addresscountry" labelKey="address.country" path="billingAddress.regionIso" mandatory="false" skipBlank="false" skipBlankMessageKey="address.country"
                                                                           items="${billingCountries}" itemValue="isocode" selectCSSClass="form-control"/>
                                                <formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="text" mandatory="true"/>
                                                <formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="text" mandatory="true"/>
                                                <formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="billingAddress.phone" inputCSS="text" mandatory="false"/>
                                                <form:hidden path="billingAddress.countryIso" value="${billingAddress.isocode}"/>
                                                <form:hidden path="billingAddress.shippingAddress"/>
                                                <form:hidden path="billingAddress.billingAddress"/>
                                            </div>
                                        </div>
                                    </form:form>
                                </ycommerce:testId>
                            </div>
                        </div>
                        <button type="button" class="btn btn-primary btn-block submit_wirecardPaymentDetailsForm checkout-next">
                            <spring:theme code="checkout.multi.paymentMethod.continue"/></button>
                    </jsp:body>
                </multiCheckout:checkoutSteps>
            </div>
            <div style="display:none" id="popup_sepa_mandate_conditions_form">
                <div class="pad_left" id="popup_sepa_mandate_conditions">
                    <div class="span-13 ">
                        <div class="textpage textpage-termsAndConditions">

                            <spring:theme code="${labelKey}" arguments="${labelArguments}"/>
                            <p>${sepaMandateData.creditorId}</p>
                            <p>${sepaMandateData.creditorName}</p>
                            <p>${sepaMandateData.storeCity}</p>
                            <p>
                                <spring:theme code="checkout.wirecard.payment.sepa.consent.one" arguments="${sepaMandateData.creditorName}" htmlEscape="false"/></p>
                            <p><spring:theme code="checkout.wirecard.payment.sepa.consent.two" htmlEscape="false"/></p>
                            <p>
                                <spring:theme code="checkout.wirecard.payment.sepa.consent.three" arguments="${sepaMandateData.creditorName}" htmlEscape="false"/></p>
                            <p>
                                <spring:theme code="checkout.wirecard.payment.sepa.consent.sign" arguments="${sepaMandateData.storeCity}"/></p>
                            <p id="accountHolderSepa"></p>
                            <p></p>


                            <div class="checkbox">
                                <label class="control-label uncased">
                                    <input type="checkbox" id="sepaMandateChkConditions" name="sepaMandateChkConditions"/>
                                    <spring:theme code="checkout.wirecard.payment.sepa.consent.four" htmlEscape="false"/>
                                </label>
                            </div>

                            <button id="sepaMandateButton" class="btn btn-primary">
                                <spring:theme code="checkout.wirecard.payment.sepa.consent.proceed"></spring:theme>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <c:if test="${not empty paymentInfos}">
                <div id="savedpayments">
                    <div id="savedpaymentstitle">
                        <div class="headline">
                            <span class="headline-text"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useSavedCard"/></span>
                        </div>
                    </div>
                    <div id="savedpaymentsbody">
                        <c:forEach items="${paymentInfos}" var="paymentInfo" varStatus="status">
                            <div class="saved-payment-entry">
                                <form id="WirecardPaymentDetailsForm" name="wirecardPaymentDetailsForm" commandName="wirecardPaymentDetailsForm" action="${request.contextPath}/checkout/multi/wirecard/payment-method/add" method="POST">
                                    <input type="hidden" name="tokenId" value="${fn:escapeXml(paymentInfo.token)}"/>
                                    <input type="hidden" name="paymentMethodChosen" value="${creditCardCode}"/>
                                    <input type="hidden" name="isSavedCC" value="true"/>
                                    <ul>
                                        <strong>${fn:escapeXml(paymentInfo.billingAddress.firstName)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.lastName)}</strong><br/>
                                            ${fn:escapeXml(paymentInfo.cardTypeData.name)}<br/>
                                            ${fn:escapeXml(paymentInfo.cardNumber)}<br/>
                                        <c:if test="${not empty paymentInfo.expiryMonth}">
                                            <spring:theme code="checkout.multi.paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(paymentInfo.expiryMonth)},${fn:escapeXml(paymentInfo.expiryYear)}"/><br/>
                                        </c:if>
                                            ${fn:escapeXml(paymentInfo.billingAddress.line1)}<br/>
                                            ${fn:escapeXml(paymentInfo.billingAddress.town)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.region.isocodeShort)}<br/>
                                            ${fn:escapeXml(paymentInfo.billingAddress.postalCode)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.country.isocode)}<br/>
                                    </ul>
                                    <button type="submit" class="btn btn-primary btn-block" tabindex="${(status.count * 2) - 1}">
                                        <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useThesePaymentDetails"/></button>
                                </form>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
            <div class="col-sm-6 hidden-xs">
                <multiCheckout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true"/>
            </div>
            <div class="col-sm-12 col-lg-12">
                <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                    <cms:component component="${feature}"/>
                </cms:pageSlot>
            </div>
        </div>
    </jsp:body>
</template:page>