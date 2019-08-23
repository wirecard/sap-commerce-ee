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

        target.setThreeDSRequestor(find3DSRequestor(source,customer));

        target.setThreeDSServerTransID(findThreeDSServerTransID(source,customer));

        target.setCardholderAccount(findCardHolderAccount(source,customer));

        target.setCardholder(findCardHolder(source,customer));

        target.setPurchase(findPurchase(source,customer));;

        target.setBrowserInformation(findBrowserInformation());
    }

    private BrowserInformation findBrowserInformation()
    {
        BrowserInformation browserInformation = new BrowserInformation();

        browserInformation.setChallengeWindowSize(null); //NOT AVAILABLE
        return browserInformation;
    }

    private Purchase findPurchase(AbstractOrderModel source, CustomerModel customer)
    {
        Purchase purchase = new Purchase();

        purchase.setMerchantRiskIndicator(findMerchantRiskIndicator(source,customer));
        Double totalAmount = source.getTotalPrice()+source.getTotalTax();
        purchase.setPurchaseAmount(totalAmount.toString());
        purchase.setPurchaseCurrency(source.getCurrency().getIsocode());
        purchase.setPurchaseExponent(null); // NOT AVAILABLE
        purchase.setTransType(null); //NOT AVAILABLE

        return purchase;
    }

    private MerchantRiskIndicator findMerchantRiskIndicator(AbstractOrderModel source, CustomerModel customer)
    {
        MerchantRiskIndicator merchantRiskIndicator = new MerchantRiskIndicator();

        merchantRiskIndicator.setDeliveryEmailAddress(null); //NOT AVAILABLE
        merchantRiskIndicator.setDeliveryTimeframe(null); //NOT AVAILABLE
        merchantRiskIndicator.setGiftCardAmount(null); //NOT AVAILABLE
        merchantRiskIndicator.setGiftCardCurr(null); //NOT AVAILABLE
        merchantRiskIndicator.setGiftCardCount(null); //NOT AVAILABLE
        merchantRiskIndicator.setPreOrderDate(null); //NOT AVAILABLE
        if(findRiskInfoAvailability(source))
        {
            // 01 Currently available
            merchantRiskIndicator.setPreOrderPurchaseInd("01");
        }else
        {
            // 02 Future availability
            merchantRiskIndicator.setPreOrderPurchaseInd("02");
        }
        if(findRiskInfoReorderItems(source,customer))
        {
            // 01 First time
            merchantRiskIndicator.setReorderItemsInd("01");
        }else
        {
            // 01 Reorder
            merchantRiskIndicator.setReorderItemsInd("02");
        }
        merchantRiskIndicator.setShipIndicator(null); //NOT AVAILABLE

        return merchantRiskIndicator;
    }

    private Cardholder findCardHolder(AbstractOrderModel source, CustomerModel customer)
    {
        Cardholder cardholder = new Cardholder();

        cardholder.setBillAddrCity(source.getPaymentAddress().getTown());
        cardholder.setBillAddrCountry(source.getPaymentAddress().getCountry().getIsocode());
        cardholder.setBillAddrLine1(source.getPaymentAddress().getLine1());
        cardholder.setBillAddrLine2(source.getPaymentAddress().getLine2());
        cardholder.setBillAddrLine3(null); //NOT AVAILABLE
        cardholder.setBillAddrPostCode(source.getPaymentAddress().getPostalcode());
        cardholder.setBillAddrState(null);//NOT AVAILABLE
        cardholder.setEmail(source.getPaymentAddress().getEmail());
        cardholder.setWorkPhone(findWorkPhone(customer));
        cardholder.setHomePhone(findHomePhone(customer));
        cardholder.setMobilePhone(findMobilePhone(customer));
        cardholder.setShipAddrCity(source.getDeliveryAddress().getTown());
        cardholder.setShipAddrCountry(source.getDeliveryAddress().getCountry().getIsocode());
        cardholder.setShipAddrLine1(source.getDeliveryAddress().getLine1());
        cardholder.setShipAddrLine2(source.getDeliveryAddress().getLine2());
        cardholder.setShipAddrLine3(null); //NOT AVAILABLE
        cardholder.setShipAddrPostCode(source.getDeliveryAddress().getPostalcode());
        cardholder.setShipAddrState(null);//NOT AVAILABLE

        return cardholder;
    }

    private Phone findHomePhone(CustomerModel customer)
    {
        Phone homePhone= new Phone();

        return homePhone;
    }

    private Phone findWorkPhone(CustomerModel customer)
    {
        Phone workPhone= new Phone();

        return workPhone;
    }

    private Phone findMobilePhone(CustomerModel customer)
    {
        Phone mobilePhone= new Phone();

        return mobilePhone;
    }

    private CardholderAccount findCardHolderAccount(AbstractOrderModel source,CustomerModel customer)
    {
        CardholderAccount cardholderAccount = new CardholderAccount();

        cardholderAccount.setAcctInfo(findAcctInfo(customer));
        cardholderAccount.setAcctID(getWirecardPaymentConfigurationService().getAuthentication(source).getMaid());

        return cardholderAccount;
    }

    private AcctInfo findAcctInfo(CustomerModel customer)
    {
        AcctInfo acctInfo = new AcctInfo();

        Date accountCreation = customer.getCreationtime();
        if (accountCreation != null)
        {

            defaultDateFormat.setTimeZone(timezone);
            String creationDate = defaultDateFormat.format(accountCreation);
            acctInfo.setChAccDate(creationDate);
        }
        Date accountModification = customer.getModifiedtime();
        if (accountModification != null)
        {

            defaultDateFormat.setTimeZone(timezone);
            String creationDate = defaultDateFormat.format(accountModification);
            acctInfo.setChAccChange(creationDate);
        }
        acctInfo.setChAccPwChange(null); //NOT AVAILABLE
        acctInfo.setShipAddressUsage(null); //NOT AVAILABLE
        acctInfo.setTxnActivityDay(findTransactionsCountLastDays(customer, 1));
        acctInfo.setTxnActivityYear(findTransactionsCountLastDays(customer, 365));
        acctInfo.setProvisionAttemptsDay(null); //NOT AVAILABLE
        acctInfo.setNbPurchaseAccount(findOrdersCountLastSixMonths(customer));
        acctInfo.setSuspiciousAccActivity(null); //NOT AVAILABLE
        acctInfo.setPaymentAccAge(null); //NOT AVAILABLE

        return acctInfo;
    }

    private String findThreeDSServerTransID(AbstractOrderModel source,CustomerModel customer)
    {
        return null; //NOT AVAILABLE
    }

    private ThreeDSRequestor find3DSRequestor(AbstractOrderModel source, CustomerModel customer)
    {
        ThreeDSRequestor threeDSRequestor= new ThreeDSRequestor();

        threeDSRequestor.setThreeDSRequestorAuthenticationInfo(findThreeDSRequestorAuthenticationInfo());
        if (Config.getParameter("wirecardChallengeIndicator")!=null)
        {
            if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_MANDATE)==0)
                threeDSRequestor.setThreeDSRequestorChallengeInd(CHALLENGE_MANDATE);

            if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_NO_PREFERENCE)==0)
                threeDSRequestor.setThreeDSRequestorChallengeInd(CHALLENGE_NO_PREFERENCE);

            if(Config.getParameter("wirecardChallengeIndicator").compareTo(NO_CHALLENGE)==0)
                threeDSRequestor.setThreeDSRequestorChallengeInd(NO_CHALLENGE);

            if(Config.getParameter("wirecardChallengeIndicator").compareTo(CHALLENGE_MERCHANT_PREFERENCE)==0)
                threeDSRequestor.setThreeDSRequestorChallengeInd(CHALLENGE_MERCHANT_PREFERENCE);
        }
        threeDSRequestor.setThreeDSRequestorPriorAuthenticationInfo(null); // (NOT AVAILABLE)

        return threeDSRequestor;
    }

    private ThreeDSRequestorAuthenticationInfo findThreeDSRequestorAuthenticationInfo()
    {
        ThreeDSRequestorAuthenticationInfo threeDSRequestorAuthenticationInfo = new ThreeDSRequestorAuthenticationInfo();

        threeDSRequestorAuthenticationInfo.setThreeDSReqAuthMethod(null);// (NOT AVAILABLE)
        Date lastLogin = customer.getLastLogin();
        if (lastLogin != null)
        {

            authenticationTimestampFormat.setTimeZone(timezone);
            String dateAuthenticationTimestamp = authenticationTimestampFormat.format(lastLogin);
            threeDSRequestorAuthenticationInfo.setThreeDSReqAuthTimestamp(dateAuthenticationTimestamp);
        }

        return threeDSRequestorAuthenticationInfo;
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

    private boolean findRiskInfoAvailability(AbstractOrderModel source)
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

    private boolean findRiskInfoReorderItems(AbstractOrderModel source, CustomerModel customer)
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
            return true;
        }
        else
        {
            return false;
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
