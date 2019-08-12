package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.model.Country;
import ch.webtiser.selenium.util.PropertyHelper;

public class SofortMethod extends Payment {

     public static final Payment DEFAULT = new SofortMethod(
             Country.valueOf(PropertyHelper.loadProperties().getProperty("sofort.default.country")),
             PropertyHelper.loadProperties().getProperty("sofort.default.bankName"),
             PropertyHelper.loadProperties().getProperty("sofort.default.contractNumber"),
             PropertyHelper.loadProperties().getProperty("sofort.default.password"),
             PropertyHelper.loadProperties().getProperty("sofort.default.tan"),
             Integer.parseInt(PropertyHelper.loadProperties().getProperty("sofort.default.accountId"))
    );

    private ch.webtiser.selenium.model.Country countryOfBank ;
    private String bankName;
    private String contractNumber;
    private String password;
    private String tan;
    private int accountId;

    public SofortMethod( Country countryOfBank, String bankName, String contractNumber, String password, String tan,int accountId) {
        super(PaymentType.SOFORT, false);
        this.countryOfBank = countryOfBank;
        this.bankName = bankName;
        this.contractNumber = contractNumber;
        this.password = password;
        this.tan = tan;
        this.accountId = accountId;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Payment getDEFAULT() {
        return DEFAULT;
    }

    public Country getCountryOfBank() {
        return countryOfBank;
    }

    public void setCountryOfBank(Country countryOfBank) {
        this.countryOfBank = countryOfBank;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getTan() {
        return tan;
    }

    public void setTan(String tan) {
        this.tan = tan;
    }
}
