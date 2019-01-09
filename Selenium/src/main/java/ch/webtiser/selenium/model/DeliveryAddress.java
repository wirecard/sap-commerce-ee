package ch.webtiser.selenium.model;

public enum DeliveryAddress {
	HOME(Title.MR, "Mark", "Rivers", Country.CH, "9999", "Examplestreet 99", "Example City", "041 811 12 12");

	public static final DeliveryAddress DEFAULT = HOME;

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
