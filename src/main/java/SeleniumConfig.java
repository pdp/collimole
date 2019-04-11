import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class SeleniumConfig {

    public SeleniumConfig() {

    }

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", ClassLoader.getSystemResource("chromedriver.exe").getPath());
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.collishop.be/e/nl/cs");

        WebElement element = driver.findElement(By.id("SimpleSearchForm_SearchTerm"));
        element.sendKeys("GC722D16"); // Grill: code A425 is geldig
        element.sendKeys(Keys.RETURN);

        driver.findElement(By.id("add2CartBtn")).click();

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("GotoCartButton2")));
        driver.findElement(By.id("GotoCartButton2")).click();

        driver.findElement(By.linkText("Vul je promotiecode in")).click();

        List<String> validPromoCodes = new ArrayList<String>();

        for(int i = 0; i < 26; i++){
            for(int j = 0; j < 1000; j++) {
                String promoCode = ((char)(65 + i)) + String.format("%03d", j);
                element = driver.findElement(By.id("promoCode"));
                element.sendKeys(promoCode);
                element.sendKeys(Keys.RETURN);
                Thread.sleep(400);

                boolean promoCodeLinkIsPresent = driver.findElements(By.xpath("//div[contains(text(), '" + promoCode + "')]")).size() > 0;

                if(promoCodeLinkIsPresent){
                    validPromoCodes.add(promoCode);
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Vul je promotiecode in")));
                    driver.findElement(By.linkText("Vul je promotiecode in")).click();
                    continue;
                } else {
                    continue;
                }
            }
        }

        validPromoCodes.forEach((promoCode) -> System.out.println("Valid promocode: " + promoCode));



    }
}

