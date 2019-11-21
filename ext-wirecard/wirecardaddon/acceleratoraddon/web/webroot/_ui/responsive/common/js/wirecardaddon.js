/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

var allSupportedBillingCountries;

var wirecardaddon = {

    bindPaymentMethodPostForm:            function () {
        $('.submit_wirecardPaymentDetailsForm').click(function () {
            if ($('#wd-creditcard').is(':checked')) {
                WirecardPaymentPage.seamlessSubmitForm({
                    requestData:   requestData,
                    wrappingDivId: 'creditcard-form-div',
                    onSuccess:     submitPaymentDetailsForm,
                    onError:       displayError
                })
            } else if ($('#wd-sepadirectdebit').is(':checked')) {
                if ($('#bankAccountOwner-SEPA-DD').val().length > 1) {
                    ACC.colorbox.open('Sepa Mandate Conditions', {
                        href:       '#popup_sepa_mandate_conditions',
                        inline:     true,
                        width:      '525px',
                        onComplete: function () {
                            $(this).colorbox.resize();
                        }
                    });
                } else {
                    $('#bankAccountOwner-required').attr({'style': 'display: block; color: red'});
                    var dest = $('#bankAccountOwner-required').offset().top;
                    $('html, body').animate({scrollTop: dest});
                }
            } else {
                $('#wirecardPaymentDetailsForm').submit();
            }
        });
    },
    handleSepaMandateChkConditionsChange: function () {
        $('#sepaMandateChkConditions').change(function (e) {
            e.preventDefault();
            if ($(this).is(':checked')) {
                $('#sepaMandateButton').prop('disabled', false);
            }
            else {
                $('#sepaMandateButton').prop('disabled', true);
            }
        });
    },
    handleSepaMandateButtonClick:         function () {
        $('#sepaMandateButton').click(function () {
            if ($('#sepaMandateChkConditions').is(':checked')) {
                $('#wirecardPaymentDetailsForm').submit();
            }
        });
    },
    setSepaMandateAccountHolder:          function () {
        var tdate       = new Date();
        var dd          = tdate.getDate(); //yields day
        var MM          = tdate.getMonth(); //yields month
        var yyyy        = tdate.getFullYear(); //yields year
        var currentDate = dd + '-' + (MM + 1) + '-' + yyyy;
        $('#bankAccountOwner-SEPA-DD').on('change keyup paste', function () {
            $('#accountHolderSepa').text($('#bankAccountOwner-SEPA-DD').val() + '  --  ' + currentDate);
        });
    },
    updateBillingAddressForm:             function () {
        var paymentDetailsContent = $('#paymentDetailsContent');
        if ($('#differentAddress').is(':checked')) {
            paymentDetailsContent.find('input').prop('disabled', false);
            paymentDetailsContent.find('select').prop('disabled', false);
        }
        else {
            paymentDetailsContent.find('input').prop('disabled', true);
            paymentDetailsContent.find('select').prop('disabled', true);
        }
    },
    showCCInfo:                           function () {
        var ccDiv       = $('#creditcard-form-div');
        var savedCC     = $('#saved-cards-div');
        var savePayment = $('#save-payment-div');

        if ($('#wd-creditcard').is(':checked')) {
            ccDiv.attr({'style': 'display: block'});
            savedCC.attr({'style': 'display: block'});
            savePayment.attr({'style': 'display: block'});

        } else {
            ccDiv.attr({'style': 'display: none'});
            savedCC.attr({'style': 'display: none'});
            savePayment.attr({'style': 'display: none'});
        }
    },
    showIdealSelector:                    function () {

        var idealSelector = $('#ideal-selector');
        if ($('#wd-ideal').is(':checked')) {

            idealSelector.attr({'style': 'display: block'});

        } else {
            idealSelector.attr({'style': 'display: none'});
        }

    },
    showSepaCredentials:                  function () {
        var sepaDiv = $('#sepa-credentials-div');
        if ($('#wd-sepadirectdebit').is(':checked')) {
            sepaDiv.attr({'style': 'display: block'});
        } else {
            sepaDiv.attr({'style': 'display: none'});
        }
    },
    updateBillingAddress:                 function () {

        var supportedBillingCountries = $('input[name="paymentMethodChosen"]:checked').data('countries');

        var selectBox = $('#addresscountry');
        selectBox.empty();
        if (supportedBillingCountries.length < 1) {
            selectBox.html(allSupportedBillingCountries);
        } else {
            var nodoOption = $('<option>', {
                'value':    '',
                'disabled': 'disabled',
                'selected': 'selected'
            });
            nodoOption.html('Country');
            selectBox.append(nodoOption);
            for (var i = 0; i < supportedBillingCountries.length; i++) {
                nodoOption = $('<option>', {'value': supportedBillingCountries[i].isocode});
                nodoOption.html(supportedBillingCountries[i].name);
                selectBox.append(nodoOption);
            }
        }
    },
    updateAllowedSameAddress: function () {

        var notAllowedSameAddress = $('input[name="paymentMethodChosen"]:checked').data('sameaddress');

        var billingAdrressInfo = $('#billingAdrressInfo');
        var checkBox           = $('#differentAddress');
        if (notAllowedSameAddress) {
            billingAdrressInfo.attr({'style': 'display: none'});
            if (checkBox.prop('checked') === true) {
                checkBox.click();
            }
        } else {
            billingAdrressInfo.attr({'style': 'display: block'});
        }
    }
};

