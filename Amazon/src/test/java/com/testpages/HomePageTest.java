package com.testpages;

import com.pages.HomePage;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import java.util.logging.Logger;

public class HomePageTest extends HomePage {
    @Test
    public void userBeingAbleToSearchItem() {
      HomePage homePage=PageFactory.initElements(driver, HomePage.class);
      homePage.typeOnSearchTextBox();
      }
      @Test
      public void userBeingAbleToClickOnCartButton(){
        HomePage homePage= PageFactory.initElements(driver,HomePage.class);
        homePage.clickOnCart();

      }
}