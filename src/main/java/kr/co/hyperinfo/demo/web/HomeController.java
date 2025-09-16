package kr.co.hyperinfo.demo.web;

import java.util.regex.Pattern;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

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
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();

            log.info("#### playwright.chromium().launch() ####");

            Page page = browser.newPage();
            page.navigate("https://playwright.dev");

            log.info("#### page.navigate(\"https://playwright.dev\") ####");

            // Expect a title "to contain" a substring.
            assertThat(page).hasTitle(Pattern.compile("Playwright"));

            // create a locator
            Locator getStarted = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Get Started"));

            log.info("#### create a locator ####");

            // Expect an attribute "to be strictly equal" to the value.
            assertThat(getStarted).hasAttribute("href", "/docs/intro");

            // Click the get started link.
            getStarted.click();
            log.info("#### getStarted.click() ####");

            // Expects page to have a heading with the name of Installation.
            assertThat(page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName("Installation"))).isVisible();
        }
        model.addAttribute("page", "pw");
        return "pw";
    }

}
