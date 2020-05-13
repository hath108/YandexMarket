package testYandexMarket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WebDriverSettings {

    protected Logger logger = LoggerFactory.getLogger(" ");

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    public void startTest() {

        logger.info("--- beforeEach ---");
//      System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");
//      driver = new ChromeDriver();

        System.setProperty("webdriver.firefox.driver", "C:/geckodriver/geckodriver.exe");
        driver = new FirefoxDriver();

        wait = new WebDriverWait(driver, 15);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().deleteAllCookies();
    }

    @AfterEach
    public void endTest() {
        logger.info("--- afterEach ---");
        driver.quit();
    }
}
