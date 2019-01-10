package ch.webtiser.selenium.model;

import ch.webtiser.selenium.util.PropertyHelper;

public enum DeliveryAddress {
	DEFAULT(Title.valueOf(PropertyHelper.getProperty("address.default.title")),
			PropertyHelper.getProperty("address.default.firstName"),
			PropertyHelper.getProperty("address.default.lastName"),
			Country.valueOf(PropertyHelper.getProperty("address.default.Country")),
			PropertyHelper.getProperty("address.default.postcode"),
			PropertyHelper.getProperty("address.default.address"),
			PropertyHelper.getProperty("address.default.city"),
			PropertyHelper.getProperty("address.default.phone")),

	SECOND_ADDRESS(Title.valueOf(PropertyHelper.getProperty("address.secondAddress.title")),
			PropertyHelper.getProperty("address.secondAddress.firstName"),
			PropertyHelper.getProperty("address.secondAddress.lastName"),
			Country.valueOf(PropertyHelper.getProperty("address.secondAddress.Country")),
			PropertyHelper.getProperty("address.secondAddress.postcode"),
			PropertyHelper.getProperty("address.secondAddress.address"),
			PropertyHelper.getProperty("address.secondAddress.city"),
			PropertyHelper.getProperty("address.secondAddress.phone"));

	private final Title title;
	private final String firstName;
	private final String lastName;
	private final Country country;
	private final String postcode;
	private final String address;
	private final String city;
    private final String phone;

	DeliveryAddress(final Title title, final String firstName, final String lastName,
                    final Country country, final String postcode, final String address,
                    final String city, String phone) {
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.country = country;
		this.postcode = postcode;
		this.address = address;
		this.city = city;
        this.phone = phone;
    }

	public Title getTitle() {
		return title;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Country getCountry() {
		return country;
	}

	public String getPostcode() {
		return postcode;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

    public String getPhone() {
        return phone;
    }
}
