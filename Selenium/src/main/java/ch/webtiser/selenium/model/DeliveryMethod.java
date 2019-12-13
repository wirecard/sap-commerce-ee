package ch.webtiser.selenium.model;

public enum DeliveryMethod {
    STANDARD_NET("standard-net"),
    PREMIUM_NET("premium-net");

    public static final DeliveryMethod DEFAULT = STANDARD_NET;

    private final String name;

    DeliveryMethod(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
