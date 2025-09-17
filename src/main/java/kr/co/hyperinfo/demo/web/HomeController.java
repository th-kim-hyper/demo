package kr.co.hyperinfo.demo.web;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        log.info("#### HomeController.index ####");
        model.addAttribute("page", "home");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        log.info("#### HomeController.about ####");
        model.addAttribute("page", "about");
        return "about";
    }

    @GetMapping("/pw")
    public String playwright(Model model) {
        log.info("#### HomeController.pw ####");

        String userId = "th.kim";
        String password = "!G!493o18!";

        String domain = "hyperinfo.co.kr";
        // 1. 로그인폼 (GET)
        Map<String, Object> result = new HashMap<>();
        String url = "https://m109.mailplug.com/member/login?host_domain=" + domain + "&cid=" + userId;
        boolean loginSuccess = false;
        String name = "N/A";
        String phone = "N/A";
        String org = "N/A";
        Date dob = null;
        String position = "N/A";
        byte[] img = null;
        String dataUrl = "";

        try (Playwright playwright = Playwright.create();
                Browser browser = playwright.chromium().launch();
                Page page = browser.newPage();) {

            log.info("#### playwright.chromium().launch() and create page ####");

            // 로그인 페이지로 이동
            page.navigate(url);
            // page.waitForLoadState();
            page.waitForTimeout(2000);
            log.info("#### Navigated to login page: {}", url);
            log.info("#### userId: {}, password: {}", userId, password);

            // 로그인 정보 입력
            page.fill("#cid", userId.trim());
            page.fill("#cpw", password.trim());
            page.click("#btnlogin");
            page.waitForLoadState();
            // page.waitForTimeout(2000);
            log.info("#### Login attempted with ID: {}", userId);

            // 로그인 후 프로필 페이지로 이동
            url = "https://gw.mailplug.com/settings/profile";
            page.navigate(url);
            page.waitForFunction("() => window.next && window.next.router");
            // page.waitForTimeout(2000);
            log.info("#### Navigated to profile page: {}", url);

            // 패스워드 입력
            page.fill("input[type='password'][name='profile-password']", password);
            page.focus("#settings > div > div > div > div.flex.flex-col.gap-7 > button");
            page.click("#settings > div > div > div > div.flex.flex-col.gap-7 > button");
            page.waitForFunction("() => window.next && window.next.router");
            // page.waitForTimeout(2000);
            log.info("#### Password entered and settings saved");

            // 프로필 정보 추출
            // ID, 이름, 전화번호, 조직명
            userId = page
                    .textContent("#settings > div > div:nth-child(2) > table > tbody > tr:nth-child(1) > td > span");
            name = page.inputValue(
                    "#settings > div > div:nth-child(2) > table > tbody > tr:nth-child(2) > td > div > label > input");
            phone = page.inputValue(
                    "#settings > div > div:nth-child(3) > table > tbody > tr > td > div > div.relative.inline-flex.items-center.gap-1 > label > input");
            org = page.textContent(
                    "#settings > div > div:nth-child(4) > table > tbody > tr:nth-child(1) > td > div > span");
            String dateString = page.inputValue(
                    "#settings > div > div:nth-child(2) > table > tbody > tr:nth-child(4) > td > div > div > div > label > input");
            if (dateString != null && !dateString.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                dob = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            position = page.textContent(
                    "#settings > div > div:nth-child(4) > table > tbody > tr:nth-child(2) > td > span");

            loginSuccess = true; // 로그인 성공 여부 설정

            img = page.screenshot();
            dataUrl = "data:image/png;base64," +
                    java.util.Base64.getEncoder().encodeToString(img);

            log.info("#### Screenshot taken and encoded to Data URL");

            // 로그인 성공 여부 확인
            result.put("loginSuccess", loginSuccess);
            result.put("image", dataUrl); // 스크린샷 데이터 URL
            result.put("userId", userId);
            result.put("password", password);
            result.put("name", name);
            result.put("phone", phone);
            result.put("org", org);
            result.put("dob", dob);
            result.put("position", position);
        }

        model.addAttribute("result", result);
        return "pw";
    }

}
