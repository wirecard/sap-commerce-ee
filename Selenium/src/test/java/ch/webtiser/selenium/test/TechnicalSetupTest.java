package ch.webtiser.selenium.test;

import ch.webtiser.selenium.model.Language;
import ch.webtiser.selenium.util.enums.Environment;
import ch.webtiser.selenium.util.enums.OperatingSystem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TechnicalSetupTest {
	private static WebDriver driver;
	private static Environment env;
	private static OperatingSystem os;

	@BeforeClass
	public static void openBrowser() {
		env = Environment.select();
		os = OperatingSystem.select();
		System.setProperty("webdriver.chrome.driver", os.webDriverPath());
		final ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--disable-gpu");
		options.addArguments("--disable-extensions");
		options.setExperimentalOption("useAutomationExtension", false);
		options.addArguments("--proxy-server='direct://'");
		options.addArguments("--proxy-bypass-list=*");
		options.addArguments("--start-maximized");
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void closeBrowser() {
		driver.quit();
	}

	@Test()
	public void browserInitTest() {
		driver.navigate().to(env.url(Language.ENGLISH));
		assertEquals("Browser can open the main-page and see the expected title.",
				"Powertools Site | Homepage", driver.getTitle());
	}
}
