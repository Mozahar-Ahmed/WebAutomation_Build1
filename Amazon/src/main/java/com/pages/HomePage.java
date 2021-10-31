package com.pages;

import com.base.testbase.TestBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;



public class HomePage extends TestBase {
private static Logger LOGGER=Logger.getLogger(HomePage.class);
    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchTextBox;
    @FindBy(id = "nav-search-submit-button")
    private WebElement submitButton;

    public void typeOnSearchTextBox() {
        searchTextBox.sendKeys("Macbook");
        submitButton.click();
        LOGGER.info("Macbook typed and search button found active");
    }
@FindBy(id = "nav-cart-count")
    private WebElement cartButton;
    public void clickOnCart(){
        cartButton.click();
    }
}
