package com.example.springbackend.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPage {
    WebDriver driver;
    @FindBy(xpath = "//*[@id='map']")
    WebElement map;
    @FindBy(xpath = "//*[@id='side-panel-icon']")
    WebElement sidePanelIcon;
    @FindBy(xpath = "//input[@placeholder='Add the starting point']")
    WebElement startingPoint;

    @FindBy(xpath = "//button[contains(text(),'ORDER RIDE')]")
    WebElement orderButton;

    String destinationXpath = "//input[@placeholder='Add a destination']";

    public MainPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isPageOpened(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='map']")));

        return true;
    }

    public void fillStartingPoint(String address){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(startingPoint)).click();
        startingPoint.sendKeys(address);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-icon='plus']"))).click();
    }

    public void fillDestinationPoint(String address){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(destinationXpath))).click();
        driver.findElement(By.xpath(destinationXpath)).sendKeys(address);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-icon='plus']"))).click();
    }

    public void openSidePanel() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='side-panel-icon']"))).click();
    }

    public void orderRide(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(orderButton)).click();
    }

    public void acceptRide(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(., 'BEGIN RIDE')]"))).click();
    }
    public void rejectRide(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(., 'REJECT  RIDE')]"))).click();
    }
}
