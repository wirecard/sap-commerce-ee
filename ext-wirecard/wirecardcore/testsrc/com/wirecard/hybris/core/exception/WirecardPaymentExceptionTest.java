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

package com.wirecard.hybris.core.exception;

import com.wirecard.hybris.core.data.types.Severity;
import com.wirecard.hybris.core.data.types.Status;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.exception.constants.WirecardPaymentExceptionConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class WirecardPaymentExceptionTest extends HybrisJUnit4TransactionalTest {

    private static String message;
    private List<Status> listStatus;

    @Before
    public void setup() {
        message = WirecardPaymentExceptionConstants.DEFAULT_ERROR;
    }

    @Test
    public void testWirecardPaymenExceptionMessageConstructor() {

        WirecardPaymenException exception = new WirecardPaymenException(message);

        assertTrue("Message in WirecardException has not been set", exception.getMessage().equals(message));

    }

    @Test
    public void testWirecardPaymenExceptionMessageCauseConstructor() {
        WirecardPaymenException exception = new WirecardPaymenException(message, fillThrowable());

        assertTrue("WirecardPaymentException has not been created",
                   exception.getMessage().equals(message) && exception.getCause().getClass().equals(Throwable.class));
    }

    @Test
    public void testWirecardPaymenExceptionConstructor() {
        message = "Transaction failed";
        listStatus = new ArrayList<>();
        Status status = new Status();
        status.setCode("401");
        status.setDescription("Not provider");
        status.setSeverity(Severity.ERROR);
        listStatus.add(status);

        WirecardPaymenException exception = new WirecardPaymenException(message, listStatus);

        assertEquals("The exception is not equals", message + "\n " + status.getCode() + " " + status.getDescription(),
                     exception.getMessage());
    }


    public Throwable fillThrowable() {

        return new Throwable();
    }

}
