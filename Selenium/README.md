# Selenium Setup:
It will run with Chrome as a default option.

## Prerequisites
1. Download chromedriver for your system:
	`https://chromedriver.storage.googleapis.com/index.html`
    Beware correct version: http://chromedriver.chromium.org/downloads
2. Place at the location specified in the `src/main/resources/selenium.properties` file. 
3. Change the properties of the default Customer in `src/main/resources/selenium.properties`.
4. Change the properties of the default Product in `src/main/resources/selenium.properties`.
5. If you don't want to use USD or JPY as currency, change the default currency of the electronics store and add a price to the default product in backoffice for that currency. 
5. Make sure all Payments are active and available for the Country, Currency and Customer you are using. 
Not only in backoffice but also if the payment provider like Paypal does support it.

## Configuration
Configuration and test data like credentials can be changed in `src/main/resources/selenium.properties`

## Information
Sometimes test can fail because it takes too long to load a side. 
If that happens either change the Timeout property in the config or rerun and hope that it will pass. There are strong dependencies to provider sandboxes which can sometimes take longer to load than usual.
