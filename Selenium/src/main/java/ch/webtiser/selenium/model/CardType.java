package ch.webtiser.selenium.model;

public enum CardType {
	VISA("Visa"),
	MASTERCARD("Mastercard"),
	AMERICAN_EXPRES("American Express"),
	DINERS_CLUB("Diners");

	private final String code;

	CardType(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
