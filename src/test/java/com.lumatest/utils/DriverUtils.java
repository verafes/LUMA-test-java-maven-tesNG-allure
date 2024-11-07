package com.lumatest.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

public class DriverUtils {
    private static final String ENV_CHROME_OPTIONS = "CHROME_OPTIONS";
    private static final String ENV_BROWSER_OPTIONS = "BROWSER_OPTIONS";
    private static final String PREFIX_PROP = "default.";
    private static final String PROP_CHROME_OPTIONS = PREFIX_PROP + ENV_CHROME_OPTIONS.trim().toLowerCase();
    private static Properties properties;
    private static void initProperties() {
        if (properties == null) {
            properties = new Properties();
            if (isServerRun()) {
                properties.setProperty(PROP_CHROME_OPTIONS, System.getenv(ENV_CHROME_OPTIONS));
                if (System.getenv(ENV_BROWSER_OPTIONS) != null) {
                    for (String option : System.getenv(ENV_BROWSER_OPTIONS).split(";")) {
                        String[] browserOptionArr = option.split("=");
                        properties.setProperty(browserOptionArr[0], browserOptionArr[1]);
                    }
                }
            } else {
                try {
                    InputStream inputStream = DriverUtils.class.getClassLoader().getResourceAsStream("local.properties");
                    if (inputStream == null) {
                        System.out.println("ERROR: The \u001B[31mlocal.properties\u001B[0m file not found in src/test/resources/ directory.");
                        System.out.println("You need to create it from local.properties.TEMPLATE file.");
                        System.exit(1);
                    }
                    properties.load(inputStream);
                } catch (IOException ignore) {
                }
            }
        }
    }
    static boolean isServerRun() {
        return System.getenv("CI_RUN") != null;
    }
    private static final ChromeOptions chromeOptions = new ChromeOptions();
    private static final FirefoxOptions firefoxOptions = new FirefoxOptions();
    private static final EdgeOptions edgeOptions = new EdgeOptions();

    static {
        initProperties();
        setupChromeOptions();
        setupFirefoxOptions();
    }

    private static void setupChromeOptions() {
        String options = properties.getProperty(PROP_CHROME_OPTIONS);

        if (options != null) {
            for (String argument : options.split(";")) {
                chromeOptions.addArguments(argument);
            }
        }

        WebDriverManager.chromedriver().setup();
    }

    private static void setupFirefoxOptions() {
        firefoxOptions.addArguments("--incognito");
        firefoxOptions.addArguments("--headless");
        firefoxOptions.addArguments("--window-size=1920,1080");
        firefoxOptions.addArguments("--disable-gpu");
        firefoxOptions.addArguments("--no-sandbox");
        firefoxOptions.addArguments("--disable-dev-shm-usage");
        firefoxOptions.addArguments("--disable-web-security");
        firefoxOptions.addArguments("--allow-running-insecure-content");
        firefoxOptions.addArguments("--ignore-certificate-errors");
        firefoxOptions.setLogLevel(FirefoxDriverLogLevel.TRACE);
        firefoxOptions.addPreference("remote.log.truncate", false);
        firefoxOptions.addPreference("extensions.logging.enabled", true);
        firefoxOptions.setCapability("acceptInsecureCerts", true);
    }

    private static void setupEdgeOptions() {
        edgeOptions.addArguments("--incognito");
        edgeOptions.addArguments("--headless");
        edgeOptions.addArguments("--window-size=1920,1080");
        edgeOptions.addArguments("--disable-gpu");
        edgeOptions.addArguments("--no-sandbox");
        edgeOptions.addArguments("--disable-dev-shm-usage");
        edgeOptions.addArguments("--disable-web-security");
        edgeOptions.addArguments("--allow-running-insecure-content");
        edgeOptions.addArguments("--ignore-certificate-errors");
    }

    private static WebDriver createChromeDriver(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.executeCdpCommand("Network.enable", Map.of());
        chromeDriver.executeCdpCommand("Network.setExtraHTTPHeaders", Map.of("headers", Map.of("accept-language", "en-US,en;q=0.9")));

        return chromeDriver;
    }

    private static WebDriver createFirefoxDriver(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
        try {
            WebDriverManager.firefoxdriver().setup();
            setupFirefoxOptions();

            return new FirefoxDriver(firefoxOptions);
        } catch (Exception e) {
            System.out.println("Error creating Firefox driver: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static WebDriver createDriver(String browser, WebDriver driver) {
        return switch(browser) {
            case "chrome" -> createChromeDriver(driver);
            case "firefox" -> createFirefoxDriver(driver);
            default -> null;
        };
    }
}
