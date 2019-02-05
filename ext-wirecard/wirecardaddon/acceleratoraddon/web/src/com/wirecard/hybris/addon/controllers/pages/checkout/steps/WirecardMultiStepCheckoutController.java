package com.wirecard.hybris.addon.controllers.pages.checkout.steps;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorfacades.ordergridform.OrderGridFormFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

@Controller
@RequestMapping(value = "/checkout/multi/wirecard")
public class WirecardMultiStepCheckoutController extends AbstractCheckoutStepController
{
	private static final String MULTI = "multi";
	private static final String REDIRECT_CART_URL = REDIRECT_PREFIX + "/cart";
	private static final String REDIRECT_URL_WIRECARD_ADD_PAYMENT_METHOD = REDIRECT_PREFIX
			+ "/checkout/multi/wirecard/payment-method/add";


	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource(name = "orderGridFormFacade")
	private OrderGridFormFacade orderGridFormFacade;
	
	
	@RequestMapping(value = "/express", method = RequestMethod.GET)
	@RequireHardLogIn
	public String performExpressCheckout(final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, CommerceCartModificationException // NOSONAR
	{		
		
		if (getSessionService().getAttribute(WebConstants.CART_RESTORATION) != null && CollectionUtils.isNotEmpty(
				((CartRestorationData) getSessionService().getAttribute(WebConstants.CART_RESTORATION)).getModifications()))
		{
			return REDIRECT_URL_CART;
		}

		if (getCheckoutFlowFacade().hasValidCart())
		{
			final String result = processCart(redirectModel);
			if (result != null)
			{
				return result;
			}
		}

		return enterStep(model, redirectModel);
	}


	@Override
	@RequestMapping(method = RequestMethod.GET)
	@PreValidateQuoteCheckoutStep
	@PreValidateCheckoutStep(checkoutStep = MULTI)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, // NOSONAR
			CommerceCartModificationException
	{
		if (validateCart(redirectAttributes))
		{
			return REDIRECT_CART_URL;
		}

		return getCheckoutStep().nextStep();
	}


	@RequestMapping(value = "/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}

	
	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(MULTI);
	}
	
	protected String processCart(final RedirectAttributes redirectModel)
	{
		switch (getCheckoutFacade().performExpressCheckout())
		{
			case SUCCESS:
				return REDIRECT_URL_SUMMARY;

			case ERROR_DELIVERY_ADDRESS:
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"checkout.express.error.deliveryAddress");
				return REDIRECT_URL_ADD_DELIVERY_ADDRESS;

			case ERROR_DELIVERY_MODE:
			case ERROR_CHEAPEST_DELIVERY_MODE:
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"checkout.express.error.deliveryMode");
				return REDIRECT_URL_CHOOSE_DELIVERY_METHOD;

			case ERROR_PAYMENT_INFO:
				return REDIRECT_URL_WIRECARD_ADD_PAYMENT_METHOD;
			default:
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"checkout.express.error.notAvailable");
		}
		return null;
	}
}
