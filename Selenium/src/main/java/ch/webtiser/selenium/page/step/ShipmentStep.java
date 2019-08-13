package ch.webtiser.selenium.page.step;

import ch.webtiser.selenium.model.DeliveryAddress;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ShipmentStep extends AbstractStep<DeliveryAddress> {

    private static final String DEFAULT_URL = "checkout/multi/delivery-address/add";

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
            openAddressBook();
            final List<WebElement> adresses = getAddressesInBook();
            chooseAddressFromBook(adresses.get(0));
        }
        return addressBookExist;
    }

    public void selectAddressFromAddressBook(final DeliveryAddress toSelect) {
        openAddressBook();
        for(int i = 1;;i++) {
            WebElement address = driver.findElement(By.cssSelector(".addressEntry:nth-child(" + i + ")"));
            new WebDriverWait(driver, 1).until(wd -> !address.findElement(By.tagName("li")).getText().isEmpty());
            final String addressInfo = address.findElement(By.tagName("li")).getText();
            if(compareAddressWithBockEntry(toSelect, addressInfo.split("\n"))) {
                address.findElement(By.xpath(".//button[@type='submit']")).click();
                break;
            }
        }
    }

    private boolean compareAddressWithBockEntry(final DeliveryAddress address, final String[] entry) {
        final String firstLine = removeWhitespaces(address.getFirstName() + address.getLastName());
        final String[] cleanedEntry = new String[entry.length];
        for(int i = 0; i < cleanedEntry.length; i++) {
            cleanedEntry[i] = removeWhitespaces(entry[i]);
        }
        return cleanedEntry[0].contains(firstLine)
                && removeWhitespaces(address.getAddress()).equals(cleanedEntry[1])
                && removeWhitespaces(address.getCity()).equals(cleanedEntry[2])
                && cleanedEntry[3].contains(address.getPostcode());
    }

    private String removeWhitespaces(final String toClean) {
        return toClean.replace(" ", "").toLowerCase();
    }


    public void openAddressBook() {
        driver.findElement(ELEMENT_ADDRESSBOOK).click();
    }


    public void closeAdressBook() {
        driver.findElement(By.id("cboxClose")).click();
    }

    @Override
    public void goTo() {
        driver.get(env.url() + DEFAULT_URL);
    }

    public void chooseAddressFromBook(final WebElement adress) {
        ((JavascriptExecutor)driver).executeScript("document.querySelector('#addressbook form').submit()");
        //adress.findElement(By.cssSelector("button[type=submit]"));
    }

    public List<WebElement> getAddressesInBook() {
        return driver.findElements(By.cssSelector(".addressEntry form"));
    }

    private Select getCountrySelect() {
        return new Select(find(ELEMENT_COUNTRY));
    }

    private Select getTitleSelect() {
        return new Select(find(ELEMENT_TITLE));
    }

    private WebElement getFirstNameInput() {
        return find(ELEMENT_FIRST_NAME);
    }

    private WebElement getLastNameInput() {
        return find(ELEMENT_LAST_NAME);
    }

    private WebElement getAdressLine1Input() {
        return find(ELEMENT_ADDRESS_LINE1);
    }

    private WebElement getAdressLine2Input() {
        return find(ELEMENT_ADDRESS_LINE2);
    }

    private WebElement getCityInput() {
        return find(ELEMENT_CITY);
    }

    private WebElement getPostCodeInput() {
        return find(ELEMENT_POST_CODE);
    }

    private WebElement getPhoneInput() {
        return find(ELEMENT_PHONE);
    }

    private WebElement getPhoneNumberInput() {
        return find(ELEMENT_PHONE_NUMBER);
    }

    private WebElement getSaveshippingCheckbox() {
        return find(ELEMENT_SAVE_SHIPPING);
    }

    private WebElement getSavedAddresse() {
        return driver.findElement(By.className("addressEntry"));
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
