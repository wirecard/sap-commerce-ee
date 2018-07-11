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

package com.wirecard.hybris.core.payment.response;

import com.wirecard.hybris.core.data.types.BankAccount;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.payment.response.impl.WirecardDebitReturnResponseHandler;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.WirecardDebitPaymentInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(Parameterized.class)
public class DefaultWirecardDebitReturnResponseHandlerTest {

    private static final String IBAN = "IBAN";
    private static final String BIC = "BIC";

    private static Collection<Object[]> data;

    @Parameters
    public static Collection<Object[]> data() {
        return data;
    }

    static {
        data = new ArrayList<>();
        // when IBAN and BIC are set, they are the expected values in the payment object
        data.add(new Object[] { IBAN, BIC, IBAN, BIC });
        // when IBAN and/or BIC are not set, payment object IBAN and BIC should not be set
        data.add(new Object[] { IBAN, null, null, null });
        data.add(new Object[] { null, BIC, null, null });
        data.add(new Object[] { null, null, null, null });
    }

    public DefaultWirecardDebitReturnResponseHandlerTest(String paymentIban, String paymentBic, String expectedIban, String expectedBic) {
        super();
        this.paymentIban = paymentIban;
        this.paymentBic = paymentBic;
        this.expectedIban = expectedIban;
        this.expectedBic = expectedBic;
    }

    private WirecardDebitReturnResponseHandler responseHandler;

    @Mock
    private ModelService modelService;

    private WirecardDebitPaymentInfoModel paymentInfo;
    private AbstractOrderModel abstractOrderModel;
    private Payment payment;

    private String paymentIban;
    private String paymentBic;
    private String expectedIban;
    private String expectedBic;

    @Before
    public void setup() throws WirecardPaymenException {
        responseHandler = new WirecardDebitReturnResponseHandler();
        MockitoAnnotations.initMocks(this);
        responseHandler.setModelService(modelService);

        paymentInfo = new WirecardDebitPaymentInfoModel();

        abstractOrderModel = new CartModel();
        abstractOrderModel.setPaymentInfo(paymentInfo);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(paymentIban);
        bankAccount.setBic(paymentBic);

        payment = new Payment();
        payment.setBankAccount(bankAccount);

    }

    @Test
    public void executeProcessNotificationTest() {

        PaymentOperationData data = new PaymentOperationData();
        data.setPayment(payment);
        try {
            this.responseHandler.processResponse(abstractOrderModel, data);
        } catch (WirecardPaymenException e) {
            // ignore errors because they are expected
        }

        assertEquals("The expected IBAN does not match", expectedIban, paymentInfo.getIban());
        assertEquals("The expected BIC does not match", expectedBic, paymentInfo.getBic());

    }

}
