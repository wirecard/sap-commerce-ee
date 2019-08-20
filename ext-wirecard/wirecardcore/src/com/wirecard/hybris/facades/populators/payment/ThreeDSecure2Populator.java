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

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.SequenceType;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.strategy.impl.DefaultWirecardPaymentOperationStrategy;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import sun.util.calendar.ZoneInfo;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class ThreeDSecure2Populator extends AbstractOrderAwarePaymentPopulator {
    private static final Logger LOG = LoggerFactory.getLogger(ThreeDSecure2Populator.class);
    //SERVICES
    private SessionService sessionService;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    private StockService stockService;
    //DATE FORMATS
    private static final String AUTHENTICATION_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat authenticationTimestampFormat = new SimpleDateFormat(AUTHENTICATION_TIMESTAMP_FORMAT);
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    //CHALLENGE_INDICATOR_VALUES
    private static final String CHALLENGE_NO_PREFERENCE = "01";
    private static final String NO_CHALLENGE = "02";
    private static final String CHALLENGE_MERCHANT_PREFERENCE= "03";
    private static final String CHALLENGE_MANDATE = "04";
    //Common attributes
    private ZoneInfo timezone;
    private CustomerModel customer;

    @Override
    public void doPopulate(AbstractOrderModel source, Payment target) throws ConversionException {
        timezone = getSessionService().getCurrentSession().getAttribute("timezone");
        customer = getSessionService().getAttribute("user");

        findAccountInfo(source,target,customer);

        findTranstactionsInfo(source,target,customer);

        findConsumerInfo(source,target,customer);

        findShippingInfo(source, target, customer);

        findRiskInfo(source, target, customer);

        findRecurringInfo(source, target, customer);

        findChallengeIndicator(target);

        findIsoTranstactionType(source,target);

        findThreeDVersion(source,target);
    }

    private void findChallengeIndicator(Payment target)
    {
        if (Config.getParameter("wirecardChallengeIndicator")!=null)
       {
           if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_MANDATE)==0)
               target.setChallengeIndicator(CHALLENGE_MANDATE);

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
            //NOT AVAILABLE

            //purchases_last_six_months
            target.setPurchasesLastSixMonths(findOrdersCountLastSixMonths(customer));

            //suspicious_activity -> Boolean indicating if the merchant knows of suspicious activities by the customer
            //NOT AVAILABLE
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
            //(NOT AVAILABLE)

            //shipping_address_first_use
            //(NOT AVAILABLE)
        }
    }

    private void findAuthenticationMethod(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //authentication_method
        // (NOT AVAILABLE)
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
        //NOT AVAILABLE

        //postal_code
        target.setPostalCode(source.getPaymentAddress().getPostalcode());

        //state
        //NOT AVAILABLE

        //email
        target.setEmail(source.getPaymentAddress().getEmail());

        //home_phone
        //NOT AVAILABLE

        //mobile_phone
        //NOT AVAILABLE

        //work_phone
        //NOT AVAILABLE

        //last_name
        target.setLastName(source.getPaymentAddress().getLastname());

        //first_name
        target.setFirstName(source.getPaymentAddress().getFirstname());
    }


    private void findThreeDVersion(AbstractOrderModel source, Payment target)
    {
        //three_d_version 1.0 or 2.1
        //(NOT AVAILABLE)
    }

    private void findIsoTranstactionType(AbstractOrderModel source, Payment target)
    {
        //iso_transaction_type
        //01 Goods/Service
        //03 Check acceptance
        //10 Account Funding
        //11 Quasi-Cash Transaction
        //28 Prepaid Activation and Loan

        //(NOT AVAILABLE)

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
        //(NOT AVAILABLE)
    }

    private void findRecurringInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //recurring_expire_date
        //(NOT AVAILABLE)

        //recurring_frequency
        //(NOT AVAILABLE)
    }

    private void findRiskInfo(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //risk_info_delivery_time
        // 01 Electronic delivery
        // 02 Same-day delivery
        // 03 Overnight delivery
        // 04 Two day or more delivery
        //(NOT AVAILABLE)

        //risk_info_delivery_mail   -> email used within electronic-delivery
        //NOT AVAILABLE


        findRiskInfoReorderItems(source,target,customer);

        //risk_info_availability
        if(findRiskInfoAvailability(source,target,customer))
        {
            // 01 Currently available
            target.setRiskInfoAvailability("01");
        }else
        {
            // 02 Future availability
            target.setRiskInfoAvailability("02");
        }


        //risk_info_preorder_date   -> Expected shipping date for preorder goods
        //(NOT AVAILABLE)

        findRiskInfoGiftCard(source, target, customer);
    }

    private boolean findRiskInfoAvailability(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //For true availability every entry within the order must have availability on any store warehouse
        String warehousePattern="Warehouse:";
        for(AbstractOrderEntryModel entry: source.getEntries())
        {
            boolean entryAvailable=false;
            for(String availability : getStockService().getAvailability(entry.getProduct(), source.getStore().getWarehouses(), null, null).split(warehousePattern))
            {
                if (parseAvailability(availability))
                {
                    entryAvailable = true;
                    break;
                }
            }
            if(!entryAvailable)
            {
                return false;
            }
        }
        return true;
    }

    private boolean parseAvailability(String availability)
    {
        if(!availability.isEmpty())
        {
            String availabilityPattern="Availability: ";
            String datePattern=" Date:";
            if(availability.contains(availabilityPattern)&&availability.contains(datePattern))
            {
                try
                {
                    String number = availability.substring((availability.lastIndexOf(availabilityPattern) + availabilityPattern.length()), availability.lastIndexOf(datePattern));
                    if (Integer.valueOf(number) > 0)
                    {
                        return true;
                    }
                }catch(Exception e)
                {
                    LOG.error("Error parsing availability. Unexpected format",e);
                }
            }
        }
        return false;
    }

    private void findRiskInfoReorderItems(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //risk_info_reorder_items
        boolean firstTime=true;
        List<AbstractOrderEntryModel> orderEntries =source.getEntries();
        for(OrderModel order : customer.getOrders())
        {
            if(Collections.disjoint(order.getEntries(),orderEntries)==false)
            {
                firstTime = false;
            }
        }
        if(firstTime)
        {
            // 01 First time
            target.setRiskInfoReorderItems("01");
        }
        else
        {
            // 02 Reorder
            target.setRiskInfoReorderItems("02");
        }
    }

    private void findRiskInfoGiftCard(AbstractOrderModel source, Payment target, CustomerModel customer)
    {
        //risk_info_gift_amount
        //(NOT AVAILABLE)

        //risk_info_amount_currency
        //(NOT AVAILABLE)

        //risk_info_gift_card_count
        //(NOT AVAILABLE)
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


    protected StockService getStockService()
    {
        return stockService;
    }

    @Required
    public void setStockService(final StockService stockService)
    {
        this.stockService = stockService;
    }

}
