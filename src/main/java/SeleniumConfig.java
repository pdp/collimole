import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class SeleniumConfig {

    public SeleniumConfig() {

    }

    public static void main(String[] args) throws InterruptedException {
        List<String> validPromoCodes = new ArrayList<String>();
        List<String> invalidPromoCodes = new ArrayList<String>();
        try {
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

            for (int i = 0; i < 26; i++) {
                if(i > 0) {
                    validPromoCodes.forEach((promoCode) -> System.out.println("Valid promocode: " + promoCode));
                }
                for (int j = 83; j < 1000; j++) {
                    String promoCode = ((char) (65 + i)) + String.format("%03d", j);
                    submitPromoCode(driver, promoCode);

                    List<WebElement> elements = driver.findElements(By.xpath("//div[contains(text(), '(" + promoCode + "')]"));

                    if (elements.size() > 0) {
                        handleValidPromoCode(validPromoCodes, driver, wait, elements);
                        continue;
                    } else {
                        handleErrorMessage(promoCode, invalidPromoCodes, driver);
                        continue;
                    }
                }
            }
        }catch (Exception ex) {
            System.out.println("Entering the catch " + ex.getMessage());
        }
        validPromoCodes.forEach((promoCode) -> System.out.println("Valid promocode: " + promoCode));

        invalidPromoCodes.forEach((promoCode) -> System.out.println("Invalid promocode: " + promoCode));
    }

    private static void submitPromoCode(WebDriver driver, String promoCode) throws InterruptedException {
        WebElement element;
        System.out.println("Testing promocode: " + promoCode);
//                    wait.until(ExpectedConditions.)
        element = driver.findElement(By.id("promoCode"));
        element.sendKeys(promoCode);
        element.sendKeys(Keys.RETURN);
        Thread.sleep(200);
    }


    private static void handleValidPromoCode(List<String> validPromoCodes, WebDriver driver, WebDriverWait wait, List<WebElement> elements) {
        String validPromoCode = elements.get(0).getText();
        validPromoCodes.add(validPromoCode);
        System.out.println("Valid promocodes: " + validPromoCode);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("deletePromo")));
        driver.findElement(By.className("deletePromo")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Vul je promotiecode in")));
        driver.findElement(By.linkText("Vul je promotiecode in")).click();
    }

    private static void handleErrorMessage(String promoCode, List<String> invalidPromoCodes, WebDriver driver) {
        List<WebElement> promoErrorDiv = driver.findElements(By.id("promoErrorDiv"));
        if(promoErrorDiv.size() > 0) {
            String errorMessage = promoErrorDiv.get(0).getText();
            if(errorMessage.contains(promoCode)) {
                String invalidCodes = errorMessage.substring(errorMessage.indexOf("'") + 1, errorMessage.lastIndexOf("'"));
                if (invalidCodes.length() > 4) {
                    invalidPromoCodes.addAll(parseErrorMessage(invalidCodes));
                }
            }
        }
    }

    private static List<String> parseErrorMessage(String errorMessage) {
        int size = 4;
        List<String> invalidPromoCodes = new ArrayList<>();

        int length = errorMessage.length();
        for (int i = 0; i < length; i += size) {
            invalidPromoCodes.add(errorMessage.substring(i, Math.min(length, i + size)));
        }
        return invalidPromoCodes;
    }
}

