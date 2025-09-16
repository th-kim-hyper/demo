package kr.co.hyperinfo.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("page", "home");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("page", "about");
        return "about";
    }

}
