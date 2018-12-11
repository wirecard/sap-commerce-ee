package ch.webtiser.selenium.model;

import ch.webtiser.selenium.model.payment.Payment;
import ch.webtiser.selenium.model.payment.PaymentType;
import ch.webtiser.selenium.util.PropertyHelper;

public enum Customer {
	TESTUSER("test-customer@selenium.corp", "12341234", "Selen", "Ium"),
	BERNARD_CUSTOMER(PropertyHelper.loadProperties().getProperty("testuser.name"), PropertyHelper.loadProperties().getProperty("testuser.password"), "Bernard", "Customer");
	//any additional users and properties have to be imported as sample data.
	//see selenium-data.impex in the 'wtinitialdata' extension.

	public static final Customer DEFAULT = BERNARD_CUSTOMER;

	private String userName;
	private String password;
	private String firstName;
	private String lastName;
	private Title title;
	private DeliveryAddress deliveryAddress;
	private DeliveryMethod deliveryMethod;
	private PaymentType paymentType;
	private Payment payment;

	Customer(final String userName,
	         final String password,
	         final String firstName,
	         final String lastName) {
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;

		//general defaults for all customers
		this.title = Title.DEFAULT;
		this.deliveryAddress = DeliveryAddress.DEFAULT;
		this.deliveryMethod = DeliveryMethod.DEFAULT;
		this.paymentType = PaymentType.DEFAULT;
		this.payment = Payment.DEFAULT;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Title getTitle() {
		return title;
	}

	public DeliveryAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	public DeliveryMethod getDeliveryMethod() {
		return deliveryMethod;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public Payment getPayment() {
		return payment;
	}

	@Override
	public String toString() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
}
