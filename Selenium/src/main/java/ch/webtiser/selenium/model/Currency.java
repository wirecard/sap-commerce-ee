package ch.webtiser.selenium.model;

public enum Currency {
    CHF("Schweizer Franken", "CHF", "SFr."),
    EUR("Euro", "EUR", "€"),
    USD("US-Dollar", "USD", "$");

    public static final Currency DEFAULT = CHF;

    private final String name;
    private final String isoCode;
    private final String shortName;

    Currency(final String name, final String isoCode, final String shortName) {
        this.name = name;
        this.isoCode = isoCode;
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String toString() {
        return getName();
    }
}
