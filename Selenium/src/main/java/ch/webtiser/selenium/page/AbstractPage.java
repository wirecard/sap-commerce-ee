package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Language;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractPage implements Page {
	protected final WebDriver driver;
	protected final Environment env;
	protected final String currentUrl;
	protected final String pageTitle;

	public AbstractPage(final WebDriver driver, final Environment env) {
		this.driver = driver;
		this.env = env;
		this.currentUrl = driver.getCurrentUrl();
		this.pageTitle = driver.getTitle();
	}

	public void waitForLoad() {
		AbstractPage.waitForLoad(driver, env);
	}

	public static void waitForLoad(final WebDriver driver, final Environment env) {
		new WebDriverWait(driver, env.timeoutSeconds()).until(wd ->
				((JavascriptExecutor) wd)
						.executeScript("return document.readyState")
						.equals("complete"));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public String url() {
		return currentUrl;
	}

	@Override
	public String title() {
		return pageTitle;
	}

	protected WebElement getElementById(final By by) {
		return driver.findElement(by);
	}
}
