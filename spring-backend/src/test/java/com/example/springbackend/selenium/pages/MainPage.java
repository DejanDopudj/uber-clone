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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='suberTitle']")));

        return true;
    }
}
