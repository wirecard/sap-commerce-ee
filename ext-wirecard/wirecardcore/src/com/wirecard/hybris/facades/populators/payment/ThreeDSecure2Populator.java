/*
 * Shop System Plugins - Terms of Use
 *
 * The plugins offered are provided free of charge by Wirecard AG and are explicitly not part
 * of the Wirecard AG range of products and services.
 *
 * They have been tested and approved for full functionality in the standard configuration
 * (status on delivery) of the corresponding shop system. They are under MIT license
 * and can be used, developed and passed on to third parties under
 * the same terms.
 *
 * However, Wirecard AG does not provide any guarantee or accept any liability for any errors
 * occurring when used in an enhanced, customized shop system configuration.
 *
 * Operation in an enhanced, customized configuration is at your own risk and requires a
 * comprehensive test phase by the user of the plugin.
 *
 * Customers use the plugins at their own risk. Wirecard AG does not guarantee their full
 * functionality neither does Wirecard AG assume liability for any disadvantages related to
 * the use of the plugins. Additionally, Wirecard AG does not guarantee the full functionality
 * for customized shop systems or installed plugins of other vendors of plugins within the same
 * shop system.
 *
 * Customers are responsible for testing the plugin's functionality before starting productive
 * operation.
 *
 * By installing the plugin into the shop system the customer agrees to these terms of use.
 * Please do not use the plugin if you do not agree to these terms of use!
 */

package com.wirecard.hybris.facades.populators.payment;

