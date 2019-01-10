package ch.webtiser.selenium.page.step;

import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ReviewStep extends AbstractStep<Boolean> {

    private static final String DEFAULT_URL = "checkout/multi/wirecard/summary/view";

    private static final By ELEMENT_TERMS = By.id("Terms1");
    private static final By ELEMENT_PLACE_ORDER = By.id("placeOrder");

    public ReviewStep(WebDriver driver, Environment env) {
        super(driver, env);
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String title() {
        return null;
    }

    @Override
    public void fill(Boolean object) {
        final WebElement terms = getTermsCheckbox();
        if(terms.isSelected() != object) {
            getTermsCheckbox().click();
        }
    }

    @Override
    public void goTo() {
        driver.get(env.url() + DEFAULT_URL);
    }

    public void placeOrder() {
        getPlaceOrderButton().click();
    }

    private WebElement getTermsCheckbox() {
        return getElementById(ELEMENT_TERMS);
    }

    private WebElement getPlaceOrderButton() {
        return getElementById(ELEMENT_PLACE_ORDER);
    }
}
