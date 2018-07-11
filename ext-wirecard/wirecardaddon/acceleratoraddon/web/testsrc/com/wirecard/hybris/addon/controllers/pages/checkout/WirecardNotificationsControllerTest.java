/*********************************************************************
 * The Initial Developer of the content of this file is NETCONOMY.
 * All portions of the code written by NETCONOMY are property of
 * NETCONOMY. All Rights Reserved.
 *
 * NETCONOMY Software & Consulting GmbH
 * Hilmgasse 4, A-8010 Graz (Austria)
 * FN 204360 f, Landesgericht fuer ZRS Graz
 * Tel: +43 (316) 815 544
 * Fax: +43 (316) 815544-99
 * www.netconomy.net
 *
 * (c) 2018 by NETCONOMY Software & Consulting GmbH
 *********************************************************************/

package com.wirecard.hybris.addon.controllers.pages.checkout;

import com.wirecard.hybris.core.constants.WirecardPaymentTransactionConstants;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WirecardNotificationsControllerTest {

    @InjectMocks
    private WirecardNotificationsController wirecardNotificationController;

    @Mock
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;

    @Mock
    private HttpServletResponse httpServletResponse;

    private String source;

    private Payment payment;

    @Before
    public void setup() throws WirecardPaymenException {

        source = "testsource";

        when(wirecardHopPaymentOperationsFacade.parseMessage(source, true, true)).thenReturn(payment);

    }

    @Test
    public void testProcessNotification() {

        wirecardNotificationController.processNotification(source, httpServletResponse);

        Assert.assertNull("the source is null", source);
        Assert.assertEquals("the response isn't success", httpServletResponse.getStatus(), HttpServletResponse.SC_OK);

    }

    @Test
    public void testExceptionProcessNotification() throws WirecardPaymenException {

        when(wirecardHopPaymentOperationsFacade.executePaymentOperation(WirecardPaymentTransactionConstants.NOTIFICATION,
                                                                        Mockito.anyObject(),
                                                                        Mockito.anyString()))
            .thenThrow(WirecardPaymenException.class);

        wirecardNotificationController.processNotification(source, httpServletResponse);

        Assert.assertEquals("the response isn't success", httpServletResponse.getStatus(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    }

}
