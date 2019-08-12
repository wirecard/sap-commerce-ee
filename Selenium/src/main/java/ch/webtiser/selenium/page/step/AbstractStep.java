package ch.webtiser.selenium.page.step;

import ch.webtiser.selenium.page.AbstractPage;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractStep<T> extends AbstractPage implements Step<T> {

    final static By ELEMENT_NEXT_BUTTON = By.id("checkout-next");

    public AbstractStep(WebDriver driver, Environment env) {
        super(driver, env);
    }

    public WebElement getNextButton() {
        return driver.findElement(ELEMENT_NEXT_BUTTON);
    }

    public void submitStep() {
        getNextButton().click();
    }

}
