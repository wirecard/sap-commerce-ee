package ch.webtiser.selenium.PaymentFiller;

import ch.webtiser.selenium.model.payment.Payment;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractPaymentFiller<T extends  Payment> implements PaymentFiller<T> {
    protected final WebDriver driver;
    protected Environment env;

    public AbstractPaymentFiller(final WebDriver driver, final Environment env) {
        this.driver = driver;
        this.env = env;
    }

    protected void switchToFrame(final Payment payment) {
        final String frameCssSelector = "#"+ payment.getPaymentType().toString().toLowerCase() +"-form-div .wirecard-seamless-frame";
        WebElement frame =  driver.findElement(By.cssSelector(frameCssSelector));
        driver.switchTo().frame(frame);
    }

}
