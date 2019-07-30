package ch.webtiser.selenium.model;

import ch.webtiser.selenium.util.PropertyHelper;

public enum Product {
	TEST_PRODUCT(PropertyHelper.loadProperties().getProperty("testproduct.id"), PropertyHelper.loadProperties().getProperty("testproduct.name"), PropertyHelper.loadProperties().getProperty("testproduct.price"), Currency.DEFAULT);

	public static final Product DEFAULT = TEST_PRODUCT;

	private final String id;
	private final String name;
	private final String price;
	private final Currency currency;

	Product(final String id, final String name, final String price, final Currency currency) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.currency = currency;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPrice() {
		return price;
	}

	public String url(final String baseUrl) {
		return baseUrl + "/p/" + id;
	}

	public Currency getCurrency() {
		return currency;
	}

	public String getFullPrice() {
		return price + " " + currency.getShortName();
	}

	@Override
	public String toString() {
		return name;
	}
}
