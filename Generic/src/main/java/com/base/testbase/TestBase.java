package com.base.testbase;

import com.base.report.ExtentManager;
import com.base.report.ExtentTestManager;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class TestBase {
    public static WebDriver driver;
    public static ExtentReports extent;
    public static String sauceUserName;
    public static String sauceKey;
    public static String browserstackUserName="mozaharhossainah_nBgYdI";
    public static String browserstackKey="jUiyy55kK1Hxjd66MW9L";
    public static String SAUCE_URL = "https://" + sauceUserName + ":" + sauceKey + "@ondemand.saucelabs.com:80/wd/hub";
    public static String BROWSERSTACK_URL = "https://" + browserstackUserName + ":" + browserstackKey + "@hub-cloud.browserstack.com/wd/hub";
   private static Logger LOGGER=Logger.getLogger(TestBase.class);

    @Parameters({"platform", "url", "browser", "browserVersion", "cloud", "envName"})
    @BeforeMethod
    public static WebDriver setupDriver(String platform, String url, String browser, String browserVersion,
                                        boolean cloud, String envName) throws MalformedURLException {
        if (cloud) {
            driver = getCloudDriver(platform, browser, browserVersion, envName);
        } else {
            driver = getLocalDriver (platform, browser);
        }
        driver.get(url);
        return driver;
    }

    public static WebDriver getCloudDriver(String platform, String browser, String browserVersion, String envName) throws MalformedURLException {
        DesiredCapabilities desCap = new DesiredCapabilities();
        desCap.setCapability("name", "Cloud Execution");
        desCap.setCapability("browser", browser);
        desCap.setCapability("browserVersion", browserVersion);
        desCap.setCapability("os", platform);
        desCap.setCapability("os version", "10");
        desCap.setCapability("resolution", "2048x1536");
        if (envName.equalsIgnoreCase("saucelabs")) {
            driver = new RemoteWebDriver(new URL(SAUCE_URL), desCap);
        } else if (envName.equalsIgnoreCase("browserstack")) {
            driver = new RemoteWebDriver(new URL(BROWSERSTACK_URL), desCap);
        }
        return driver;
    }

    public static WebDriver getLocalDriver(String platform, String browser) {
        if (platform.equalsIgnoreCase("windows") && browser.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver", "..\\Generic\\src\\main\\resources\\chromedriver.exe");
            driver = new ChromeDriver();
        } else if (platform.equalsIgnoreCase("windows") && browser.equalsIgnoreCase("firefox")) {
            System.setProperty("webdriver.gecko.driver", "..\\generic\\src\\main\\resources\\geckodriver.exe");
            driver = new FirefoxDriver();
        } else if (platform.equalsIgnoreCase("mac") && browser.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver", "..\\generic\\src\\main\\resources\\chromedriver");
            driver = new ChromeDriver();
        } else if (platform.equalsIgnoreCase("mac") && browser.equalsIgnoreCase("firefox")) {
            System.setProperty("webdriver.gecko.driver", "..\\generic\\src\\main\\resources\\geckodriver");
            driver = new FirefoxDriver();
        }
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }
    //screenshot
    public void captureScreenshot(WebDriver driver, String screenshotName) {
        DateFormat df = new SimpleDateFormat("HH_mm_ss");
        Date date = new Date();
        df.format(date);
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(file, new File(System.getProperty("user.dir") + "/screenshots/" + screenshotName + " " + df.format(date) + ".jpg"));
            System.out.println("Screenshot captured");
        } catch (Exception e) {
            System.out.println("Exception while taking screenshot " + e.getMessage());
        }
    }
    //reporting starts
    @BeforeSuite
    public void extentSetup(ITestContext context) {
        ExtentManager.setOutputDirectory(context);
        extent = ExtentManager.getInstance();
    }

    @BeforeMethod
    public void startExtent(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        ExtentTestManager.startTest(method.getName());
        ExtentTestManager.getTest().assignCategory(className);
    }

    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    @AfterMethod
    public void afterEachTestMethod(ITestResult result) {
        ExtentTestManager.getTest().setStartedTime(getTime(result.getStartMillis()));
        ExtentTestManager.getTest().setEndedTime(getTime(result.getEndMillis()));
        for (String group : result.getMethod().getGroups()) {
            ExtentTestManager.getTest().assignCategory(group);
        }

        if (result.getStatus() == 1) {
            ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed");
        } else if (result.getStatus() == 2) {
            ExtentTestManager.getTest().log(LogStatus.FAIL, getStackTrace(result.getThrowable()));
        } else if (result.getStatus() == 3) {
            ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped");
        }

        ExtentTestManager.endTest();
        extent.flush();
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenshot(driver, result.getName());
        }
    }

    public Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    @AfterSuite
    public void generateReport() {
        extent.close();
    }
    //reporting finish

    public static void sleepFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
@AfterMethod
    public static void cleanUp() {
        driver.close();
        driver.quit();
        LOGGER.info("Browser successfully closed");
    }

}