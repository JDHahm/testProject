import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Headline {

    /*
    자비스앤빌런스(삼쩜삼) 4번 과제
     */


    public static ChromeDriver driver;

    public static WebElement searchBar;

    public static WebElement newsButton;

    public static ArrayList<WebElement> newsGroupList = new ArrayList<>();

    public static WebElement headLine; // 기사 제목


    public static ArrayList<WebElement> pageNumberList = new ArrayList<>(); // 네이버 기사 페이지 1, 2, 3... 을 담기 위한 배열

    public static List<String> dataLines = new ArrayList<>(); // 기사 제목과 링크를 담기 위한 배열



    public static void main(String[] args) {
        setUp();
        getHeadline("chatGPT",10); // 페이지 2개 긁어오기
        exportCSV(dataLines);
    }



    public static void setUp () {
        // 크롬 웹드라이버 셋업 환경 설정
        System.setProperty("webdriver.chrome.driver", "/Users/jd/Desktop/chromedriver"); // 드라이버 경로 수정 필요


        // 크롬버전 111로 올라가면서 WARNING: Invalid Status code=403 text=Forbidden 에러 처리
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver();
        driver.get("https://naver.com/");

    }

    public static void getHeadline(String searchWord, int numberOfPage) {
        //파라미터로 받은 searchWord를 네이버에서 검색하고,
        // 뉴스 -> 최신순 탭으로가서 numberOfPage 만큼의 갯수만큼 헤드라인 긁어오기 (cf. 5를 입력시 최신순으로 5페이지까지 뉴스를 헤드라인을 전부 긁어옴)
        driver.manage().window().maximize();

        //검색바 클릭
        searchBar = driver.findElement(By.id("query"));
        searchBar.click();

        //키보드 입력
        Actions action = new Actions(driver);
        action.sendKeys(searchWord)
                .sendKeys(Keys.ENTER)
                .perform();

        //뉴스탭 접근
        //검색어에 따라 뉴스탭 인덱스가 바뀜 (노출 메뉴는 총 10개 고정)
        // "#lnb > div.lnb_group > div > ul > li:nth-child(1) > a" -> 첫번째 탭
        for (int i = 1; i <= 10; i++ ) {
            // 노출 메뉴가 10개 고정이므로, 10으로 픽스
            newsButton = driver.findElement(By.cssSelector("#lnb > div.lnb_group > div > ul > li:nth-child("+i+") > a"));
            if (newsButton.getText().equals("뉴스")) {
                break;
            }
        }
        newsButton.click();

        //최신 순으로 정렬
        WebElement recentButton = driver.findElement(By.cssSelector("#snb > div.api_group_option_filter._search_option_simple_wrap > div > div.option_area.type_sort > a:nth-child(2)"));
        recentButton.click();


        // 페이지에 노출되는 모든 뉴스들을 arraylist에 저장
        newsGroupList = (ArrayList<WebElement>) driver.findElements(By.cssSelector("#main_pack > section.sc_new.sp_nnews._prs_nws > div > div.group_news > ul > li"));
        pageNumberList = (ArrayList<WebElement>) driver.findElements(By.cssSelector("#main_pack > div.api_sc_page_wrap > div > div > a"));
        JavascriptExecutor js = (JavascriptExecutor) driver;


        //test code : 페이지넘버 잘 출력되는 지 확인
//        for (int i = 2; i <= 10; i++) {
////            WebElement test = driver.findElement(By.xpath("//*[@id=\"main_pack\"]/div[2]/div/div/a["+i+"]"));
////            test.click();
//            headLine = driver.findElement(By.xpath("//*[@id=\"sp_nws1\"]/div/div/a"));
//            String name = headLine.getAttribute("href");
//            System.out.println(name);
//
//        }
        int plusIndex = 0; // 1페이지 10개, 2페이지 10개....
        for(int pageIndex = 1; pageIndex <= numberOfPage; pageIndex++) {

            // 페이지 넘버 pageNumber 1,2,3,4....를 돌면서 기사 헤드라인 긁어오기
            WebElement pageNumber = driver.findElement(By.xpath("//*[@id=\"main_pack\"]/div[2]/div/div/a["+pageIndex+"]"));

            // 5번째 페이지 이상으로 가면 pageIndex는 고정
            if (pageIndex >= 6) {
                pageNumber =  driver.findElement(By.xpath("//*[@id=\"main_pack\"]/div[2]/div/div/a[6]\n"));
            }

            pageNumber.click();

            for (int i =1; i <= newsGroupList.size(); i++) {
                //newsGroupList의 사이즈만큼 노출된 뉴스 갯수를 체크하고, 인덱스를 체크하여 각각의 헤드라인의 getText()를 받아옴.
                headLine = driver.findElement(By.cssSelector("#sp_nws"+(i+plusIndex)+" > div > div > a")); // 뉴스인덱스번호 추가 plusIndex
                String link = headLine.getAttribute("href"); // 헤드라인에 해당하는 링크

                System.out.println(headLine.getText() + " / " + link);
                dataLines.add(headLine.getText() + " / " + link); // 헤드라인 / 링크로 추가
            }

            //js -> 스크롤 0부터 최대길이만큼 내리기
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

            //구분선
            System.out.println("------------" + " Headlines in Page "+ pageIndex + "------------");

            plusIndex += 10;

        }

        driver.close(); // 브라우져 종료


        //*[@id="main_pack"]/div[2]/div/div/a[5]
        //*[@id="main_pack"]/div[2]/div/div/a[5]
        //*[@id="main_pack"]/div[2]/div/div/a[6]



    }

    public static void exportCSV(List<String> dataLines) {
        //csv 파일쓰기
        // String type의 List dataLine을 받고, 한줄씩 csv로
        try {

            FileWriter csvWriter = new FileWriter("/Users/jd/Desktop/test.csv"); //csv파일경로/파일이름.csv

            // ArrayList의 값들을 CSV 파일에 쓰기
            for (String element : dataLines) {
                if (element.contains(",")) {
                    csvWriter.append("\"");
                    csvWriter.append(element);
                    csvWriter.append("\"");
                    csvWriter.append("\n");
                }
                csvWriter.append(element);
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();

            //성공
            System.out.println("CSV 파일이 생성되었습니다.");


        } catch (Exception e ){
            //실패
            System.out.println("CSV 파일 생성 중 오류가 발생하였습니다.");
            e.printStackTrace();
        }
    }
}
