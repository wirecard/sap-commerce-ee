package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.DeliveryAddress;
import ch.webtiser.selenium.model.DeliveryMethod;
import ch.webtiser.selenium.page.step.ShipmentStep;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AddAddressPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(AddAddressPage.class);
	private final String currentUrl;
	private final String pageTitle;
    public static final String DEFAULT_URL ="my-account/add-address";

	private final By ELEMENT_SAVE = By.className("change_address_button");

	public AddAddressPage(final WebDriver driver, final Environment env) {
		super(driver, env);
        driver.get(env.url() + DEFAULT_URL);
		waitForLoad();
		currentUrl = driver.getCurrentUrl();
		pageTitle = driver.getTitle();
	}

	public static AddAddressPage goTo(final WebDriver driver, final Environment env) {
		final AddAddressPage page = new AddAddressPage(driver, env);
		return page;
	}

	public void addAddresse(final DeliveryAddress toAdd) {
		final ShipmentStep shipmentStep = new ShipmentStep(driver, env);
		shipmentStep.fill(toAdd);
		getSaveButton().click();
		waitForLoad(driver, env);
	}


	public WebElement getSaveButton() {
		return driver.findElement(ELEMENT_SAVE);
	}
	@Override
	public String url() {
		return currentUrl;
	}

	@Override
	public String title() {
		return pageTitle;
	}


}
