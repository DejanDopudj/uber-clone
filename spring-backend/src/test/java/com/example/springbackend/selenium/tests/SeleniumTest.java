package com.example.springbackend.selenium.tests;

import com.example.springbackend.selenium.pages.Login.LoginPage;
import com.example.springbackend.selenium.pages.MainPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Bulevar Patrijarha Pavla 125 Novi Sad");
        mainPage.fillDestinationPoint("Bulevar Patrijarha Pavla 55 Novi Sad");
        mainPage.orderRide();


        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        WebDriver driver2 = new ChromeDriver(options);


        LoginPage homePage2 = new LoginPage(driver2);
        assertTrue(homePage2.isPageOpened());
        homePage2.login("driver1@noemail.com","cascaded");
        MainPage mainPage2 = new MainPage(driver2);
        assertTrue(mainPage2.isPageOpened());
        mainPage2.acceptRide();

    }
}
