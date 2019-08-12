package ch.webtiser.selenium.model;

public enum  Country {
    CH, DE, FR;

    @Override
    public String toString() {
        return this.name().toUpperCase();
    }
}
