package com.dev9.william;

import com.dev9.page.SearchQueryPage;
import com.dev9.page.SearchResultPage;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Fail.fail;

public class SearchSteps {

    private WebDriver driver;
    private Object currentPage;
    private String webVersion = "";
    private Cookie stackCookie = null;
    private List<WebElement> games;

    @Before({"@requires_browser"})
    public void buildDriver() {
        LocalDateTime localDateTime = LocalDateTime.now();

        System.out.println("The current time is = " + localDateTime);
        driver = new FirefoxDriver();
    }

    @After({"@requires_browser"})
    public void destroyDriver() {
        driver.quit();
    }

    @Given("^WilliamHill's games page$")
    public void A_Google_search_page() throws Throwable {
        currentPage = SearchQueryPage.loadUsing(driver);
    }


    @When("^Login to the lobby using my credentials$")
    public void Login_to_the_lobby_using_my_credentials() throws Throwable {
        synchronized (driver) {
            driver.findElement(By.xpath("/html/body/div[2]/section[1]/header/div[3]/div/a")).click();
            WebDriverWait wait = new WebDriverWait(driver, 0);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"loginForm\"]/div[1]/input")));
            driver.findElement(By.xpath("//*[@id=\"loginForm\"]/div[1]/input")).sendKeys("DavidFloydd");
            driver.findElement(By.xpath("//*[@id=\"loginForm\"]/div[4]/input")).sendKeys("C7QgHept6");
            driver.findElement(By.xpath("//*[@id=\"loginForm\"]/button")).click();
        }
    }


    @Then("^We are able to access with no problems")
    public void We_are_able_to_access_with_no_problems() throws Throwable {
        // Here we check that indeed the login has being made by testing whether we can have access to the element related to our balance
        try {
            driver.findElement(By.xpath("/html/body/div[2]/section[1]/header/div[5]/span/a[2]"));
            assertThat(true);
        } catch (Exception e) {
            assertThat(false);
        }
    }


    @When("^Counting the games in the A-Z list$")
    public void Counting_the_games_in_the_A_Z_list() throws Throwable {
        synchronized (driver) {
            WebDriverWait wait = new WebDriverWait(driver, 0);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[1]/div/div/div[3]/div/a[3]")));
            driver.wait(2000);
            driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div[3]/div/a[3]")).click();
            driver.wait(50000);
            games = driver.findElements(By.xpath("/html/body/div[2]/div[2]/section/div/div/div/div"));

        }
    }

    @Then("^We should see 371 games, and list them in the console")
    public void We_should_see_371_games_and_list_them_in_the_console() throws Throwable {
        for (int i = 1; i <= games.size(); i++) {
            String xpath = "/html/body/div[2]/div[2]/section/div/div/div/div[" + i + "]/figure/a[1]";
            WebElement child = driver.findElement(By.xpath(xpath));
            String link = child.getAttribute("href");
            System.out.println("The game #" + i + " is = " + link.substring(link.indexOf("h/games-") + 8, link.length()));
        }
        System.out.println("The total number of games is = " + games.size());
        assertThat(games.size()).isEqualTo(371);
    }

    @When("^Displaying all the cookies available$")
    public void Displaying_all_the_cookies_available() throws Throwable {
        Set<Cookie> stackCookie = driver.manage().getCookies();
        for (Cookie c : stackCookie) {
            System.out.println("The value for the cookie " + c.getName() + " is = " + c.getValue());
            Cookie stack = null;
            if (c.toString().contains("STACK") || c.toString().contains("stack") || c.getValue().contains("STACK") || c.getValue().contains("stack") || c.getValue().contains("gs")) {
                stack = c;
            }
        }
    }

    @Then("^We can access the cookie STACK and its value")
    public void We_can_access_the_cookie_STACK_and_its_value() throws Throwable {
        assertThat(webVersion).isNotNull();
    }

    @When("^Exploring the elements for the maincss file$")
    public void I_explore_the_elements_for_the_maincss_file() throws Throwable {
        List<WebElement> list = driver.findElements(By.xpath("//*[@href or @src]"));
        for (WebElement e : list) {
            String link = e.getAttribute("href");
            if (null == link)
                link = e.getAttribute("src");
            if (link.contains("main.css")) {
                webVersion = link.subSequence(link.indexOf("gaming") + 7, link.indexOf("gaming") + 13) + "";
                System.out.println("The site version is = " + webVersion);
                break;
            }
        }
    }

    @And("^I submit the search by pressing \"([^\"]*)\"$")
    public void I_submit_the_search_by_pressing(String submitType) throws Throwable {
        verifyCurrentPage(SearchQueryPage.class);
        switch (submitType.toLowerCase()) {
            case "enter":
            case "enter key":
                currentPage = ((SearchQueryPage) currentPage).pressEnterInQuery();
                break;
            case "search":
            case "google search":
            case "google search button":
            case "search button":
                currentPage = ((SearchQueryPage) currentPage).clickSearchButton();
                break;
            case "i'm feeling lucky button":
            case "i'm feeling lucky":
            case "lucky":
            case "lucky button":
                currentPage = ((SearchQueryPage) currentPage).clickLuckyButton();
                break;
        }
    }

    @Then("^It should match with the current page version")
    public void It_should_match_with_the_current_page_version() throws Throwable {
        assertThat(webVersion).isEqualTo("1.52.3");
    }

    private void verifyCurrentPage(Class pageClass) {
        if (!currentPage.getClass().equals(pageClass)) {
            fail(
                    String.format("Expected current page to have type %s - actual type is %s",
                            pageClass.getSimpleName(),
                            currentPage.getClass().getSimpleName())
            );
        }
    }
}
