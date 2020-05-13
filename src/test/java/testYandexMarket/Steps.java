package testYandexMarket;


import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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


    private List<WebElement> list;

    @Step("Открываем главную страницу Яндекса")
    public void openYandexPage() {
        driver.get("https://yandex.ru/");
        System.out.println(driver.getTitle());
    }

    @Step("Открываем ЯндексМаркет")
    public void goToYandexMarket() {

        try {
            wait.until(ExpectedConditions.elementToBeClickable(driver
                    .findElement(YANDEXMARKETPAGE)))
                    .click();
            wait.until(ExpectedConditions
                    .numberOfWindowsToBe(2));
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(1));

        } catch (Exception e) {
            System.out.println("goToYandexMarket failed");
        }
    }

    @Step("Переходим в раздел по селектору {selector}")
    public void selectChapter(String selector) {
        try {
            wait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath(selector)))
                    .click();
            System.out.println("selectChapter " + selector + " ...successful");

        } catch (Exception e) {
            System.out.println("Chapter " + selector + " not found");
        }
    }

    @Step("Устанавливаем цену от  {from} до {to}")
    public void setPrice(String from, String to) {
        try {
            WebElement priceFrom = wait.until(ExpectedConditions.elementToBeClickable(PRICEFROM));
            WebElement priceTo = wait.until(ExpectedConditions.elementToBeClickable(PRICETO));

            priceFrom.sendKeys(from);
            priceTo.sendKeys(to + "\n");

            System.out.println("Price is set from: " + priceFrom.getAttribute("value") + " to: " + priceTo.getAttribute("value"));

            System.out.println("setPrice ...successful");

        } catch (Exception e) {
            System.out.println("Price fields not found");
        }
    }

    @Step("Выбираем производителей {args}")
    public void setManufacturers(String[] args) {
        try {
            // проверяем что на странице загрузилось как минимум 5 ноутбуков
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCTNAME, 5));

            // сохраняем в список все загрузившиеся к текущему моменту ноутбуки
            List<WebElement> list = driver.findElements(PRODUCTNAME);

            // ждем появления на странице чекбоксов с переданными параметрами и чекаем
            for (String arg : args) {
                By manufacturer = By.xpath("//div/span[text()='" + arg + "']");
                wait.until(ExpectedConditions.elementToBeClickable(manufacturer))
                        .click();
            }
            // ожидание обновления страницы
            checkStalenessOf(list);
            System.out.println("setManufacturers ...successful");

        } catch (Exception e) {
            System.out.println("Manufacturers not found");
        }
    }

    @Step("Устанавлием 12 позиций на страницу")
    public void setTwelvePerPage() {
        try {
            list = checkPageIsUpdated(10);

            // ждем появления на странице селектора количества отображаемых элементов
            wait.until(ExpectedConditions.elementToBeClickable(driver
                    .findElement(LISTBOX)))
                    .click();
            // ждем пока появится "Показывать по 12" в выпадающем списке
            wait.until(ExpectedConditions.elementToBeClickable(driver
                    .findElement(TWELVELOCATOR)))
                    .click();
            // ожидание обновления страницы
            checkStalenessOf(list);
            list.clear();
            list = checkPageIsUpdated(10);
            Assertions.assertEquals(12, list.size(), "Actual list size is" + list.size());
        } catch (Exception e) {
            System.out.println("setTwelvePerPage failed");
        }
    }

    @Step("Выбираем первый элемент из списка")
    public String selectFirstElement() {
        String firstEl = "";
        try {
            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions
                    .numberOfElementsToBeMoreThan(PRODUCTNAME, 5));

            firstEl = driver.findElement(PRODUCTNAME).getText();
            System.out.println("firstEl = " + firstEl);
            System.out.println("selectFirstElement ...successful");

        } catch (Exception e) {
            System.out.println("selectFirstElement failed");
        }
        return firstEl;
    }

    @Step("Проверка на совпадение первого элемента списка")
    public void assertFirstElement(String element) {

        try {
            driver.findElement(SEARCHFIELD)
                    .sendKeys(element + "\n");
            String secondFirstEl = wait.until(ExpectedConditions
                    .presenceOfElementLocated(PRODUCTNAME))
                    .getText();

            System.out.println("secondFirstEl = " + secondFirstEl);

            Assertions.assertEquals(element, secondFirstEl, "Elements are not equal");
            System.out.println("assertFirstElement ...successful");

        } catch (Exception e) {
            System.out.println("assertFirstElement failed");
        }
    }

    public void checkStalenessOf(List<WebElement> list) {

        int count = 0;
        // ждем пока вебэлементы(ноутбуки) из сохраненного списка больше не отображатются
        for (WebElement el : list) {
            wait.until(ExpectedConditions.stalenessOf(el));
            if (count++ > 10) break;
        }
        System.out.println("checkStalenessOf ...successful");
    }

    public List<WebElement> checkPageIsUpdated(int amount) {
        // проверяем что на странице загрузилось как минимум переданное кол-во ноутбуков
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCTNAME, amount));
        // сохраняем в список все загрузившиеся к текущему моменту ноутбуки
        list = driver.findElements(PRODUCTNAME);
        System.out.println("checkPageIsUpdated ...successful");
        return list;
    }
}
