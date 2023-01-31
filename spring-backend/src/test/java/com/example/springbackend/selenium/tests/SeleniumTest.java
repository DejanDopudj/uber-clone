package com.example.springbackend.selenium.tests;

import com.example.springbackend.selenium.pages.Login.LoginPage;
import com.example.springbackend.selenium.pages.MainPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumTest {

    WebDriver driver;

    @BeforeAll()
    public void setupAll() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    /*@AfterAll()
    public void setupQuitAll() {
        driver.quit();
    }*/

    @Test()
    public void mainTest() {
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login();

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Bulevar Patrijarha Pavla 125 Novi Sad");
        mainPage.fillDestinationPoint("Bulevar Patrijarha Pavla 33 Novi Sad");
        mainPage.orderRide();
    }
}
