package ch.webtiser.selenium.page.step;

import ch.webtiser.selenium.model.DeliveryAddress;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.*;
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
    private static final By ELEMENT_PHONE= By.id("address.phone");
    private static final By ELEMENT_ADDRESSBOOK= By.cssSelector(".main__inner-wrapper .js-address-book");
    private static final String BIRTHDAY_NAME = "birthday";
    private static final String FORM_PREFIX = "address.";

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
        getPhoneInput().sendKeys(object.getPhone());
    }

    public boolean selectFirstAddressFromAddressBook() {
        final boolean addressBookExist = driver.findElements(ELEMENT_ADDRESSBOOK).size() != 0;
        if(addressBookExist) {
            openAdressBoock();
            final List<WebElement> adresses = getAddressesInBook();
            chooseAddressFromBook(adresses.get(0));
        }
        return addressBookExist;
    }

    private void addBirthdayField() {
        ((JavascriptExecutor)driver).executeScript(
                "var input = document.createElement('input');" +
                    "input.setAttribute('name','" + BIRTHDAY_NAME + "');" +
                    "input.setAttribute('id','" + FORM_PREFIX + BIRTHDAY_NAME + "');" +
                    "document.getElementById('addressForm').appendChild(input);"
        );
    }

    public void openAdressBoock() {
        driver.findElement(ELEMENT_ADDRESSBOOK).click();
    }


    public void closeAdressBoock() {
        driver.findElement(By.id("cboxClose")).click();
    }

    public void chooseAddressFromBook(final WebElement adress) {
        ((JavascriptExecutor)driver).executeScript("document.querySelector('#addressbook form').submit()");
        //adress.findElement(By.cssSelector("button[type=submit]"));
    }

    public List<WebElement> getAddressesInBook() {
        return driver.findElements(By.cssSelector(".addressEntry form"));
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

    private WebElement getPhoneInput() {
        return getElementById(ELEMENT_PHONE);
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
