import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;

public class NaverPractice {

    //연습을 위한

    public static ChromeDriver driver;

    public static void setUp() {
        // 환경 설정
        System.setProperty("webdriver.chrome.driver", "/Users/jd/Desktop/chromedriver");


        // 크롬버전 111로 올라가면서 WARNING: Invalid Status code=403 text=Forbidden 에러 처리
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver();
        driver.get("https://datalab.naver.com/");
    }


    public static void main(String[] args) {
        setUp();
        navigateMenu();
    }

    public static void navigateMenu() {


        WebElement menuSelector = driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div[3]/div[1]"));

        ArrayList<WebElement> menuCategory = new ArrayList<>();

        // 드롭박스 메뉴들
        menuCategory = (ArrayList<WebElement>) driver.findElements(By.cssSelector("#content > div.spot.section_keyword > div.section.main_tab_opt > div.select.depth._dropdown > ul > li"));

//        System.out.println(menuCategory); //test code

//        //test code
//        for (int i = 1; i < menuCategory.size(); i ++) {
//            System.out.println(menuCategory.get(i).getText());
//        }

        //*[@id="content"]/div[1]/div[4]/div/div[1]/div/div
//        #content > div.spot.section_keyword > div.home_section.active > div > div.keyword_carousel > div > div
        //#content > div.spot.section_keyword > div.home_section.active > div > div.keyword_carousel > div > div
//        #content > div.spot.section_keyword > div.home_section.active > div > div.keyword_carousel > div > div > div:nth-child(1)
        //*[@id="content"]/div[1]/div[4]/div/div[1]/div/div/div[1]

        ArrayList<WebDriver> carousel_area = new ArrayList<>();
        carousel_area = (ArrayList<WebDriver>) driver.findElement(By.cssSelector("#content > div.spot.section_keyword > div.home_section.active > div > div.keyword_carousel > div > div"));
        System.out.println(carousel_area.size());
    }
}