function doNothing(response) {

    console.log(response);

}

function displayError(response) {

    var errormessage = response.status_description_1;
    var div = document.createElement('div');
    div.setAttribute('class', 'global-alerts');
    var innerDiv = document.createElement('div');
    innerDiv.setAttribute('class', 'alert alert-danger alert-dismissable getAccAlert"');
    var text = '<button class="close closeAccAlert" aria-hidden="true" data-dismiss="alert" type="button">×</button>' + errormessage
    innerDiv.innerHTML = text;
    div.appendChild(innerDiv);
    var innerWrapper = document.getElementsByClassName("main__inner-wrapper");
    innerWrapper[0].prepend(div);
    var button = innerDiv.getElementsByClassName("closeAccAlert");
    button[0].addEventListener("click", removeGlobalMessage , false);

}

function removeGlobalMessage() {
    var innerWrapper = document.getElementsByClassName("main__inner-wrapper");
    innerWrapper[0].removeChild(innerWrapper[0].firstChild);
}

function submitPaymentDetailsForm(response) {
    $('#tokenId').val(response.token_id);
    $('#wirecardPaymentDetailsForm').submit();
}

$(document).ready(function () {

    allSupportedBillingCountries = $('#addresscountry').html();
    wirecardaddon.updateBillingAddressForm();
    wirecardaddon.showCCInfo();
    wirecardaddon.showSepaCredentials();

    if ($('#differentAddress').length > 0) {
        $('#differentAddress').click(function () {
            wirecardaddon.updateBillingAddressForm();
        })
    }
    else {
        $('#paymentDetailsContent').find('input').prop('disabled', false);
    }
    var creditcard = $('#wd-creditcard');
    if (creditcard.length > 0) {
        creditcard.parent().append($('#saved-cards-div'));
        creditcard.parent().append($('#creditcard-form-div'));
        creditcard.parent().append($('#save-payment-div'));
    }
    
    var sepa = $('#wd-sepadirectdebit');
    if (sepa.length > 0) {
        sepa.parent().append($('#sepa-credentials-div'));
    }

    var ideal = $('#wd-ideal');
    if (ideal.length > 0) {
        ideal.parent().append($('#ideal-selector'));
    }
    if ($('.paymentMethods').length > 0) {
        $('.paymentMethods').click(function () {
            wirecardaddon.showCCInfo();
            wirecardaddon.showSepaCredentials();
            wirecardaddon.updateBillingAddress();
            wirecardaddon.showIdealSelector();
            wirecardaddon.updateAllowedSameAddress();
        })
        if ($('#wd-creditcard').length > 0) {
            WirecardPaymentPage.seamlessRenderForm({
                requestData:   requestData,
                wrappingDivId: 'creditcard-form-div',
                onSuccess:     doNothing,
                onError:       doNothing
            });
        }
    }
    wirecardaddon.bindPaymentMethodPostForm();
    wirecardaddon.handleSepaMandateChkConditionsChange();
    wirecardaddon.handleSepaMandateButtonClick();
    wirecardaddon.setSepaMandateAccountHolder();

    if ($('#downloadForm').length > 0) {
        $('#downloadForm').submit();
    }

    (function(){
        var listOfActions = Array.prototype.slice.call(document.querySelectorAll('[class^="AccountOrderDetailsOverviewComponent"]'), 0);
        var firstVisibleAction = listOfActions.filter(function(element){
            return element.childElementCount > 0;
        })[0];
        if (firstVisibleAction){
            var target = firstVisibleAction.querySelectorAll('input[type="submit"], button')[0];
            target.className = target.className.replace(/btn\-default/g,'btn-primary');
        }
    })();
});


