package ch.webtiser.selenium.page.step;

import ch.webtiser.selenium.model.DeliveryAddress;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ShipmentStep extends AbstractStep<DeliveryAddress> {

    private static final By ELEMENT_COUNTRY = By.id("address.country");
    private static final By ELEMENT_TITLE = By.id("address.title");
    private static final By ELEMENT_FIRST_NAME = By.id("address.firstName");
    private static final By ELEMENT_LAST_NAME = By.id("address.surname");
    private static final By ELEMENT_ADDRESS_LINE1 = By.id("address.line1");
    private static final By ELEMENT_ADDRESS_LINE2 = By.id("address.line2");
    private static final By ELEMENT_CITY = By.id("address.townCity");
    private static final By ELEMENT_POST_CODE = By.id("address.postcode");
    private static final By ELEMENT_PHONE_NUMBER = By.id("address.phone");
    private static final By ELEMENT_SAVE_SHIPPING = By.id("saveAddressInMyAddressBook");

    public ShipmentStep(WebDriver driver, Environment env) {
        super(driver, env);
    }

    @Override
    public void fill(DeliveryAddress object) {
        getCountrySelect().selectByValue(object.getCountry().toString());
        getTitleSelect().selectByValue(object.getTitle().toString());
        getFirstNameInput().sendKeys(object.getFirstName());
        getLastNameInput().sendKeys(object.getLastName());
        getAdressLine1Input().sendKeys(object.getAddress());
        getCityInput().sendKeys(object.getCity());
        getPostCodeInput().sendKeys(object.getPostcode());
    }

    public void selectFirstAdressFromAdressBook() {
        openAdressBoock();
        final List<WebElement> adresses = getAdressesInBook();
        chooseAddressFromBook(adresses.get(0));
    }

    public void openAdressBoock() {
        driver.findElement(By.className("js-address-book")).click();
    }

    public void chooseAddressFromBook(final WebElement adress) {
        adress.findElement(By.cssSelector("button[type=submit]"));
    }

    public List<WebElement> getAdressesInBook() {
        return driver.findElements(By.cssSelector("addressEntry.form"));
    }

    private Select getCountrySelect() {
        return new Select(getElementById(ELEMENT_COUNTRY));
    }

    private Select getTitleSelect() {
        return new Select(getElementById(ELEMENT_TITLE));
    }

    private WebElement getFirstNameInput() {
        return getElementById(ELEMENT_FIRST_NAME);
    }

    private WebElement getLastNameInput() {
        return getElementById(ELEMENT_LAST_NAME);
    }

    private WebElement getAdressLine1Input() {
        return getElementById(ELEMENT_ADDRESS_LINE1);
    }

    private WebElement getAdressLine2Input() {
        return getElementById(ELEMENT_ADDRESS_LINE2);
    }

    private WebElement getCityInput() {
        return getElementById(ELEMENT_CITY);
    }

    private WebElement getPostCodeInput() {
        return getElementById(ELEMENT_POST_CODE);
    }

    private WebElement getPhoneNumberInput() {
        return getElementById(ELEMENT_PHONE_NUMBER);
    }

    private WebElement getSaveshippingCheckbox() {
        return getElementById(ELEMENT_SAVE_SHIPPING);
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String title() {
        return null;
    }

}
