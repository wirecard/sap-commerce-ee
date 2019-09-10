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

import com.wirecard.hybris.core.data.types.*;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.strategy.impl.DefaultWirecardPaymentOperationStrategy;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

        target.setAccountHolder(findAccountHolder(source,customer, target.getAccountHolder()));
        target.setRiskInfo(findRiskInfo(source, target, customer));

    }

    private RiskInfo findRiskInfo(AbstractOrderModel source, Payment target,CustomerModel customer ){
        RiskInfo riskInfo = new RiskInfo();
        riskInfo.setAvailability(findRiskInfoAvailability(source));
        riskInfo.setDeliveryMail(source.getDeliveryAddress().getEmail());
        riskInfo.setReorderItems(findRiskInfoReorderItems(source, customer));//not available
        riskInfo.setGift(null);//not available
        riskInfo.setDeliveryTimeframe(null);//dependant on Delivery method that are different for each merchant.
        return riskInfo;
    }


    private AccountHolder findAccountHolder(AbstractOrderModel source,CustomerModel customer, AccountHolder holder){
        holder.setAccountInfo(findAccountInfo(source, customer));
        holder.setMerchantCrmId(customer.getCustomerID());
        return holder;
    }


    private AccountInfo findAccountInfo (AbstractOrderModel source ,CustomerModel customer){
        AccountInfo info  = new AccountInfo();
        try {
            info.setAuthenticationMethod(null);//Not available
            Date lastLogin = customer.getLastLogin();
            if (lastLogin != null)
            {
                info.setAuthenticationTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar( lastLogin.toInstant().toString()));
            }

            if (Config.getParameter("wirecardChallengeIndicator")!=null)
            {
                if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_MANDATE)==0)
                    info.setChallengeIndicator(CHALLENGE_MANDATE);

                if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_NO_PREFERENCE)==0)
                    info.setChallengeIndicator(CHALLENGE_NO_PREFERENCE);

                if(Config.getParameter("wirecardChallengeIndicator").compareTo(NO_CHALLENGE)==0)
                    info.setChallengeIndicator(NO_CHALLENGE);

                if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_MERCHANT_PREFERENCE)==0)
                    info.setChallengeIndicator(CHALLENGE_MERCHANT_PREFERENCE);
            }
            //challenge-indicator
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
            info.setCreationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(formatter.format(customer.getCreationtime().toInstant())));
            info.setUpdateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar( formatter.format(customer.getModifiedtime().toInstant())));
            info.setPasswordChangeDate(null);//Not available
            info.setShippingAddressFirstUse(null);
            info.setTransactionsLastDay(findTransactionsCountLastDays(customer, 1));
            info.setTransactionsLastYear(findTransactionsCountLastDays(customer, 365));
            info.setPurchasesLastSixMonths(findOrdersCountLastSixMonths(customer));
            info.setSuspiciousActivity(null); //NOT AVAILABLE
            info.setCardCreationDate(null); //NOT AVAILABLE
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        return info;
    }

    private int findOrdersCountLastSixMonths(CustomerModel customer)
    {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, -6);
        return (int) customer.getOrders().stream().filter(order -> order.getDate().after(cal.getTime())).count();
    }

    private int findTransactionsCountLastDays(CustomerModel customer, int days)
    {
        int transactions = 0;
        List<PaymentStatus> validStatus= Arrays.asList(PaymentStatus.PAID, PaymentStatus.ERROR);
        List<OrderModel> orders = findOrdersInLastDays(customer, days);
        for(OrderModel order: orders)
        {
           transactions+= order.getPaymentTransactions().size();
        }
        return transactions;
    }

    private List<OrderModel> findOrdersInLastDays(CustomerModel customer, int days)
    {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, ((days)*-1));
        return customer.getOrders().stream().filter(order -> order.getDate().after(cal.getTime())).collect(Collectors.toList());

    }

    private String findRiskInfoAvailability(AbstractOrderModel source)
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
                return "02";
            }
        }
        return "01";
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

    private String findRiskInfoReorderItems(AbstractOrderModel source, CustomerModel customer)
    {
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
            return "01";
        }
        else
        {
            return "02";
        }
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
