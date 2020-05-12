package testYandexMarket;


import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class Steps extends WebDriverSettings {
    private String productName = "//h3/a[contains(@title, '')]";
    private List<WebElement> list;

    @Step("Открываем Яндекс")
    public void openYandexPage() {
        driver.get("https://yandex.ru/");
        System.out.println(driver.getTitle());
    }

    @Step("Открываем ЯндексМаркет")
    public void goToYandexMarket() {

        try {
            wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By
                    .xpath("//a[@data-id='market']"))))
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
            WebElement priceFrom = wait.until(ExpectedConditions.elementToBeClickable(By.id("glpricefrom")));
            WebElement priceTo = wait.until(ExpectedConditions.elementToBeClickable(By.id("glpriceto")));

            priceFrom.sendKeys(from);
            priceTo.sendKeys(to + "\n");

            // ждем обновления цены
            wait.until(ExpectedConditions.presenceOfElementLocated(By
                    .xpath("//div[contains(@data-bem, 'priceto\":\"30000')]")));
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
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(productName), 5));
            // сохраняем в список все загрузившиеся к текущему моменту ноутбуки
            List<WebElement> list = driver.findElements(By.xpath(productName));
            // ждем появления на странице чекбоксов с переданными параметрами и чекаем
            for (String arg : args) {
                wait.until(ExpectedConditions
                        .elementToBeClickable(By
                                .xpath("//div/span[text()='" + arg + "']")))
                        .click();
                System.out.println(arg);
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
            WebElement listBox;
            String twelveLocator = "//span[contains(text(), 'Показывать по 12')]";
            // ждем появления на странице селектора количества отображаемых элементов
            wait.until(ExpectedConditions.elementToBeClickable(
                    listBox = driver.findElement(By.xpath("//button[@role='listbox']"))));
            listBox.click();
            // ждем пока появится "Показывать по 12" в выпадающем списке
            wait.until(ExpectedConditions.elementToBeClickable(driver
                    .findElement(By.xpath(twelveLocator))))
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
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By
                    .xpath(productName), 5));

            firstEl = driver.findElement(By.xpath(productName)).getText();
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
            WebDriverWait wait = new WebDriverWait(driver, 15);
            driver.findElement(By.xpath("//*[@id='header-search']"))
                    .sendKeys(element + "\n");
            String secondFirstEl = wait.until(ExpectedConditions
                    .presenceOfElementLocated(By.xpath(productName)))
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
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By
                .xpath(productName), amount));
        // сохраняем в список все загрузившиеся к текущему моменту ноутбуки
        list = driver.findElements(By.xpath(productName));
        System.out.println("checkPageIsUpdated ...successful");
        return list;
    }
}
