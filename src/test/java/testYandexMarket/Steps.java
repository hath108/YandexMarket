package testYandexMarket;


import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


import java.util.ArrayList;
import java.util.List;

public class Steps extends WebDriverSettings {


    private final By PRODUCTNAME = By.xpath("//h3/a[contains(@title, '')]");
    private final By YANDEXMARKETPAGE = By.xpath("//a[@data-id='market']");
    private final By LISTBOX = By.xpath("//button[@role='listbox']");
    private final By TWELVELOCATOR = By.xpath("//span[contains(text(), 'Показывать по 12')]");
    private final By PRICEFROM = By.id("glpricefrom");
    private final By PRICETO = By.id("glpriceto");
    private final By SEARCHFIELD = By.id("header-search");
    private final By SUBMITBUTTON = By.xpath("//button[@type='submit']");
    private List<WebElement> list = new ArrayList<>();

    @Step("Открываем главную страницу Яндекса")
    public void openYandexPage() {
        driver.get("https://yandex.ru/");
        logger.info(driver.getTitle());
    }

    @Step("Открываем ЯндексМаркет")
    public void goToYandexMarket() {

        try {
            waitFor(YANDEXMARKETPAGE).click();
            wait.until(ExpectedConditions
                    .numberOfWindowsToBe(2));
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(1));

        } catch (Exception e) {
            logger.error("goToYandexMarket failed");
        }
    }

    @Step("Переходим в раздел по селектору {selector}")
    public void selectChapter(String selector) {
        try {
            waitFor(By.xpath(selector)).click();
            logger.info("selectChapter " + selector + " ...successful");

        } catch (Exception e) {
            logger.error("Chapter " + selector + " not found");
        }
    }

    @Step("Устанавливаем цену от  {from} до {to}")
    public void setPrice(String from, String to) {
        try {
            WebElement priceFrom = waitFor(PRICEFROM);
            WebElement priceTo = waitFor(PRICETO);

            priceFrom.sendKeys(from);
            priceTo.sendKeys(to + "\n");
            wait.until(ExpectedConditions.refreshed(ExpectedConditions
                    .elementToBeClickable(PRICETO)));

            logger.info("Price is set from: " + priceFrom.getAttribute("value") + " to: " + priceTo.getAttribute("value"));
            logger.info("setPrice ...successful");

        } catch (Exception e) {
            logger.error("Price fields not found");
        }
    }

    @Step("Выбираем производителей {args}")
    public void setManufacturers(String[] args) {
        try {
            // проверяем что на странице загрузилось как минимум 5 ноутбуков
            // сохраняем в список все загрузившиеся к текущему моменту ноутбуки
            list = collectNamesOfProductsOnPage(10);

            // ждем появления на странице чекбоксов с переданными параметрами и чекаем
            for (String arg : args) {
                By manufacturer = By.xpath("//div/span[text()='" + arg + "']");
                waitFor(manufacturer).click();
            }
            // ожидание обновления страницы
            checkStalenessOf(list);
            logger.info("setManufacturers ...successful");

        } catch (Exception e) {
            logger.error("Manufacturers not found");
        }
    }

    @Step("Устанавлием 12 позиций на страницу")
    public void setTwelvePerPage() {
        try {
            list = collectNamesOfProductsOnPage(10);
            // ждем появления на странице селектора количества отображаемых элементов
            waitFor(LISTBOX).click();
            // ждем пока появится "Показывать по 12" в выпадающем списке
            waitFor(TWELVELOCATOR).click();
            // ожидание обновления страницы
            checkStalenessOf(list);
            list = collectNamesOfProductsOnPage(11);
            Assertions.assertEquals(12, list.size(), "Actual list size is" + list.size());
            logger.info("Элементов на странице: " + list.size());
        } catch (Exception e) {
            logger.error("setTwelvePerPage failed");
        }
    }

    @Step("Выбираем первый элемент из списка")
    public String selectFirstElement() {
        String firstElementOnPage = "";
        try {
            list = collectNamesOfProductsOnPage(1);
            firstElementOnPage = waitFor(PRODUCTNAME).getText();

            logger.info("firstElementOnPage = " + firstElementOnPage);
            logger.info("selectFirstElement ...successful");

        } catch (Exception e) {
            logger.error("selectFirstElement failed");
        }
        return firstElementOnPage;
    }

    @Step("Проверка на совпадение первого элемента списка")
    public void assertFirstElement(String element) {

        try {
            list = collectNamesOfProductsOnPage(5);
            waitFor(SEARCHFIELD).sendKeys(element);
            waitFor(SUBMITBUTTON).click();
            checkStalenessOf(list);
            String newFirstElementOnPage = waitFor(PRODUCTNAME).getText();
            logger.info("newFirstElementOnPage = " + newFirstElementOnPage);
            Assertions.assertEquals(element, newFirstElementOnPage, "Elements are not equal");

        } catch (Exception e) {
            logger.error("assertFirstElement failed");
        }
    }

    private WebElement waitFor(By selector) {
        return wait.until(ExpectedConditions.elementToBeClickable(driver
                .findElement(selector)));
    }

    public void checkStalenessOf(List<WebElement> list) {

        int count = 0;
        // ждем пока вебэлементы(ноутбуки) из сохраненного списка больше не отображатются
        for (WebElement el : list) {
            wait.until(ExpectedConditions.stalenessOf(el));
            if (count++ > 10) break;
        }
    }

    public List<WebElement> collectNamesOfProductsOnPage(int amount) {
        list.clear();
        // проверяем что на странице загрузилось как минимум переданное кол-во ноутбуков
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCTNAME, amount));
        // сохраняем в список все загрузившиеся к текущему моменту ноутбуки
        list = driver.findElements(PRODUCTNAME);
        return list;
    }
}
