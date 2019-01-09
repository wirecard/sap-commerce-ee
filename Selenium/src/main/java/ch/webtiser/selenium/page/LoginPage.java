package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends AbstractPage {
	public LoginPage(final WebDriver driver, final Environment env) {
		super(driver, env);
		driver.navigate().to(env.url() + url());
		waitForLoad();
	}

	@Override
	public String url() {
		return "login";
	}

	@Override
	public String title() {
		return "Login | Electronics Site";
	}

	public MainPage loginCustomer(final Customer customer) {
		return loginUser(customer.getUserName(), customer.getPassword());
	}

	public MainPage loginUser(final String userName, final String password) {
		inputUserName().sendKeys(userName);
		inputPassword().sendKeys(password);
		buttonLogin().click();
		waitForLoad();
		return new MainPage(driver, env);
	}

	public LoginPage loadLoginPage() {
		return new LoginPage(driver, env);
	}

	private WebElement inputUserName() {
		return driver.findElement(By.id("j_username"));
	}

	private WebElement inputPassword() {
		return driver.findElement(By.id("j_password"));
	}

	private WebElement buttonLogin() {
		final By path = By.cssSelector(".login-section button");
		return driver.findElement(path);
	}
}
