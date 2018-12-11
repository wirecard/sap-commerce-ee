package ch.webtiser.selenium.model;

public enum Title {
    MR, MRS, MISS, MS, DR, REV;

    public static final Title DEFAULT = MR;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
