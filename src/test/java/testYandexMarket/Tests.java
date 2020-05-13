package testYandexMarket;

import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;


public class Tests extends Steps {

    private String computersSelector = "//a/span[text()='Компьютеры']";
    private String notebooksSelector = "//a[text()='Ноутбуки']";



    @Test
    @Description(value = "Полный цикл тестов")
    public void testYandexPage() {
        openYandexPage();
        goToYandexMarket();
        selectChapter(computersSelector);
        selectChapter(notebooksSelector);
        setPrice("10000", "30000");
        setManufacturers(new String[]{"HP", "Lenovo"});
        setTwelvePerPage();
        String el1 = selectFirstElement();
        assertFirstElement(el1);

    }
}
