package com.lumatest.test;

import com.lumatest.base.BaseTest;
import com.lumatest.data.TestData;
import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NavigationTest extends BaseTest {
    @Test(
            description = "TC-01 Open Base URL",
            groups = {"Smoke", "Regression"}
    )    @Story("Navigation")
    @Severity(SeverityLevel.BLOCKER)
    @Description("To verify that the base URL and title of the app are correct and as expected.")
    @Link(TestData.BASE_URL)
    public void testOpenBaseURL() {
        Allure.step("Set up expected results.");
        final String expectedURL = TestData.BASE_URL + "/";
        final String expectedTitle = TestData.BASE_URL_TITLE;

        Allure.step("Open base URL.");
        getDriver().get(TestData.BASE_URL);

        Allure.step("Collect actual URL and actual title.");
        final String actualURL = getDriver().getCurrentUrl();
        final String actualTitle = getDriver().getTitle();

        Allure.step("Verify that the actual URL is as expected.");
        Assert.assertEquals(actualURL, expectedURL);
        Allure.step("Verify that the actual title is as expected.");
        Assert.assertEquals(actualTitle, expectedTitle);
    }

    @Test(
            groups = {"Smoke", "Regression"},
            description = "TC-02 Top Menu Navigation",
            dataProvider = "navigationMenuData",
            dataProviderClass = TestData.class
    )    @Story("Navigation")
    @Severity(SeverityLevel.BLOCKER)
    @Description("To verify that top navigation menu functions correctly " +
            "and clicking on menu items navigate to the expected URL with the expected title.")
    @Link(TestData.BASE_URL)
    public void testNavigationMenu(String baseUrl, By navMenu, String expectedUrl, String expectedTitle) {
        Allure.step("Open Base URL");
        getDriver().get(baseUrl);

        Allure.step("Click on the navigation menu" + navMenu.toString());
        getDriver().findElement(navMenu).click();

        Allure.step("Collect actualURL, actualTitle");
        final String actualURL = getDriver().getCurrentUrl();
        final String actualTitle = getDriver().getTitle();

        Allure.step("Verify the URL and title");
        Assert.assertEquals(actualURL, expectedUrl);
        Assert.assertEquals(actualTitle, expectedTitle);
    }
}
