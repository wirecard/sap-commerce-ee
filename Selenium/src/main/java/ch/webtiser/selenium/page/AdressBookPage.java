package ch.webtiser.selenium.page;

import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdressBookPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(AdressBookPage.class);
	private final String currentUrl;
	private final String pageTitle;
	private final String URL ="my-account/address-book";

	private final By ELEMENT_REMOVE_ADRESS_ICON = By.className("removeAddressFromBookButton");
	private final By ELEMENT_REMOVE_ADRESS_BUTTON = By.xpath("//div[@class=''account-address-removal-popup]/a[contains(@href, 'remove')]");

	public AdressBookPage(final WebDriver driver, final Environment env) {
		super(driver, env);
		driver.navigate().to(env.baseUrl() + URL);
		waitForLoad();
		currentUrl = driver.getCurrentUrl();
		pageTitle = driver.getTitle();
	}

	public static AdressBookPage goTo(final WebDriver driver, final Environment env) {
		final AdressBookPage page = new AdressBookPage(driver, env);
		return page;
	}

	public void removeAllAdresses() {
		final List<WebElement> icons = getRemoveIcons();
		for(final WebElement icon : icons) {
			icon.click();
			getRemoveButton().click();
			waitForLoad(driver, env);
		}
	}


	public List<WebElement> getRemoveIcons() {
		return driver.findElements(ELEMENT_REMOVE_ADRESS_ICON);
	}

	public WebElement getRemoveButton() {
		return driver.findElement(ELEMENT_REMOVE_ADRESS_BUTTON);
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
