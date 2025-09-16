package kr.co.hyperinfo.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

}
