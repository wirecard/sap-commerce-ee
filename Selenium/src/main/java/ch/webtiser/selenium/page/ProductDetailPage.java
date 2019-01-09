package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Product;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductDetailPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(ProductDetailPage.class);
	private final String currentUrl;
	private final String pageTitle;

	private ProductDetailPage(final WebDriver driver, final Environment env, final Product product) {
		super(driver, env);
		driver.navigate().to(product.url(env.baseUrl()));
		waitForLoad();
		currentUrl = driver.getCurrentUrl();
		pageTitle = driver.getTitle();
	}

	public static ProductDetailPage goTo(final WebDriver driver, final Environment env, final Product product) {
		final ProductDetailPage page = new ProductDetailPage(driver, env, product);
		LOG.debug("Loaded {} for product {}.", page, product);
		return page;
	}


	@Override
	public String url() {
		return currentUrl;
	}

	@Override
	public String title() {
		return pageTitle;
	}

	public WebElement buttonAddToCart() {
		return driver.findElement(By.id("addToCartButton"));
	}

	public WebElement inputQuantity() {
		return driver.findElement(By.id("pdpAddtoCartInput"));
	}


}