import com.wirecard.hybris.core.data.types.MerchantAccountId;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.util.Config;
import org.springframework.beans.factory.annotation.Required;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import sun.util.calendar.ZoneInfo;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class ThreeDSecure2Populator extends AbstractOrderAwarePaymentPopulator {
    private SessionService sessionService;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;
    private static final String AUTHENTICATION_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    //AUTHENTICATION_METHOD_VALUES
    private static final String AUTHENTICATION_GUEST_LOGIN = "01";
    private static final String AUTHENTICATION_SHOP_CREDENTIALS_LOGIN = "02";
    private static final String AUTHENTICATION_FEDERATED_ID_LOGIN = "03";
    private static final String AUTHENTICATION_CARD_ISSUER_LOGIN = "04";
    private static final String AUTHENTICATION_THIRD_PARTY_LOGIN = "05";
    private static final String AUTHENTICATION_FIDO_LOGIN = "06";
    //CHALLENGE_INDICATOR_VALUES
    private static final String CHALLENGE_NO_PREFERENCE = "01";
    private static final String NO_CHALLENGE = "02";
    private static final String CHALLENGE_MERCHANT_PREFERENCE= "03";
    private static final String CHALLENGE_MANDATE = "04";


    private static final SimpleDateFormat authenticationTimestampFormat = new SimpleDateFormat(AUTHENTICATION_TIMESTAMP_FORMAT);
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    private ZoneInfo timezone;

    @Override
    public void doPopulate(AbstractOrderModel source, Payment target) throws ConversionException {
        Session session= getSessionService().getCurrentSession();
        timezone = getSessionService().getCurrentSession().getAttribute("timezone");
        CustomerModel customer = session.getAttribute("user");

        findAccountInfo(source,target,customer);

        findChallengeIndicator(source,target,customer);

        findTranstactionsInfo(source,target,customer);

        findConsumerInfo(source,target,customer);

        findShippingInfo(source, target, customer);

        findRiskInfo(source, target, customer);

        findRecurringInfo(source, target, customer);

        findIsoTranstactionType(source,target);

        findThreeDVersion(source,target);
    }

    private void findChallengeIndicator(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //challenge_indicator
       if(wirecardHopPaymentOperationsFacade.isSavedCC())
           target.setChallengeIndicator(CHALLENGE_MANDATE);
       else if (Config.getParameter("wirecardChallengeIndicator")!=null)
       {
           if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_NO_PREFERENCE)==0)
                target.setChallengeIndicator(CHALLENGE_NO_PREFERENCE);

           if(Config.getParameter("wirecardChallengeIndicator").compareTo(NO_CHALLENGE)==0)
                target.setChallengeIndicator(NO_CHALLENGE);

           if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_MERCHANT_PREFERENCE)==0)
                target.setChallengeIndicator(CHALLENGE_MERCHANT_PREFERENCE);
       }

    }

    private void findTranstactionsInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {

        if(customer!=null)
        {
            //transactions_last_day
            target.setTransactionsLastDay(findTransactionsCountLastDays(customer, 1));

            //transactions_last_year
            target.setTransactionsLastYear(findTransactionsCountLastDays(customer, 365));
            //card_transactions_last_day

            //purchases_last_six_months
            target.setPurchasesLastSixMonths(findOrdersCountLastSixMonths(customer));
            //suspicious_activity

            //card_creation_date
        }



    }

    private String findOrdersCountLastSixMonths(CustomerModel customer)
    {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, -6);
        return String.valueOf(customer.getOrders().stream().filter(order -> order.getDate().after(cal.getTime())).count());
    }

    private String findTransactionsCountLastDays(CustomerModel customer, int days)
    {
        int transactions = 0;
        List<PaymentStatus> validStatus= Arrays.asList(PaymentStatus.PAID, PaymentStatus.ERROR);
        List<OrderModel> orders = findOrdersInLastDays(customer, days);
        for(OrderModel order: orders)
        {
            if(validStatus.contains(order.getPaymentStatus()))
            {
                transactions+= order.getPaymentTransactions().size();
            }
        }
        return String.valueOf(transactions);
    }

    private List<OrderModel> findOrdersInLastDays(CustomerModel customer, int days)
    {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, ((days)*-1));
        return customer.getOrders().stream().filter(order -> order.getDate().after(cal.getTime())).collect(Collectors.toList());

    }

    private void findAccountInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        if(customer!=null)
        {
            findAuthenticationMethod(source, target, customer);

            //authentication_timestamp
            Date lastLogin = customer.getLastLogin();
            if (lastLogin != null)
            {

                authenticationTimestampFormat.setTimeZone(timezone);
                String dateAuthenticationTimestamp = authenticationTimestampFormat.format(lastLogin);
                target.setAuthenticationTimestamp(dateAuthenticationTimestamp);
            }
            //account_creation_date
            Date accountCreation = customer.getCreationtime();
            if (accountCreation != null)
            {

                defaultDateFormat.setTimeZone(timezone);
                String creationDate = defaultDateFormat.format(accountCreation);
                target.setAccountCreationDate(creationDate);
            }
            //account_update_date
            Date accountModification = customer.getModifiedtime();
            if (accountModification != null)
            {

                defaultDateFormat.setTimeZone(timezone);
                String creationDate = defaultDateFormat.format(accountModification);
                target.setAccountUpdateDate(creationDate);
            }
            //account_password_change_date

            //shipping_address_first_use
        }
    }

    private void findAuthenticationMethod(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //authentication_method
        target.setAuthenticationMethod(AUTHENTICATION_SHOP_CREDENTIALS_LOGIN);
    }

    private void findConsumerInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //merchant_crm_id
        target.setMerchantCrmId( getWirecardPaymentConfigurationService().getAuthentication(source).getMaid());

        //city
        target.setCity(source.getPaymentAddress().getTown());

        //country
        target.setCountry(source.getPaymentAddress().getCountry().getIsocode());

        //street1
        target.setStreet1(source.getPaymentAddress().getLine1());

        //street2
        target.setStreet2(source.getPaymentAddress().getLine2());

        //street3

        //postal_code
        target.setPostalCode(source.getPaymentAddress().getPostalcode());

        //state

        //email
        target.setEmail(source.getPaymentAddress().getEmail());
        //home_phone

        //mobile_phone

        //work_phone

        //last_name
        target.setLastName(source.getPaymentAddress().getLastname());

        //first_name
        target.setFirstName(source.getPaymentAddress().getFirstname());
    }


    private void findThreeDVersion(AbstractOrderModel source, Payment target)
    {
        //three_d_version 1.0 or 2.1
    }

    private void findIsoTranstactionType(AbstractOrderModel source, Payment target)
    {
        //iso_transaction_type
        //01 Goods/Service
        //03 Check acceptance
        //10 Account Funding
        //11 Quasi-Cash Transaction
        //28 Prepaid Activation and Loan

    }

    private void findShippingInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        findShippingShippingMethodInfo(source,target, customer);

        //shipping_city
        target.setShippingCity(source.getDeliveryAddress().getTown());

        //shipping_country
        target.setShippingCountry(source.getDeliveryAddress().getCountry().getIsocode());

        //shipping_street1
        target.setShippingStreet1(source.getDeliveryAddress().getLine1());

        //shipping_street2
        target.setShippingStreet2(source.getDeliveryAddress().getLine2());
        //shipping_street3

        //shipping_postal_code
        target.setShippingPostalCode(source.getDeliveryAddress().getPostalcode());
        //shipping_state

    }

    private void findShippingShippingMethodInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //shipping_shipping_method
        //home_delivery, verified_address_delivery,  other_address_delivery, store_pick_up, digital_goods, digital_tickets, other_verified

    }

    private void findRecurringInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //recurring_expire_date

        //recurring_frequency
    }

    private void findRiskInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //risk_info_delivery_time
        // 01 Electronic delivery
        // 02 Same-day delivery
        // 03 Overnight delivery
        // 04 Two day or more delivery

        //risk_info_delivery_mail
        // email used within electronic-delivery

        //risk_info_reorder_items
        // 01 First time
        // 02 Reorder

        //risk_info_availability
        // 01 Currently available
        // 02 Future availability

        //risk_info_preorder_date
        //Expected shipping date for preorder goods

        findRiskInfoGiftCard(source, target, customer);

    }

    private void findRiskInfoGiftCard(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //risk_info_gift_amount

        //risk_info_amount_currency

        //risk_info_gift_card_count
    }

    public SessionService getSessionService() {
        return this.sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }

    protected WirecardHopPaymentOperationsFacade getWirecardHopPaymentOperationsFacade() {
        return wirecardHopPaymentOperationsFacade;
    }

    @Required
    public void setWirecardHopPaymentOperationsFacade(WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade) {
        this.wirecardHopPaymentOperationsFacade = wirecardHopPaymentOperationsFacade;
    }

}
