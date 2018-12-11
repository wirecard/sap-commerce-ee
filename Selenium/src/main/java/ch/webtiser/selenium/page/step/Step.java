package ch.webtiser.selenium.page.step;

import ch.webtiser.selenium.page.AbstractPage;
import ch.webtiser.selenium.page.Page;
import org.openqa.selenium.WebElement;

public interface Step<T> {

    public void fill(T object);

}
