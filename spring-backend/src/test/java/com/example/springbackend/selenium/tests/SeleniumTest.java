package com.example.springbackend.selenium.tests;

import com.example.springbackend.selenium.pages.Login.LoginPage;
import com.example.springbackend.selenium.pages.MainPage;
import com.example.springbackend.selenium.pages.RideRejectionPage;
import org.junit.jupiter.api.AfterAll;
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

    @AfterAll()
    public void setupQuitAll() {
        driver.quit();
    }

    @Test()
    public void mainTest() {
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Bulevar Patrijarha Pavla 36 Novi Sad");
        mainPage.fillDestinationPoint("Kis Ernea 8, Telep Novi Sad");
        mainPage.orderRide();


        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        WebDriver driver2 = new ChromeDriver(options);
        driver2.manage().window().maximize();


        LoginPage homePage2 = new LoginPage(driver2);
        assertTrue(homePage2.isPageOpened());
        homePage2.login("driver1@noemail.com","cascaded");
        MainPage mainPage2 = new MainPage(driver2);
        assertTrue(mainPage2.isPageOpened());
        mainPage2.acceptRide();
        mainPage2.completeRide();
    }


    @Test()
    public void rejectRideTest() {
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Rajka Mamuzica 24 Novi Sad");
        mainPage.fillDestinationPoint("Kis Ernea 8, Telep Novi Sad");
        mainPage.orderRide();


        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver driver2 = new ChromeDriver();
        driver2.manage().window().maximize();


        LoginPage homePage2 = new LoginPage(driver2);
        assertTrue(homePage2.isPageOpened());
        homePage2.login("driver1@noemail.com","cascaded");
        MainPage mainPage2 = new MainPage(driver2);
        assertTrue(mainPage2.isPageOpened());
        mainPage2.rejectRide();

        WebDriver driver3 = new ChromeDriver();
        driver3.manage().window().maximize();
        LoginPage adminLoginPage = new LoginPage(driver3);
        assertTrue(adminLoginPage.isPageOpened());
        adminLoginPage.login("admin1@noemail.com","cascaded");
        MainPage adminMainPage = new MainPage(driver3);
        assertTrue(adminMainPage.isPageOpened());
        adminMainPage.viewRideRejectionRequests();
        RideRejectionPage rideRejectionPage = new RideRejectionPage(driver3);
        rideRejectionPage.acceptFirstRide();

        assertTrue(mainPage.expectMessage("The driver rejected the ride."));
        assertTrue(mainPage2.expectMessage("Your rejection is accepted"));
    }

    @Test()
    public void noAvailableDriversTest() {
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Kraljice Natalije 19 Beograd");
        mainPage.fillDestinationPoint("Mutapova 5-7 Beograd");
        mainPage.orderRide();
        assertTrue(mainPage.expectMessage("Adequate driver not found."));
    }
    @Test()
    public void insufficientFundsTest() {
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Suvoborska 16 Novi Sad");
        mainPage.fillDestinationPoint("Svetogorska 3 Novi Sad");
        mainPage.orderRide();
        assertTrue(mainPage.expectMessage("Insufficient funds."));
    }
    @Test()
    public void minimumDistanceTest() {
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Futoska 10 Novi Sad");
        mainPage.fillDestinationPoint("Futoska 12 Novi Sad");
        mainPage.orderRide();
        assertTrue(mainPage.expectMessage("Minimum ride distance is 0.25km"));
    }
    @Test()
    public void maximumDistanceTest() {
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Suvoborska 16 Novi Sad");
        mainPage.fillDestinationPoint("Momačka 9 Mali Mokri Lug");
        mainPage.orderRide();
        assertTrue(mainPage.expectMessage("Maximum ride distance is 100km"));
    }

    @Test
    public void scheduleRide(){
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Bulevar Patrijarha Pavla 36 Novi Sad");
        mainPage.fillDestinationPoint("Kis Ernea 8, Telep Novi Sad");
        mainPage.fillRideInAdvance(20);
        mainPage.orderRide();

        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        WebDriver driver2 = new ChromeDriver(options);
        driver2.manage().window().maximize();


        LoginPage homePage2 = new LoginPage(driver2);
        assertTrue(homePage2.isPageOpened());
        homePage2.login("driver1@noemail.com","cascaded");
        MainPage mainPage2 = new MainPage(driver2);
        assertTrue(mainPage2.isPageOpened());
        mainPage2.acceptRide();
        mainPage2.completeRide();
    }
    @Test
    public void scheduleRideTooSoon(){
        LoginPage homePage = new LoginPage(driver);
        assertTrue(homePage.isPageOpened());
        homePage.login("passenger1@noemail.com","cascaded");

        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.isPageOpened());
        mainPage.openSidePanel();
        mainPage.fillStartingPoint("Bulevar Patrijarha Pavla 36 Novi Sad");
        mainPage.fillDestinationPoint("Kis Ernea 8, Telep Novi Sad");
        mainPage.fillRideInAdvance(7);
        mainPage.orderRide();
        assertTrue(mainPage.expectMessage("Reservation must be made"));
    }

}
