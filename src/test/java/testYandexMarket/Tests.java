package testYandexMarket;

import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;


public class Tests extends Steps  {

    String computers = "//a/span[text()='Компьютеры']";
    String notebooks = "//a[text()='Ноутбуки']";



    @Test
    @Description(value = "Тестим ноуты")
    public void test12() throws InterruptedException {
        driver.get("https://market.yandex.ru/");
        selectChapter(computers);
        selectChapter(notebooks);
        setTwelvePerPage();
        Thread.sleep(3000);
    }


    @Test
    @Description(value = "Полный цикл тестов")
    public void testYandexPage()  {
        openYandexPage();
        goToYandexMarket();
        selectChapter(computers);
        selectChapter(notebooks);
        setPrice("10000", "30000");
        setManufacturers(new String[]{"HP", "Lenovo"});
        setTwelvePerPage();
        String el1 = selectFirstElement();
        assertFirstElement(el1);

    }
}
