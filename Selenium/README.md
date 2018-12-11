# Selenium Setup:
It will run with Chrome as a default option.

## Prerequisites
1. Download chromdriver for your system:
	`https://chromedriver.storage.googleapis.com/index.html`
    Beware correct version: http://chromedriver.chromium.org/downloads
2. Place at the location specified in the `config/exec.properties` file for your system or adapt the corresponding property. 

## Configuration
Configuration of the target environments can be found in the `config/target.properties` file. Environments are defined by a entry `url` and a `marker` defining the environment.
The target system can be selected by setting the system property `SELENIUM_TARGET_ENV` it defaults to `local`.

Configuration of the local execution environment is stored in `config/exec.properties`. The include the path to the driver executable and the reference to the log path.
The target system can be selected by setting the system property `SELENIUM_EXEC_ENV` it defaults to `windows`.

